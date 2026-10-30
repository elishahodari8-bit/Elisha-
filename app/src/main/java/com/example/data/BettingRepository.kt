package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class BettingRepository(
    private val onBetPlaced: (Double) -> Boolean, // returns true if user balance was deducted
    private val onWinOrCashout: (Double, String) -> Unit // credits wallet
) {
    private val _betSlipItems = MutableStateFlow<List<BetSlipItem>>(emptyList())
    val betSlipItems: StateFlow<List<BetSlipItem>> = _betSlipItems.asStateFlow()

    private val _stake = MutableStateFlow(2000.0) // default 2,000 TSh
    val stake: StateFlow<Double> = _stake.asStateFlow()

    private val _placedBets = MutableStateFlow<List<PlacedBet>>(emptyList())
    val placedBets: StateFlow<List<PlacedBet>> = _placedBets.asStateFlow()

    init {
        // Pre-populate with realistic betting history so user immediately sees active & past bets
        _placedBets.value = listOf(
            PlacedBet(
                id = "BET-99410",
                timestamp = System.currentTimeMillis() - 45 * 60 * 1000,
                items = listOf(
                    BetSlipItem("m_fb_1", "Arsenal FC vs Chelsea FC", Sport.FOOTBALL, "1X2", "opt_1", "Arsenal FC", 1.55),
                    BetSlipItem("m_bb_1", "LA Lakers vs GS Warriors", Sport.BASKETBALL, "Moneyline", "opt_lal", "LA Lakers", 2.10)
                ),
                stake = 5000.0,
                totalOdds = 3.25,
                accaBonusPercentage = 7.0,
                potentialReturn = 17387.0,
                status = BetStatus.OPEN,
                cashoutValue = 8800.0 // Ready to cash out for profit!
            ),
            PlacedBet(
                id = "BET-88123",
                timestamp = System.currentTimeMillis() - 24 * 60 * 60 * 1000,
                items = listOf(
                    BetSlipItem("m_fb_2", "Simba SC vs Yanga SC", Sport.FOOTBALL, "Total Goals", "opt_ou_o25", "Over 2.5", 2.05),
                    BetSlipItem("m_tn_1", "Alcaraz vs Sinner", Sport.TENNIS, "Match Winner", "opt_alc", "Carlos Alcaraz", 1.60)
                ),
                stake = 10000.0,
                totalOdds = 3.28,
                accaBonusPercentage = 5.0,
                potentialReturn = 34440.0,
                status = BetStatus.WON,
                cashoutValue = 0.0
            ),
            PlacedBet(
                id = "BET-77211",
                timestamp = System.currentTimeMillis() - 48 * 60 * 60 * 1000,
                items = listOf(
                    BetSlipItem("m_f1_1", "Formula 1 Grand Prix", Sport.FORMULA_1, "Winner", "opt_ham", "Lewis Hamilton", 9.00)
                ),
                stake = 2000.0,
                totalOdds = 9.00,
                accaBonusPercentage = 0.0,
                potentialReturn = 18000.0,
                status = BetStatus.LOST,
                cashoutValue = 0.0
            )
        )
    }

    fun toggleSelection(item: BetSlipItem) {
        val current = _betSlipItems.value.toMutableList()
        val existingIndex = current.indexOfFirst { it.matchId == item.matchId }

        if (existingIndex >= 0) {
            val existing = current[existingIndex]
            if (existing.optionId == item.optionId) {
                // Remove if same option clicked again
                current.removeAt(existingIndex)
            } else {
                // Replace with new option from same match
                current[existingIndex] = item
            }
        } else {
            // Add new match selection
            current.add(item)
        }
        _betSlipItems.value = current
    }

    fun removeSelection(optionId: String) {
        _betSlipItems.value = _betSlipItems.value.filterNot { it.optionId == optionId }
    }

    fun clearSlip() {
        _betSlipItems.value = emptyList()
    }

    fun updateStake(newStake: Double) {
        _stake.value = newStake.coerceAtLeast(500.0)
    }

    fun calculateTotalOdds(): Double {
        if (_betSlipItems.value.isEmpty()) return 1.0
        val product = _betSlipItems.value.fold(1.0) { acc, item -> acc * item.odds }
        return Math.round(product * 100.0) / 100.0
    }

    fun calculateAccaBonusPercentage(): Double {
        val count = _betSlipItems.value.size
        return when {
            count <= 1 -> 0.0
            count == 2 -> 3.0
            count == 3 -> 7.0
            count == 4 -> 12.0
            count == 5 -> 18.0
            count == 6 -> 25.0
            count == 7 -> 35.0
            else -> 50.0 // Super boost for 8+ legs!
        }
    }

    fun calculatePotentialReturn(): Double {
        val raw = _stake.value * calculateTotalOdds()
        val bonus = raw * (calculateAccaBonusPercentage() / 100.0)
        return Math.round((raw + bonus) * 100.0) / 100.0
    }

    fun placeBet(): Result<PlacedBet> {
        val items = _betSlipItems.value
        if (items.isEmpty()) return Result.failure(Exception("Bet slip is empty"))
        val currentStake = _stake.value

        val deducted = onBetPlaced(currentStake)
        if (!deducted) {
            return Result.failure(Exception("Insufficient wallet balance or daily limit exceeded"))
        }

        val totalOdds = calculateTotalOdds()
        val accaBonus = calculateAccaBonusPercentage()
        val potentialReturn = calculatePotentialReturn()
        val newBetId = "BET-" + UUID.randomUUID().toString().substring(0, 6).uppercase()

        val newBet = PlacedBet(
            id = newBetId,
            timestamp = System.currentTimeMillis(),
            items = items,
            stake = currentStake,
            totalOdds = totalOdds,
            accaBonusPercentage = accaBonus,
            potentialReturn = potentialReturn,
            status = BetStatus.OPEN,
            cashoutValue = Math.round(currentStake * 0.9 * 100.0) / 100.0
        )

        _placedBets.value = listOf(newBet) + _placedBets.value
        clearSlip()
        return Result.success(newBet)
    }

    fun cashOut(betId: String): Result<Double> {
        val bets = _placedBets.value.toMutableList()
        val index = bets.indexOfFirst { it.id == betId }
        if (index == -1) return Result.failure(Exception("Bet not found"))

        val bet = bets[index]
        if (bet.status != BetStatus.OPEN) return Result.failure(Exception("Bet cannot be cashed out"))

        val cashoutAmount = bet.cashoutValue
        bets[index] = bet.copy(
            status = BetStatus.CASHED_OUT
        )
        _placedBets.value = bets

        onWinOrCashout(cashoutAmount, "Early Cashout payout for #${bet.id}")
        return Result.success(cashoutAmount)
    }

    fun settleBet(betId: String, isWin: Boolean) {
        val bets = _placedBets.value.toMutableList()
        val index = bets.indexOfFirst { it.id == betId }
        if (index >= 0) {
            val bet = bets[index]
            val newStatus = if (isWin) BetStatus.WON else BetStatus.LOST
            bets[index] = bet.copy(status = newStatus)
            _placedBets.value = bets

            if (isWin) {
                onWinOrCashout(bet.potentialReturn, "Winning payout for #${bet.id}")
            }
        }
    }
}
