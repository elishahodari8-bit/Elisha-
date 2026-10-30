package com.example.model

// Re-export core sportsbook data structures from com.example.data.model
typealias Sport = com.example.data.model.Sport
typealias BetOption = com.example.data.model.BetOption
typealias BetMarket = com.example.data.model.BetMarket
typealias Market = com.example.data.model.Market
typealias MatchStats = com.example.data.model.MatchStats
typealias Match = com.example.data.model.Match
typealias BetSlipItem = com.example.data.model.BetSlipItem
typealias BetType = com.example.data.model.BetType
typealias BetStatus = com.example.data.model.BetStatus
typealias PlacedBet = com.example.data.model.PlacedBet
typealias Bet = com.example.data.model.Bet


enum class KycStatus {
    NOT_VERIFIED,
    PENDING_REVIEW,
    VERIFIED
}

enum class PaymentMethod(val id: String, val title: String, val providerCode: String, val minDeposit: Double, val maxDeposit: Double) {
    MPESA("mpesa", "M-Pesa (Vodacom)", "*150*00#", 1000.0, 3000000.0),
    AIRTEL_MONEY("airtel", "Airtel Money", "*150*60#", 1000.0, 3000000.0),
    TIGO_PESA("tigo", "Tigo Pesa", "*150*01#", 1000.0, 3000000.0),
    HALOPESA("halopesa", "HaloPesa (Halotel)", "*150*88#", 1000.0, 2000000.0),
    BANK_TRANSFER("bank", "Bank Transfer (CRDB / NMB)", "BANK", 5000.0, 10000000.0)
}

enum class TransactionType {
    DEPOSIT,
    WITHDRAWAL,
    BET_STAKE,
    BET_WIN,
    BET_CASHOUT,
    REFERRAL_COMMISSION,
    AGENT_CASHIN
}

enum class TransactionStatus {
    COMPLETED,
    PENDING_APPROVAL,
    REJECTED
}

data class TransactionRecord(
    val id: String,
    val type: TransactionType,
    val method: PaymentMethod?,
    val amount: Double,
    val timestamp: Long,
    val reference: String,
    val status: TransactionStatus,
    val note: String = ""
)

data class UserProfile(
    val id: String = "USR-88219",
    val fullName: String = "Elisha Hodari",
    val phone: String = "+255 754 123 456",
    val email: String = "elishahodari8@gmail.com",
    val isAgeVerified: Boolean = true,
    val kycStatus: KycStatus = KycStatus.VERIFIED,
    val kycDocumentType: String = "National ID (NIDA)",
    val kycDocumentNumber: String = "19940822-XXXXX-0001",
    val realBalance: Double = 85000.0, // TSh
    val bonusBalance: Double = 15000.0, // TSh
    val dailyDepositLimit: Double = 1000000.0,
    val usedDepositToday: Double = 50000.0,
    val maxSingleBetLimit: Double = 500000.0,
    val selfExclusionActive: Boolean = false,
    val referralCode: String = "ELISHA-VIP-77",
    val referralCount: Int = 6,
    val referralEarnings: Double = 60000.0
)

data class AgentProfile(
    val id: String = "AGT-4019",
    val name: String = "Elisha Agency Hub (City Center)",
    val agentCode: String = "AGT-TZ-007",
    val phone: String = "+255 765 998 877",
    val shopLocation: String = "Samora Avenue, Dar es Salaam",
    val floatBalance: Double = 450000.0,
    val totalCommissionEarned: Double = 125000.0,
    val activeCustomersCount: Int = 38,
    val commissionRatePercentage: Double = 10.0
)

data class AgentVoucher(
    val code: String,
    val amount: Double,
    val customerPhone: String,
    val createdAt: Long,
    val isRedeemed: Boolean = false
)

data class Promotion(
    val id: String,
    val title: String,
    val tag: String,
    val description: String,
    val bonusPercentage: Int,
    val maxBonusTsh: Double,
    val code: String,
    val isClaimed: Boolean = false
)

data class AuditLog(
    val id: String,
    val timestamp: Long,
    val actor: String,
    val role: String,
    val action: String,
    val details: String
)

data class SupportMessage(
    val id: String,
    val sender: String,
    val isFromUser: Boolean,
    val text: String,
    val timestamp: Long
)

data class PlatformMetrics(
    val totalUsers: Int,
    val activeBettors: Int,
    val totalDeposits: Double,
    val totalWithdrawals: Double,
    val platformFloat: Double,
    val ggr: Double
)

enum class AppRole(val title: String, val badge: String) {
    CUSTOMER("Bettor (Customer)", "VIP"),
    AGENT("Agent Portal", "SHOP"),
    ADMIN("Super Admin", "HQ")
}

