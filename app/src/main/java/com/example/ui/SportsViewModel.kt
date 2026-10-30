package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class Screen {
    DASHBOARD,
    MATCH_DETAIL,
    BET_HISTORY,
    WALLET,
    PROMOTIONS,
    RESPONSIBLE_GAMBLING,
    CUSTOMER_SUPPORT,
    AGENT_PORTAL,
    ADMIN_PORTAL
}

class SportsViewModel : ViewModel() {

    val sportsRepository = SportsRepository(viewModelScope)
    val walletRepository = WalletRepository()

    val bettingRepository = BettingRepository(
        onBetPlaced = { stake ->
            walletRepository.deductForBet(stake)
        },
        onWinOrCashout = { amount, note ->
            walletRepository.creditBalance(amount, note)
        }
    )

    val agentRepository = AgentRepository(
        onCustomerCashin = { _, amount ->
            walletRepository.creditBalance(amount, "Cash-in voucher from Agent")
        }
    )

    val adminRepository = AdminRepository(sportsRepository, bettingRepository, walletRepository)

    // UI States
    private val _currentScreen = MutableStateFlow(Screen.DASHBOARD)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _currentRole = MutableStateFlow(AppRole.CUSTOMER)
    val currentRole: StateFlow<AppRole> = _currentRole.asStateFlow()

    private val _selectedMatchId = MutableStateFlow<String?>(null)
    val selectedMatchId: StateFlow<String?> = _selectedMatchId.asStateFlow()

    private val _isBetSlipExpanded = MutableStateFlow(false)
    val isBetSlipExpanded: StateFlow<Boolean> = _isBetSlipExpanded.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterLiveOnly = MutableStateFlow(false)
    val filterLiveOnly: StateFlow<Boolean> = _filterLiveOnly.asStateFlow()

    private val _userFeedbackMessage = MutableStateFlow<String?>(null)
    val userFeedbackMessage: StateFlow<String?> = _userFeedbackMessage.asStateFlow()

    // Support chat messages
    private val _supportMessages = MutableStateFlow<List<SupportMessage>>(
        listOf(
            SupportMessage("1", "ELISHA BET Support", false, "Welcome to ELISHA BET Customer Care! How can we assist with your betting, deposit, or verification today?", System.currentTimeMillis() - 60000)
        )
    )
    val supportMessages: StateFlow<List<SupportMessage>> = _supportMessages.asStateFlow()

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun selectRole(role: AppRole) {
        _currentRole.value = role
        when (role) {
            AppRole.CUSTOMER -> navigateTo(Screen.DASHBOARD)
            AppRole.AGENT -> navigateTo(Screen.AGENT_PORTAL)
            AppRole.ADMIN -> navigateTo(Screen.ADMIN_PORTAL)
        }
    }

    fun openMatchDetail(matchId: String) {
        _selectedMatchId.value = matchId
        navigateTo(Screen.MATCH_DETAIL)
    }

    fun setBetSlipExpanded(expanded: Boolean) {
        _isBetSlipExpanded.value = expanded
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleLiveOnly() {
        _filterLiveOnly.value = !_filterLiveOnly.value
    }

    fun showFeedback(msg: String) {
        _userFeedbackMessage.value = msg
    }

    fun clearFeedback() {
        _userFeedbackMessage.value = null
    }

    fun sendSupportMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = SupportMessage(
            id = System.currentTimeMillis().toString(),
            sender = "You",
            isFromUser = true,
            text = text,
            timestamp = System.currentTimeMillis()
        )
        _supportMessages.value = _supportMessages.value + userMsg

        // Automated smart reply simulation
        viewModelScope.launch {
            kotlinx.coroutines.delay(1200)
            val replyText = when {
                text.contains("deposit", ignoreCase = true) ->
                    "Deposits via M-Pesa, Airtel Money, Tigo Pesa, and HaloPesa reflect instantly. Ensure your registered phone number matches your mobile money wallet."
                text.contains("withdraw", ignoreCase = true) ->
                    "Withdrawals are processed 24/7. First withdrawals require KYC identity document confirmation (NIDA/Passport) under Gaming Board regulations."
                text.contains("bonus", ignoreCase = true) || text.contains("promo", ignoreCase = true) ->
                    "Our 100% Welcome Bonus is credited to your Sports Bonus wallet upon first deposit. Acca Boost adds up to 500% extra cash on parlays!"
                text.contains("cashout", ignoreCase = true) ->
                    "Cash Out is available on live active tickets before the final whistle. Check your 'My Bets' tab to see current cashout valuations."
                else ->
                    "Thank you for contacting ELISHA BET Support. An agent has logged your ticket and is reviewing your account details."
            }
            val botMsg = SupportMessage(
                id = (System.currentTimeMillis() + 1).toString(),
                sender = "ELISHA BET Specialist",
                isFromUser = false,
                text = replyText,
                timestamp = System.currentTimeMillis()
            )
            _supportMessages.value = _supportMessages.value + botMsg
        }
    }
}
