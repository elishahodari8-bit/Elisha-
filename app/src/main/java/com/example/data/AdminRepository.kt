package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class AdminRepository(
    private val sportsRepository: SportsRepository,
    private val bettingRepository: BettingRepository,
    private val walletRepository: WalletRepository
) {
    private val _platformMetrics = MutableStateFlow(
        PlatformMetrics(
            totalUsers = 14280,
            activeBettors = 3190,
            totalDeposits = 184500000.0, // 184.5M TSh
            totalWithdrawals = 92300000.0, // 92.3M TSh
            platformFloat = 92200000.0,
            ggr = 24800000.0
        )
    )
    val platformMetrics: StateFlow<PlatformMetrics> = _platformMetrics.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<AuditLog>>(emptyList())
    val auditLogs: StateFlow<List<AuditLog>> = _auditLogs.asStateFlow()

    init {
        _auditLogs.value = listOf(
            AuditLog(
                id = "LOG-01",
                timestamp = System.currentTimeMillis() - 10 * 60 * 1000,
                actor = "SUPER_ADMIN (Elisha)",
                role = "ADMIN",
                action = "ODDS_ADJUSTMENT",
                details = "Adjusted Arsenal vs Chelsea 1X2 odds to 1.45 due to live red card on Chelsea"
            ),
            AuditLog(
                id = "LOG-02",
                timestamp = System.currentTimeMillis() - 40 * 60 * 1000,
                actor = "RISK_ENGINE",
                role = "SYSTEM",
                action = "LIABILITY_CHECK",
                details = "Maximum payout liability cap checked on Simba vs Yanga derby (Safe limit OK)"
            ),
            AuditLog(
                id = "LOG-03",
                timestamp = System.currentTimeMillis() - 90 * 60 * 1000,
                actor = "FINANCE_DEPT",
                role = "ADMIN",
                action = "WITHDRAWAL_APPROVAL",
                details = "Approved M-Pesa batch payout ref #WD-881290 (150,000 TSh)"
            )
        )
    }

    fun logAction(action: String, details: String) {
        val newLog = AuditLog(
            id = "LOG-" + UUID.randomUUID().toString().substring(0, 5).uppercase(),
            timestamp = System.currentTimeMillis(),
            actor = "SUPER_ADMIN",
            role = "ADMIN",
            action = action,
            details = details
        )
        _auditLogs.value = listOf(newLog) + _auditLogs.value
    }

    fun approveWithdrawal(txId: String) {
        logAction("WITHDRAWAL_APPROVED", "Admin approved withdrawal transaction $txId")
    }

    fun rejectWithdrawal(txId: String, reason: String) {
        logAction("WITHDRAWAL_REJECTED", "Admin rejected withdrawal $txId: $reason")
    }

    fun updateOdds(matchId: String, marketId: String, optionId: String, newOdds: Double) {
        sportsRepository.updateOdds(matchId, marketId, optionId, newOdds)
        logAction("MANUAL_ODDS_EDIT", "Updated option $optionId to $newOdds in match $matchId")
    }

    fun toggleMarket(matchId: String, marketId: String) {
        sportsRepository.toggleMarketSuspension(matchId, marketId)
        logAction("MARKET_SUSPENSION_TOGGLE", "Toggled suspension for market $marketId in match $matchId")
    }

    fun settleMatch(matchId: String, homeScore: Int, awayScore: Int) {
        sportsRepository.settleMatch(matchId, homeScore, awayScore)
        logAction("MATCH_SETTLEMENT", "Match $matchId settled with final score $homeScore - $awayScore. Auto-settled open bets.")
    }
}
