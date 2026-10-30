package com.example.data.model

/**
 * Core sports supported by ELISHA BET sportsbook.
 */
enum class Sport(val id: String, val displayName: String, val iconSymbol: String) {
    FOOTBALL("football", "Football", "⚽"),
    BASKETBALL("basketball", "Basketball", "🏀"),
    TENNIS("tennis", "Tennis", "🎾"),
    VOLLEYBALL("volleyball", "Volleyball", "🏐"),
    BASEBALL("baseball", "Baseball", "⚾"),
    RUGBY("rugby", "Rugby", "🏉"),
    CRICKET("cricket", "Cricket", "🏏"),
    ICE_HOCKEY("ice_hockey", "Ice Hockey", "🏒"),
    BOXING("boxing", "Boxing", "🥊"),
    FORMULA_1("f1", "Formula 1", "🏎️")
}

/**
 * Individual betting selection/outcome with odds and suspension flag.
 */
data class BetOption(
    val id: String,
    val name: String,
    val odds: Double,
    val previousOdds: Double = odds,
    val suspended: Boolean = false
) {
    val isOddsIncreased: Boolean get() = odds > previousOdds
    val isOddsDecreased: Boolean get() = odds < previousOdds
}

/**
 * A betting market containing multiple selectable outcomes (e.g. 1X2, Over/Under).
 */
data class BetMarket(
    val id: String,
    val name: String,
    val category: String, // "Main", "Goals", "Halves", "Handicap", "Specials"
    val options: List<BetOption>
)

typealias Market = BetMarket

/**
 * Live telemetry and statistical counters for in-play simulation and pitch visualizers.
 */
data class MatchStats(
    val possessionHome: Int = 50,
    val possessionAway: Int = 50,
    val shotsHome: Int = 6,
    val shotsAway: Int = 4,
    val shotsOnTargetHome: Int = 3,
    val shotsOnTargetAway: Int = 2,
    val foulsHome: Int = 5,
    val foulsAway: Int = 7,
    val yellowCardsHome: Int = 1,
    val yellowCardsAway: Int = 2,
    val attackingSide: String = "HOME" // "HOME", "MIDFIELD", "AWAY"
)

/**
 * Sports match event with live score, timing status, and available markets.
 */
data class Match(
    val id: String,
    val sport: Sport,
    val league: String,
    val homeTeam: String,
    val awayTeam: String,
    val startTime: String,
    val isLive: Boolean,
    val liveMinute: Int,
    val livePeriod: String, // "1st Half", "2nd Half", "Q3", "Set 2", etc.
    val homeScore: Int,
    val awayScore: Int,
    val stats: MatchStats = MatchStats(),
    val markets: List<BetMarket>,
    val isFeatured: Boolean = false,
    val isFinished: Boolean = false
) {
    val liveScoreFormatted: String get() = "$homeScore - $awayScore"
    val isOddsAvailable: Boolean get() = markets.isNotEmpty() && markets.any { m -> m.options.any { !it.suspended } }
    val displayStatus: String get() = when {
        isFinished -> "FT"
        isLive -> "$liveMinute' $livePeriod"
        else -> startTime
    }
}

/**
 * Item chosen by the bettor staged in the Bet Slip.
 */
data class BetSlipItem(
    val matchId: String,
    val matchTitle: String,
    val sport: Sport,
    val marketName: String,
    val optionId: String,
    val optionName: String,
    val odds: Double
)

enum class BetType {
    SINGLE,
    ACCUMULATOR
}

enum class BetStatus {
    OPEN,
    WON,
    LOST,
    CASHED_OUT
}

/**
 * A placed sportsbook wager with potential payout, bonus boost, and live cashout value.
 */
data class PlacedBet(
    val id: String,
    val timestamp: Long,
    val items: List<BetSlipItem>,
    val stake: Double,
    val totalOdds: Double,
    val accaBonusPercentage: Double,
    val potentialReturn: Double,
    val status: BetStatus,
    val cashoutValue: Double
)

typealias Bet = PlacedBet
