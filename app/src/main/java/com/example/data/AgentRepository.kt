package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class AgentRepository(
    private val onCustomerCashin: (String, Double) -> Unit // credits customer wallet
) {
    private val _agent = MutableStateFlow(AgentProfile())
    val agent: StateFlow<AgentProfile> = _agent.asStateFlow()

    private val _vouchers = MutableStateFlow<List<AgentVoucher>>(emptyList())
    val vouchers: StateFlow<List<AgentVoucher>> = _vouchers.asStateFlow()

    init {
        _vouchers.value = listOf(
            AgentVoucher("VCH-9821-EL", 10000.0, "+255 712 334 455", System.currentTimeMillis() - 3600000, true),
            AgentVoucher("VCH-5510-EL", 25000.0, "+255 788 112 233", System.currentTimeMillis() - 7200000, true)
        )
    }

    fun cashInCustomer(customerPhone: String, amount: Double): Result<AgentVoucher> {
        val currentAgent = _agent.value
        if (currentAgent.floatBalance < amount) {
            return Result.failure(Exception("Insufficient cashier float balance to fulfill cash-in."))
        }

        val commissionEarned = amount * (currentAgent.commissionRatePercentage / 100.0)
        _agent.value = currentAgent.copy(
            floatBalance = currentAgent.floatBalance - amount,
            totalCommissionEarned = currentAgent.totalCommissionEarned + commissionEarned,
            activeCustomersCount = currentAgent.activeCustomersCount + 1
        )

        val voucherCode = "EL-" + UUID.randomUUID().toString().substring(0, 8).uppercase()
        val voucher = AgentVoucher(
            code = voucherCode,
            amount = amount,
            customerPhone = customerPhone,
            createdAt = System.currentTimeMillis(),
            isRedeemed = true
        )

        _vouchers.value = listOf(voucher) + _vouchers.value
        onCustomerCashin(customerPhone, amount)
        return Result.success(voucher)
    }

    fun topupFloat(amount: Double) {
        val currentAgent = _agent.value
        _agent.value = currentAgent.copy(
            floatBalance = currentAgent.floatBalance + amount
        )
    }
}
