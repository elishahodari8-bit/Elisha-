package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class WalletRepository {

    private val _user = MutableStateFlow(UserProfile())
    val user: StateFlow<UserProfile> = _user.asStateFlow()

    private val _transactions = MutableStateFlow<List<TransactionRecord>>(emptyList())
    val transactions: StateFlow<List<TransactionRecord>> = _transactions.asStateFlow()

    private val _promotions = MutableStateFlow<List<Promotion>>(emptyList())
    val promotions: StateFlow<List<Promotion>> = _promotions.asStateFlow()

    init {
        // Initial transaction records for realism
        _transactions.value = listOf(
            TransactionRecord(
                id = "TX-90124",
                type = TransactionType.DEPOSIT,
                method = PaymentMethod.MPESA,
                amount = 50000.0,
                timestamp = System.currentTimeMillis() - 2 * 60 * 60 * 1000,
                reference = "MPESA-9X72K1L",
                status = TransactionStatus.COMPLETED,
                note = "Mobile Money Deposit via M-Pesa"
            ),
            TransactionRecord(
                id = "TX-88401",
                type = TransactionType.BET_WIN,
                method = null,
                amount = 34440.0,
                timestamp = System.currentTimeMillis() - 24 * 60 * 60 * 1000,
                reference = "BET-88123-PAY",
                status = TransactionStatus.COMPLETED,
                note = "Payout for Won Acca Bet"
            ),
            TransactionRecord(
                id = "TX-77312",
                type = TransactionType.DEPOSIT,
                method = PaymentMethod.AIRTEL_MONEY,
                amount = 20000.0,
                timestamp = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000,
                reference = "AIRTEL-4A999B",
                status = TransactionStatus.COMPLETED,
                note = "Mobile Money Deposit via Airtel Money"
            )
        )

        _promotions.value = listOf(
            Promotion(
                id = "p_welcome",
                title = "100% First Deposit Match",
                tag = "WELCOME",
                description = "Double your initial deposit up to 500,000 TSh in Sports Bonus funds. Valid on multi-bets with min odds 1.50 per leg.",
                bonusPercentage = 100,
                maxBonusTsh = 500000.0,
                code = "ELISHA100",
                isClaimed = true
            ),
            Promotion(
                id = "p_acca",
                title = "500% Mega Accumulator Boost",
                tag = "ACCA BOOST",
                description = "Get extra winnings added automatically to your ticket from 3% (2 legs) up to 500% (30+ legs) across all sports!",
                bonusPercentage = 50,
                maxBonusTsh = 2000000.0,
                code = "BOOST500",
                isClaimed = false
            ),
            Promotion(
                id = "p_friday",
                title = "Free Bet Friday Bonanza",
                tag = "WEEKLY",
                description = "Deposit 10,000 TSh or more every Friday to receive a guaranteed 5,000 TSh Free Bet voucher on Premier League action.",
                bonusPercentage = 50,
                maxBonusTsh = 50000.0,
                code = "FREEFRIDAY",
                isClaimed = false
            )
        )
    }

    fun deductForBet(stake: Double): Boolean {
        val currentUser = _user.value
        if (currentUser.selfExclusionActive) {
            return false
        }
        if (stake > currentUser.maxSingleBetLimit) {
            return false
        }
        if (currentUser.realBalance >= stake) {
            _user.value = currentUser.copy(
                realBalance = currentUser.realBalance - stake
            )
            addTransaction(
                TransactionRecord(
                    id = "TX-" + UUID.randomUUID().toString().substring(0, 7).uppercase(),
                    type = TransactionType.BET_STAKE,
                    method = null,
                    amount = stake,
                    timestamp = System.currentTimeMillis(),
                    reference = "WAGER-" + System.currentTimeMillis().toString().takeLast(6),
                    status = TransactionStatus.COMPLETED,
                    note = "Sports Bet Wager"
                )
            )
            return true
        }
        return false
    }

    fun creditBalance(amount: Double, referenceNote: String) {
        val currentUser = _user.value
        _user.value = currentUser.copy(
            realBalance = currentUser.realBalance + amount
        )
        addTransaction(
            TransactionRecord(
                id = "TX-" + UUID.randomUUID().toString().substring(0, 7).uppercase(),
                type = TransactionType.BET_WIN,
                method = null,
                amount = amount,
                timestamp = System.currentTimeMillis(),
                reference = "WIN-" + System.currentTimeMillis().toString().takeLast(6),
                status = TransactionStatus.COMPLETED,
                note = referenceNote
            )
        )
    }

    fun deposit(
        method: PaymentMethod,
        phoneOrAccount: String,
        amount: Double
    ): Result<TransactionRecord> {
        val currentUser = _user.value
        if (currentUser.selfExclusionActive) {
            return Result.failure(Exception("Account is currently in self-exclusion cool-off period."))
        }
        if (currentUser.usedDepositToday + amount > currentUser.dailyDepositLimit) {
            return Result.failure(Exception("Deposit exceeds your daily responsible gambling limit of ${currentUser.dailyDepositLimit} TSh."))
        }
        if (amount < method.minDeposit || amount > method.maxDeposit) {
            return Result.failure(Exception("Amount must be between ${method.minDeposit} and ${method.maxDeposit} TSh for ${method.title}."))
        }

        val refCode = method.id.uppercase().take(4) + "-" + UUID.randomUUID().toString().substring(0, 6).uppercase()
        val tx = TransactionRecord(
            id = "TX-" + UUID.randomUUID().toString().substring(0, 7).uppercase(),
            type = TransactionType.DEPOSIT,
            method = method,
            amount = amount,
            timestamp = System.currentTimeMillis(),
            reference = refCode,
            status = TransactionStatus.COMPLETED,
            note = "Deposit via ${method.title} ($phoneOrAccount)"
        )

        _user.value = currentUser.copy(
            realBalance = currentUser.realBalance + amount,
            usedDepositToday = currentUser.usedDepositToday + amount
        )
        addTransaction(tx)
        return Result.success(tx)
    }

    fun requestWithdrawal(
        method: PaymentMethod,
        phoneOrAccount: String,
        amount: Double
    ): Result<TransactionRecord> {
        val currentUser = _user.value
        if (currentUser.realBalance < amount) {
            return Result.failure(Exception("Insufficient withdrawable balance."))
        }
        if (currentUser.kycStatus != KycStatus.VERIFIED) {
            return Result.failure(Exception("KYC identity verification required prior to processing withdrawals."))
        }

        val tx = TransactionRecord(
            id = "TX-" + UUID.randomUUID().toString().substring(0, 7).uppercase(),
            type = TransactionType.WITHDRAWAL,
            method = method,
            amount = amount,
            timestamp = System.currentTimeMillis(),
            reference = "WD-" + UUID.randomUUID().toString().substring(0, 6).uppercase(),
            status = if (amount <= 50000.0) TransactionStatus.COMPLETED else TransactionStatus.PENDING_APPROVAL,
            note = "Withdrawal to ${method.title} ($phoneOrAccount)"
        )

        _user.value = currentUser.copy(
            realBalance = currentUser.realBalance - amount
        )
        addTransaction(tx)
        return Result.success(tx)
    }

    fun submitKyc(docType: String, docNumber: String): Result<String> {
        val currentUser = _user.value
        _user.value = currentUser.copy(
            kycStatus = KycStatus.VERIFIED,
            kycDocumentType = docType,
            kycDocumentNumber = docNumber
        )
        return Result.success("KYC identity verification approved. Real money safeguards enabled.")
    }

    fun updateDepositLimit(newLimit: Double) {
        val currentUser = _user.value
        _user.value = currentUser.copy(
            dailyDepositLimit = newLimit
        )
    }

    fun toggleSelfExclusion(enable: Boolean) {
        val currentUser = _user.value
        _user.value = currentUser.copy(
            selfExclusionActive = enable
        )
    }

    fun claimPromotion(promoId: String): Result<Double> {
        val currentPromos = _promotions.value.toMutableList()
        val index = currentPromos.indexOfFirst { it.id == promoId }
        if (index == -1) return Result.failure(Exception("Promotion not found"))

        val promo = currentPromos[index]
        if (promo.isClaimed) return Result.failure(Exception("Promotion already claimed"))

        val bonusAward = (promo.maxBonusTsh * 0.25).coerceAtLeast(10000.0)
        currentPromos[index] = promo.copy(isClaimed = true)
        _promotions.value = currentPromos

        _user.value = _user.value.copy(
            bonusBalance = _user.value.bonusBalance + bonusAward
        )

        addTransaction(
            TransactionRecord(
                id = "TX-" + UUID.randomUUID().toString().substring(0, 7).uppercase(),
                type = TransactionType.REFERRAL_COMMISSION,
                method = null,
                amount = bonusAward,
                timestamp = System.currentTimeMillis(),
                reference = "PROMO-" + promo.code,
                status = TransactionStatus.COMPLETED,
                note = "Claimed promo bonus: ${promo.title}"
            )
        )
        return Result.success(bonusAward)
    }

    private fun addTransaction(tx: TransactionRecord) {
        _transactions.value = listOf(tx) + _transactions.value
    }
}
