package com.example.data

import com.example.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class SportsRepository(private val scope: CoroutineScope) {

    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    val matches: StateFlow<List<Match>> = _matches.asStateFlow()

    private val _selectedSport = MutableStateFlow<Sport?>(null) // null = All
    val selectedSport: StateFlow<Sport?> = _selectedSport.asStateFlow()

    init {
        _matches.value = generateInitialMatches()
        startLiveScoreEngine()
    }

    fun selectSport(sport: Sport?) {
        _selectedSport.value = sport
    }

    private fun startLiveScoreEngine() {
        scope.launch(Dispatchers.Default) {
            while (true) {
                delay(3500)
                updateLiveMatches()
            }
        }
    }

    private fun updateLiveMatches() {
        _matches.value = _matches.value.map { match ->
            if (!match.isLive || match.isFinished) return@map match

            val newMinute = match.liveMinute + 1
            var homeScore = match.homeScore
            var awayScore = match.awayScore

            // Random chance of goal/point event
            val eventChance = Random.nextInt(100)
            if (eventChance > 92) {
                if (Random.nextBoolean()) homeScore += 1 else awayScore += 1
            }

            val attackingSides = listOf("HOME", "MIDFIELD", "AWAY")
            val newAttacking = attackingSides[Random.nextInt(attackingSides.size)]

            // Minor realistic odds shift
            val updatedMarkets = match.markets.map { market ->
                val updatedOptions = market.options.map { opt ->
                    val delta = if (Random.nextInt(100) > 85) (Random.nextDouble(-0.05, 0.05)) else 0.0
                    val newOdds = (opt.odds + delta).coerceIn(1.05, 50.0)
                    opt.copy(
                        previousOdds = opt.odds,
                        odds = (Math.round(newOdds * 100.0) / 100.0)
                    )
                }
                market.copy(options = updatedOptions)
            }

            val updatedStats = match.stats.copy(
                possessionHome = (match.stats.possessionHome + Random.nextInt(-2, 3)).coerceIn(35, 65).let { it },
                possessionAway = 100 - (match.stats.possessionHome),
                shotsHome = match.stats.shotsHome + if (eventChance > 90) 1 else 0,
                attackingSide = newAttacking
            )

            match.copy(
                liveMinute = if (newMinute > 90 && match.sport == Sport.FOOTBALL) 90 else newMinute,
                homeScore = homeScore,
                awayScore = awayScore,
                stats = updatedStats,
                markets = updatedMarkets
            )
        }
    }

    fun updateOdds(matchId: String, marketId: String, optionId: String, newOdds: Double) {
        _matches.value = _matches.value.map { match ->
            if (match.id != matchId) return@map match
            val updatedMarkets = match.markets.map { market ->
                if (market.id != marketId) return@map market
                val updatedOptions = market.options.map { opt ->
                    if (opt.id == optionId) opt.copy(odds = newOdds, previousOdds = opt.odds) else opt
                }
                market.copy(options = updatedOptions)
            }
            match.copy(markets = updatedMarkets)
        }
    }

    fun toggleMarketSuspension(matchId: String, marketId: String) {
        _matches.value = _matches.value.map { match ->
            if (match.id != matchId) return@map match
            val updatedMarkets = match.markets.map { market ->
                if (market.id != marketId) return@map market
                val isSuspended = market.options.any { it.suspended }
                val updatedOptions = market.options.map { it.copy(suspended = !isSuspended) }
                market.copy(options = updatedOptions)
            }
            match.copy(markets = updatedMarkets)
        }
    }

    fun settleMatch(matchId: String, finalHomeScore: Int, finalAwayScore: Int) {
        _matches.value = _matches.value.map { match ->
            if (match.id != matchId) return@map match
            match.copy(
                isLive = false,
                isFinished = true,
                homeScore = finalHomeScore,
                awayScore = finalAwayScore
            )
        }
    }

    private fun generateInitialMatches(): List<Match> {
        return listOf(
            // 1. FOOTBALL - Premier League (LIVE)
            Match(
                id = "m_fb_1",
                sport = Sport.FOOTBALL,
                league = "English Premier League",
                homeTeam = "Arsenal FC",
                awayTeam = "Chelsea FC",
                startTime = "LIVE",
                isLive = true,
                liveMinute = 67,
                livePeriod = "2nd Half",
                homeScore = 2,
                awayScore = 1,
                isFeatured = true,
                stats = MatchStats(possessionHome = 58, possessionAway = 42, shotsHome = 12, shotsAway = 7, yellowCardsHome = 2, yellowCardsAway = 3, attackingSide = "HOME"),
                markets = listOf(
                    BetMarket("mkt_1x2_fb1", "Full Time Result (1X2)", "Main", listOf(
                        BetOption("opt_1", "Arsenal", 1.45),
                        BetOption("opt_x", "Draw", 3.80),
                        BetOption("opt_2", "Chelsea", 6.50)
                    )),
                    BetMarket("mkt_ou_fb1", "Total Goals Over/Under 3.5", "Goals", listOf(
                        BetOption("opt_o35", "Over 3.5", 1.95),
                        BetOption("opt_u35", "Under 3.5", 1.80)
                    )),
                    BetMarket("mkt_btts_fb1", "Both Teams to Score", "Goals", listOf(
                        BetOption("opt_btts_y", "Yes", 1.35),
                        BetOption("opt_btts_n", "No", 2.95)
                    )),
                    BetMarket("mkt_dc_fb1", "Double Chance", "Main", listOf(
                        BetOption("opt_dc_1x", "1X (Arsenal or Draw)", 1.10),
                        BetOption("opt_dc_12", "12 (Any Team Wins)", 1.22),
                        BetOption("opt_dc_x2", "X2 (Draw or Chelsea)", 2.45)
                    ))
                )
            ),

            // FOOTBALL - Tanzania NBC Premier League (Derby)
            Match(
                id = "m_fb_2",
                sport = Sport.FOOTBALL,
                league = "Tanzania NBC Premier League",
                homeTeam = "Simba SC",
                awayTeam = "Young Africans SC (Yanga)",
                startTime = "Today, 19:00",
                isLive = false,
                liveMinute = 0,
                livePeriod = "Pre-Match",
                homeScore = 0,
                awayScore = 0,
                isFeatured = true,
                markets = listOf(
                    BetMarket("mkt_1x2_fb2", "Full Time Result (1X2)", "Main", listOf(
                        BetOption("opt_s1", "Simba SC", 2.30),
                        BetOption("opt_sx", "Draw", 3.10),
                        BetOption("opt_s2", "Yanga SC", 2.85)
                    )),
                    BetMarket("mkt_ou_fb2", "Total Goals Over/Under 2.5", "Goals", listOf(
                        BetOption("opt_ou_o25", "Over 2.5", 2.10),
                        BetOption("opt_ou_u25", "Under 2.5", 1.65)
                    )),
                    BetMarket("mkt_btts_fb2", "Both Teams to Score", "Goals", listOf(
                        BetOption("opt_btts_y2", "Yes", 1.85),
                        BetOption("opt_btts_n2", "No", 1.90)
                    ))
                )
            ),

            // FOOTBALL - UEFA Champions League
            Match(
                id = "m_fb_3",
                sport = Sport.FOOTBALL,
                league = "UEFA Champions League",
                homeTeam = "Real Madrid",
                awayTeam = "Manchester City",
                startTime = "Tomorrow, 22:00",
                isLive = false,
                liveMinute = 0,
                livePeriod = "Pre-Match",
                homeScore = 0,
                awayScore = 0,
                isFeatured = true,
                markets = listOf(
                    BetMarket("mkt_1x2_fb3", "Full Time Result (1X2)", "Main", listOf(
                        BetOption("opt_rm", "Real Madrid", 2.65),
                        BetOption("opt_draw_cl", "Draw", 3.45),
                        BetOption("opt_mc", "Man City", 2.40)
                    )),
                    BetMarket("mkt_ou_fb3", "Over/Under 2.5 Goals", "Goals", listOf(
                        BetOption("opt_cl_o25", "Over 2.5", 1.70),
                        BetOption("opt_cl_u25", "Under 2.5", 2.15)
                    ))
                )
            ),

            // 2. BASKETBALL - NBA (LIVE)
            Match(
                id = "m_bb_1",
                sport = Sport.BASKETBALL,
                league = "NBA Regular Season",
                homeTeam = "Los Angeles Lakers",
                awayTeam = "Golden State Warriors",
                startTime = "LIVE",
                isLive = true,
                liveMinute = 9,
                livePeriod = "3rd Quarter",
                homeScore = 78,
                awayScore = 82,
                isFeatured = true,
                stats = MatchStats(possessionHome = 51, possessionAway = 49, shotsHome = 34, shotsAway = 36, foulsHome = 11, foulsAway = 9, attackingSide = "AWAY"),
                markets = listOf(
                    BetMarket("mkt_bb_win", "Moneyline (Match Winner)", "Main", listOf(
                        BetOption("opt_lal", "LA Lakers", 2.15),
                        BetOption("opt_gsw", "GS Warriors", 1.72)
                    )),
                    BetMarket("mkt_bb_spread", "Point Spread (-3.5)", "Handicap", listOf(
                        BetOption("opt_lal_sp", "Lakers +3.5", 1.90),
                        BetOption("opt_gsw_sp", "Warriors -3.5", 1.90)
                    )),
                    BetMarket("mkt_bb_total", "Total Points Over/Under 224.5", "Totals", listOf(
                        BetOption("opt_pts_o", "Over 224.5", 1.88),
                        BetOption("opt_pts_u", "Under 224.5", 1.88)
                    ))
                )
            ),

            // 3. TENNIS - ATP Finals (LIVE)
            Match(
                id = "m_tn_1",
                sport = Sport.TENNIS,
                league = "ATP Finals Turin",
                homeTeam = "Carlos Alcaraz",
                awayTeam = "Jannik Sinner",
                startTime = "LIVE",
                isLive = true,
                liveMinute = 4,
                livePeriod = "Set 2 (Game 4)",
                homeScore = 1,
                awayScore = 0,
                isFeatured = true,
                markets = listOf(
                    BetMarket("mkt_tn_win", "Match Winner", "Main", listOf(
                        BetOption("opt_alc", "Carlos Alcaraz", 1.62),
                        BetOption("opt_sin", "Jannik Sinner", 2.25)
                    )),
                    BetMarket("mkt_tn_set2", "Set 2 Winner", "Sets", listOf(
                        BetOption("opt_s2_alc", "Alcaraz Set 2", 1.75),
                        BetOption("opt_s2_sin", "Sinner Set 2", 2.05)
                    )),
                    BetMarket("mkt_tn_games", "Total Games Over/Under 22.5", "Totals", listOf(
                        BetOption("opt_gm_o", "Over 22.5", 1.85),
                        BetOption("opt_gm_u", "Under 22.5", 1.95)
                    ))
                )
            ),

            // 4. VOLLEYBALL - FIVB World League
            Match(
                id = "m_vb_1",
                sport = Sport.VOLLEYBALL,
                league = "FIVB Nations League",
                homeTeam = "Poland",
                awayTeam = "Italy",
                startTime = "Today, 20:30",
                isLive = false,
                liveMinute = 0,
                livePeriod = "Pre-Match",
                homeScore = 0,
                awayScore = 0,
                markets = listOf(
                    BetMarket("mkt_vb_win", "Match Winner", "Main", listOf(
                        BetOption("opt_pol", "Poland", 1.55),
                        BetOption("opt_ita", "Italy", 2.45)
                    )),
                    BetMarket("mkt_vb_sets", "Total Sets Over/Under 3.5", "Totals", listOf(
                        BetOption("opt_vb_o35", "Over 3.5 Sets", 1.40),
                        BetOption("opt_vb_u35", "Under 3.5 Sets", 2.75)
                    ))
                )
            ),

            // 5. BASEBALL - MLB
            Match(
                id = "m_bs_1",
                sport = Sport.BASEBALL,
                league = "MLB World Series",
                homeTeam = "New York Yankees",
                awayTeam = "Los Angeles Dodgers",
                startTime = "Tonight, 02:00",
                isLive = false,
                liveMinute = 0,
                livePeriod = "Pre-Match",
                homeScore = 0,
                awayScore = 0,
                markets = listOf(
                    BetMarket("mkt_bs_win", "Moneyline Winner", "Main", listOf(
                        BetOption("opt_nyy", "NY Yankees", 1.98),
                        BetOption("opt_lad", "LA Dodgers", 1.86)
                    )),
                    BetMarket("mkt_bs_runs", "Total Runs Over/Under 8.5", "Totals", listOf(
                        BetOption("opt_bs_o85", "Over 8.5 Runs", 1.90),
                        BetOption("opt_bs_u85", "Under 8.5 Runs", 1.90)
                    ))
                )
            ),

            // 6. RUGBY - Six Nations Championship
            Match(
                id = "m_rg_1",
                sport = Sport.RUGBY,
                league = "Six Nations Championship",
                homeTeam = "New Zealand (All Blacks)",
                awayTeam = "South Africa (Springboks)",
                startTime = "Saturday, 18:00",
                isLive = false,
                liveMinute = 0,
                livePeriod = "Pre-Match",
                homeScore = 0,
                awayScore = 0,
                markets = listOf(
                    BetMarket("mkt_rg_1x2", "Match Result (3-Way)", "Main", listOf(
                        BetOption("opt_allblacks", "New Zealand", 1.70),
                        BetOption("opt_rg_draw", "Draw", 18.0),
                        BetOption("opt_springboks", "South Africa", 2.15)
                    )),
                    BetMarket("mkt_rg_handicap", "Handicap (-2.5)", "Handicap", listOf(
                        BetOption("opt_rg_nz_hc", "New Zealand -2.5", 1.88),
                        BetOption("opt_rg_sa_hc", "South Africa +2.5", 1.88)
                    ))
                )
            ),

            // 7. CRICKET - ICC World Cup
            Match(
                id = "m_ck_1",
                sport = Sport.CRICKET,
                league = "ICC Men's T20 World Cup",
                homeTeam = "India",
                awayTeam = "Australia",
                startTime = "Sunday, 15:30",
                isLive = false,
                liveMinute = 0,
                livePeriod = "Pre-Match",
                homeScore = 0,
                awayScore = 0,
                markets = listOf(
                    BetMarket("mkt_ck_win", "Match Winner", "Main", listOf(
                        BetOption("opt_ind", "India", 1.68),
                        BetOption("opt_aus", "Australia", 2.20)
                    )),
                    BetMarket("mkt_ck_sixes", "Most Match Sixes", "Specials", listOf(
                        BetOption("opt_ind_6", "India", 1.80),
                        BetOption("opt_aus_6", "Australia", 2.05),
                        BetOption("opt_tie_6", "Tie", 6.50)
                    ))
                )
            ),

            // 8. ICE HOCKEY - NHL
            Match(
                id = "m_ih_1",
                sport = Sport.ICE_HOCKEY,
                league = "NHL Stanley Cup",
                homeTeam = "Edmonton Oilers",
                awayTeam = "Florida Panthers",
                startTime = "Tonight, 03:00",
                isLive = false,
                liveMinute = 0,
                livePeriod = "Pre-Match",
                homeScore = 0,
                awayScore = 0,
                markets = listOf(
                    BetMarket("mkt_ih_win", "Moneyline Winner", "Main", listOf(
                        BetOption("opt_oilers", "Edmonton Oilers", 1.92),
                        BetOption("opt_panthers", "Florida Panthers", 1.92)
                    )),
                    BetMarket("mkt_ih_total", "Total Goals Over/Under 5.5", "Totals", listOf(
                        BetOption("opt_ih_o55", "Over 5.5", 1.78),
                        BetOption("opt_ih_u55", "Under 5.5", 2.05)
                    ))
                )
            ),

            // 9. BOXING - World Heavyweight Title
            Match(
                id = "m_bx_1",
                sport = Sport.BOXING,
                league = "Undisputed Heavyweight Championship",
                homeTeam = "Tyson Fury",
                awayTeam = "Oleksandr Usyk",
                startTime = "Next Saturday, 23:30",
                isLive = false,
                liveMinute = 0,
                livePeriod = "Pre-Match",
                homeScore = 0,
                awayScore = 0,
                isFeatured = true,
                markets = listOf(
                    BetMarket("mkt_bx_win", "Bout Winner (2-Way)", "Main", listOf(
                        BetOption("opt_fury", "Tyson Fury", 1.95),
                        BetOption("opt_usyk", "Oleksandr Usyk", 1.88)
                    )),
                    BetMarket("mkt_bx_method", "Method of Victory", "Specials", listOf(
                        BetOption("opt_fury_ko", "Fury by KO/TKO", 3.75),
                        BetOption("opt_fury_dec", "Fury by Decision", 3.25),
                        BetOption("opt_usyk_ko", "Usyk by KO/TKO", 5.00),
                        BetOption("opt_usyk_dec", "Usyk by Decision", 2.40)
                    ))
                )
            ),

            // 10. FORMULA 1 - Grand Prix
            Match(
                id = "m_f1_1",
                sport = Sport.FORMULA_1,
                league = "Formula 1 Grand Prix",
                homeTeam = "Max Verstappen",
                awayTeam = "Lewis Hamilton",
                startTime = "Sunday, 16:00",
                isLive = false,
                liveMinute = 0,
                livePeriod = "Pre-Race",
                homeScore = 0,
                awayScore = 0,
                isFeatured = true,
                markets = listOf(
                    BetMarket("mkt_f1_winner", "Race Winner", "Main", listOf(
                        BetOption("opt_ver", "Max Verstappen (Red Bull)", 1.55),
                        BetOption("opt_nor", "Lando Norris (McLaren)", 3.40),
                        BetOption("opt_lec", "Charles Leclerc (Ferrari)", 5.50),
                        BetOption("opt_ham", "Lewis Hamilton (Mercedes)", 9.00)
                    )),
                    BetMarket("mkt_f1_podium", "Podium Finish (Top 3)", "Specials", listOf(
                        BetOption("opt_ver_pod", "Verstappen Top 3", 1.15),
                        BetOption("opt_nor_pod", "Norris Top 3", 1.45),
                        BetOption("opt_lec_pod", "Leclerc Top 3", 1.80)
                    ))
                )
            )
        )
    }
}
