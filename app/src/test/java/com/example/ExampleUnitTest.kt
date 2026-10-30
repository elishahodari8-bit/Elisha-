package com.example

import com.example.data.AgentRepository
import com.example.data.BettingRepository
import com.example.data.WalletRepository
import com.example.model.BetSlipItem
import com.example.model.PaymentMethod
import com.example.model.Sport
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testAllTenSportsSupported() {
        val sports = Sport.values()
        assertEquals(10, sports.size)

        val sportIds = sports.map { it.id }.toSet()
        val expectedIds = setOf(
            "football", "basketball", "tennis", "volleyball", "baseball",
            "rugby", "cricket", "ice_hockey", "boxing", "f1"
        )
        assertEquals(expectedIds, sportIds)
    }

    @Test
    fun testBetSlipAndAccaBonusCalculation() {
        var balance = 50000.0
        val bettingRepo = BettingRepository(
            onBetPlaced = { stake ->
                if (balance >= stake) {
                    balance -= stake
                    true
                } else false
            },
            onWinOrCashout = { amount, _ ->
                balance += amount
            }
        )

        // Add 3 legs
        bettingRepo.toggleSelection(
            BetSlipItem("m1", "Arsenal vs Chelsea", Sport.FOOTBALL, "1X2", "opt_1", "Arsenal", 1.50)
        )
        bettingRepo.toggleSelection(
            BetSlipItem("m2", "Lakers vs Warriors", Sport.BASKETBALL, "Moneyline", "opt_2", "Lakers", 2.00)
        )
        bettingRepo.toggleSelection(
            BetSlipItem("m3", "Alcaraz vs Sinner", Sport.TENNIS, "Match Winner", "opt_3", "Alcaraz", 1.60)
        )

        assertEquals(3, bettingRepo.betSlipItems.value.size)
        // 1.5 * 2.0 * 1.6 = 4.8
        assertEquals(4.80, bettingRepo.calculateTotalOdds(), 0.01)

        // 3 legs gives 7% acca boost
        assertEquals(7.0, bettingRepo.calculateAccaBonusPercentage(), 0.01)

        bettingRepo.updateStake(10000.0)
        // Raw: 10,000 * 4.8 = 48,000. With 7% bonus: 48,000 * 1.07 = 51,360
        assertEquals(51360.0, bettingRepo.calculatePotentialReturn(), 1.0)
    }

    @Test
    fun testWalletDepositLimitsCompliance() {
        val walletRepo = WalletRepository()
        val initialUser = walletRepo.user.value

        // Try depositing more than daily limit (limit is 1,000,000 TSh)
        val excessiveResult = walletRepo.deposit(
            PaymentMethod.MPESA,
            "+255 754 000 111",
            2000000.0
        )
        assertTrue(excessiveResult.isFailure)

        // Safe deposit within limits
        val validResult = walletRepo.deposit(
            PaymentMethod.MPESA,
            "+255 754 000 111",
            10000.0
        )
        assertTrue(validResult.isSuccess)
        assertEquals(initialUser.realBalance + 10000.0, walletRepo.user.value.realBalance, 0.01)
    }

    @Test
    fun testAgentCashinCommission() {
        var customerReceived = 0.0
        val agentRepo = AgentRepository(
            onCustomerCashin = { _, amount -> customerReceived += amount }
        )

        val initialFloat = agentRepo.agent.value.floatBalance
        val initialCommission = agentRepo.agent.value.totalCommissionEarned

        val res = agentRepo.cashInCustomer("+255 712 999 888", 20000.0)
        assertTrue(res.isSuccess)
        assertEquals(20000.0, customerReceived, 0.01)

        // Float deducted by 20,000
        assertEquals(initialFloat - 20000.0, agentRepo.agent.value.floatBalance, 0.01)
        // Commission credited by 10% (2,000)
        assertEquals(initialCommission + 2000.0, agentRepo.agent.value.totalCommissionEarned, 0.01)
    }
}
