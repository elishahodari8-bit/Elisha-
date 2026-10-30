package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.*
import com.example.ui.Screen
import com.example.ui.SportsViewModel
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ElishaBetTheme {
                ElishaBetApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElishaBetApp(
    viewModel: SportsViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val matches by viewModel.sportsRepository.matches.collectAsStateWithLifecycle()
    val selectedSport by viewModel.sportsRepository.selectedSport.collectAsStateWithLifecycle()
    val user by viewModel.walletRepository.user.collectAsStateWithLifecycle()
    val transactions by viewModel.walletRepository.transactions.collectAsStateWithLifecycle()
    val promotions by viewModel.walletRepository.promotions.collectAsStateWithLifecycle()

    val betSlipItems by viewModel.bettingRepository.betSlipItems.collectAsStateWithLifecycle()
    val stake by viewModel.bettingRepository.stake.collectAsStateWithLifecycle()
    val placedBets by viewModel.bettingRepository.placedBets.collectAsStateWithLifecycle()

    val agent by viewModel.agentRepository.agent.collectAsStateWithLifecycle()
    val agentVouchers by viewModel.agentRepository.vouchers.collectAsStateWithLifecycle()

    val adminMetrics by viewModel.adminRepository.platformMetrics.collectAsStateWithLifecycle()
    val auditLogs by viewModel.adminRepository.auditLogs.collectAsStateWithLifecycle()

    val isBetSlipExpanded by viewModel.isBetSlipExpanded.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filterLiveOnly by viewModel.filterLiveOnly.collectAsStateWithLifecycle()
    val userFeedbackMessage by viewModel.userFeedbackMessage.collectAsStateWithLifecycle()
    val supportMessages by viewModel.supportMessages.collectAsStateWithLifecycle()
    val selectedMatchId by viewModel.selectedMatchId.collectAsStateWithLifecycle()

    val selectedOptionIds = remember(betSlipItems) {
        betSlipItems.map { it.optionId }.toSet()
    }

    val totalOdds = remember(betSlipItems) { viewModel.bettingRepository.calculateTotalOdds() }
    val accaBonus = remember(betSlipItems) { viewModel.bettingRepository.calculateAccaBonusPercentage() }
    val potentialReturn = remember(betSlipItems, stake) { viewModel.bettingRepository.calculatePotentialReturn() }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userFeedbackMessage) {
        userFeedbackMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearFeedback()
        }
    }

    // Back handling
    BackHandler(enabled = currentScreen != Screen.DASHBOARD) {
        if (isBetSlipExpanded) {
            viewModel.setBetSlipExpanded(false)
        } else {
            viewModel.navigateTo(Screen.DASHBOARD)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = LuxuryBlack,
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                ComplianceDisclaimerBanner(
                    onNavigateResponsibleGaming = { viewModel.navigateTo(Screen.RESPONSIBLE_GAMBLING) }
                )
                ElishaBetHeader(
                    user = user,
                    currentRole = currentRole,
                    onRoleSelected = { viewModel.selectRole(it) },
                    onNavigateWallet = { viewModel.navigateTo(Screen.WALLET) },
                    onNavigateSupport = { viewModel.navigateTo(Screen.CUSTOMER_SUPPORT) },
                    onNavigateResponsibleGaming = { viewModel.navigateTo(Screen.RESPONSIBLE_GAMBLING) }
                )
            }
        },
        bottomBar = {
            Column(modifier = Modifier.navigationBarsPadding()) {
                // Floating Bet Slip Bar (if items in slip)
                if (betSlipItems.isNotEmpty() && !isBetSlipExpanded) {
                    BetSlipFloatingBar(
                        itemCount = betSlipItems.size,
                        totalOdds = totalOdds,
                        accaBonusPercentage = accaBonus,
                        onClick = { viewModel.setBetSlipExpanded(true) }
                    )
                }

                // Standard Bottom Navigation Bar
                NavigationBar(
                    containerColor = DarkSurface,
                    contentColor = TextPrimary,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("main_bottom_nav")
                ) {
                    NavigationBarItem(
                        selected = currentScreen == Screen.DASHBOARD,
                        onClick = { viewModel.navigateTo(Screen.DASHBOARD) },
                        icon = { Icon(Icons.Default.SportsSoccer, contentDescription = "Sports") },
                        label = { Text("Sports", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = LuxuryBlack,
                            selectedTextColor = GoldPrimary,
                            indicatorColor = GoldPrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen == Screen.BET_HISTORY,
                        onClick = { viewModel.navigateTo(Screen.BET_HISTORY) },
                        icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "My Bets") },
                        label = { Text("My Bets", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = LuxuryBlack,
                            selectedTextColor = GoldPrimary,
                            indicatorColor = GoldPrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen == Screen.WALLET,
                        onClick = { viewModel.navigateTo(Screen.WALLET) },
                        icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Wallet") },
                        label = { Text("Wallet", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = LuxuryBlack,
                            selectedTextColor = GoldPrimary,
                            indicatorColor = GoldPrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen == Screen.PROMOTIONS,
                        onClick = { viewModel.navigateTo(Screen.PROMOTIONS) },
                        icon = { Icon(Icons.Default.CardGiftcard, contentDescription = "Promotions") },
                        label = { Text("Promos", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = LuxuryBlack,
                            selectedTextColor = GoldPrimary,
                            indicatorColor = GoldPrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen == Screen.CUSTOMER_SUPPORT,
                        onClick = { viewModel.navigateTo(Screen.CUSTOMER_SUPPORT) },
                        icon = { Icon(Icons.Default.HeadsetMic, contentDescription = "Support") },
                        label = { Text("Support", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = LuxuryBlack,
                            selectedTextColor = GoldPrimary,
                            indicatorColor = GoldPrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.DASHBOARD -> {
                    SportsDashboardScreen(
                        matches = matches,
                        selectedSport = selectedSport,
                        selectedOptionIds = selectedOptionIds,
                        searchQuery = searchQuery,
                        filterLiveOnly = filterLiveOnly,
                        onSportSelect = { viewModel.sportsRepository.selectSport(it) },
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onToggleLiveOnly = { viewModel.toggleLiveOnly() },
                        onOptionSelected = { item -> viewModel.bettingRepository.toggleSelection(item) },
                        onMatchClick = { matchId -> viewModel.openMatchDetail(matchId) },
                        onNavigatePromotions = { viewModel.navigateTo(Screen.PROMOTIONS) }
                    )
                }

                Screen.MATCH_DETAIL -> {
                    val match = matches.firstOrNull { it.id == selectedMatchId } ?: matches.first()
                    MatchDetailScreen(
                        match = match,
                        selectedOptionIds = selectedOptionIds,
                        onOptionSelected = { item -> viewModel.bettingRepository.toggleSelection(item) },
                        onBack = { viewModel.navigateTo(Screen.DASHBOARD) }
                    )
                }

                Screen.BET_HISTORY -> {
                    BetHistoryScreen(
                        placedBets = placedBets,
                        onCashOut = { betId ->
                            val res = viewModel.bettingRepository.cashOut(betId)
                            res.onSuccess { amt ->
                                viewModel.showFeedback("Successfully cashed out TSh %,d!".format(amt.toLong()))
                            }.onFailure { err ->
                                viewModel.showFeedback(err.message ?: "Cashout failed")
                            }
                        }
                    )
                }

                Screen.WALLET -> {
                    WalletScreen(
                        user = user,
                        transactions = transactions,
                        onDeposit = { method, phone, amt ->
                            val res = viewModel.walletRepository.deposit(method, phone, amt)
                            res.onSuccess {
                                viewModel.showFeedback("Deposited TSh %,d via %s!".format(amt.toLong(), method.title))
                            }.onFailure { err ->
                                viewModel.showFeedback(err.message ?: "Deposit failed")
                            }
                        },
                        onWithdraw = { method, phone, amt ->
                            val res = viewModel.walletRepository.requestWithdrawal(method, phone, amt)
                            res.onSuccess {
                                viewModel.showFeedback("Withdrawal request for TSh %,d submitted!".format(amt.toLong()))
                            }.onFailure { err ->
                                viewModel.showFeedback(err.message ?: "Withdrawal failed")
                            }
                        },
                        onSubmitKyc = { docType, docNumber ->
                            viewModel.walletRepository.submitKyc(docType, docNumber)
                            viewModel.showFeedback("KYC verified successfully. Safe betting enabled.")
                        },
                        onNavigateResponsibleGaming = { viewModel.navigateTo(Screen.RESPONSIBLE_GAMBLING) }
                    )
                }

                Screen.PROMOTIONS -> {
                    PromotionsReferralScreen(
                        user = user,
                        promotions = promotions,
                        onClaimPromo = { promoId ->
                            val res = viewModel.walletRepository.claimPromotion(promoId)
                            res.onSuccess { bonus ->
                                viewModel.showFeedback("Bonus credited: TSh %,d to your Bonus Balance!".format(bonus.toLong()))
                            }.onFailure { err ->
                                viewModel.showFeedback(err.message ?: "Promo claim failed")
                            }
                        },
                        onCopyReferral = {
                            viewModel.showFeedback("Referral code ${user.referralCode} copied!")
                        }
                    )
                }

                Screen.RESPONSIBLE_GAMBLING -> {
                    ResponsibleGamblingScreen(
                        user = user,
                        onUpdateDepositLimit = { limit ->
                            viewModel.walletRepository.updateDepositLimit(limit)
                            viewModel.showFeedback("Daily deposit limit updated to TSh %,d".format(limit.toLong()))
                        },
                        onToggleSelfExclusion = { enabled ->
                            viewModel.walletRepository.toggleSelfExclusion(enabled)
                            viewModel.showFeedback(if (enabled) "Self-exclusion active. Betting and deposits locked." else "Self-exclusion deactivated.")
                        }
                    )
                }

                Screen.CUSTOMER_SUPPORT -> {
                    CustomerSupportScreen(
                        messages = supportMessages,
                        onSendMessage = { text -> viewModel.sendSupportMessage(text) }
                    )
                }

                Screen.AGENT_PORTAL -> {
                    AgentPortalScreen(
                        agent = agent,
                        vouchers = agentVouchers,
                        onCustomerCashin = { phone, amt ->
                            val res = viewModel.agentRepository.cashInCustomer(phone, amt)
                            res.onSuccess { vch ->
                                viewModel.showFeedback("Customer $phone funded with TSh %,d!".format(amt.toLong()))
                            }.onFailure { err ->
                                viewModel.showFeedback(err.message ?: "Cash-in failed")
                            }
                        },
                        onTopupFloat = { amt ->
                            viewModel.agentRepository.topupFloat(amt)
                            viewModel.showFeedback("Float topped up with TSh %,d".format(amt.toLong()))
                        }
                    )
                }

                Screen.ADMIN_PORTAL -> {
                    AdminPortalScreen(
                        metrics = adminMetrics,
                        matches = matches,
                        pendingWithdrawals = transactions.filter { it.type == TransactionType.WITHDRAWAL && it.status == TransactionStatus.PENDING_APPROVAL },
                        auditLogs = auditLogs,
                        onUpdateOdds = { mId, mktId, optId, odds ->
                            viewModel.adminRepository.updateOdds(mId, mktId, optId, odds)
                            viewModel.showFeedback("Odds updated to %.2f".format(odds))
                        },
                        onToggleMarket = { mId, mktId ->
                            viewModel.adminRepository.toggleMarket(mId, mktId)
                            viewModel.showFeedback("Market suspension toggled")
                        },
                        onSettleMatch = { mId, hScore, aScore ->
                            viewModel.adminRepository.settleMatch(mId, hScore, aScore)
                            viewModel.showFeedback("Match settled: $hScore - $aScore. Open bets settled!")
                        },
                        onApproveWithdrawal = { txId ->
                            viewModel.adminRepository.approveWithdrawal(txId)
                            viewModel.showFeedback("Approved withdrawal $txId")
                        },
                        onRejectWithdrawal = { txId, reason ->
                            viewModel.adminRepository.rejectWithdrawal(txId, reason)
                            viewModel.showFeedback("Rejected withdrawal $txId")
                        }
                    )
                }
            }

            // Bet Slip Bottom Sheet
            if (isBetSlipExpanded) {
                BetSlipModal(
                    items = betSlipItems,
                    stake = stake,
                    totalOdds = totalOdds,
                    accaBonusPercentage = accaBonus,
                    potentialReturn = potentialReturn,
                    onStakeChange = { viewModel.bettingRepository.updateStake(it) },
                    onRemoveItem = { viewModel.bettingRepository.removeSelection(it) },
                    onClearAll = { viewModel.bettingRepository.clearSlip() },
                    onPlaceBet = {
                        val res = viewModel.bettingRepository.placeBet()
                        res.onSuccess { bet ->
                            viewModel.setBetSlipExpanded(false)
                            viewModel.showFeedback("Bet placed successfully! Ticket #${bet.id}")
                        }.onFailure { err ->
                            viewModel.showFeedback(err.message ?: "Failed to place bet")
                        }
                    },
                    onDismiss = { viewModel.setBetSlipExpanded(false) }
                )
            }
        }
    }
}
