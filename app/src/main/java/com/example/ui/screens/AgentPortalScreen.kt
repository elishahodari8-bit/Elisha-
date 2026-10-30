package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AgentProfile
import com.example.model.AgentVoucher
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AgentPortalScreen(
    agent: AgentProfile,
    vouchers: List<AgentVoucher>,
    onCustomerCashin: (String, Double) -> Unit,
    onTopupFloat: (Double) -> Unit
) {
    var customerPhoneInput by remember { mutableStateOf("+255 712 345 678") }
    var cashinAmountInput by remember { mutableStateOf("10000") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var lastIssuedVoucherCode by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("agent_portal_screen"),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
    ) {
        // Agent Header & Shop Profile
        item {
            Surface(
                color = CardBackground,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth().testTag("agent_profile_card")
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF142B20), Color(0xFF161F26))
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "AGENT SHOP MANAGEMENT",
                                color = GoldPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = agent.name,
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(EmeraldGreen)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(agent.agentCode, color = LuxuryBlack, fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "📍 ${agent.shopLocation} • ${agent.phone}", color = TextSecondary, fontSize = 11.sp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Float Balance & Commission KPIs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Float Card
                Surface(
                    color = CardBackground,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "Cashier Float", color = TextMuted, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "TSh %,d".format(agent.floatBalance.toLong()),
                            color = GoldPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { onTopupFloat(100000.0) },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant, contentColor = GoldPrimary),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp).testTag("topup_float_button")
                        ) {
                            Text("+100K Float", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Commission Card
                Surface(
                    color = CardBackground,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "Commission (10%)", color = TextMuted, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "TSh %,d".format(agent.totalCommissionEarned.toLong()),
                            color = EmeraldGreen,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Active Bettors: ${agent.activeCustomersCount}", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Customer Cash-In / Voucher Fulfill Form
        item {
            Surface(
                color = CardBackground,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth().testTag("cashin_form_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "CUSTOMER CASH-IN & VOUCHER GENERATOR",
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Deposit cash directly into customer's account or issue a print betting voucher.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = customerPhoneInput,
                        onValueChange = { customerPhoneInput = it },
                        label = { Text("Customer Phone Number") },
                        modifier = Modifier.fillMaxWidth().testTag("agent_customer_phone_input"),
                        leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = CardBorder,
                            focusedContainerColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkSurfaceVariant,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = cashinAmountInput,
                        onValueChange = { cashinAmountInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Deposit Amount (TSh)") },
                        modifier = Modifier.fillMaxWidth().testTag("agent_cashin_amount_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = CardBorder,
                            focusedContainerColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkSurfaceVariant,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val amt = cashinAmountInput.toDoubleOrNull() ?: 5000.0
                            onCustomerCashin(customerPhoneInput, amt)
                            lastIssuedVoucherCode = "EL-" + (100000..999999).random()
                            showSuccessDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen, contentColor = LuxuryBlack),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("fulfill_cashin_button")
                    ) {
                        Text("FULFILL CASH-IN (+10% COMMISSION)", fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Voucher & Customer Transaction Log
        item {
            Text(
                text = "RECENT AGENT TRANSACTIONS",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(vouchers, key = { it.code }) { voucher ->
            val dateStr = remember(voucher.createdAt) {
                val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
                sdf.format(Date(voucher.createdAt))
            }

            Surface(
                color = CardBackground,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = voucher.code, color = GoldPrimary, fontWeight = FontWeight.Black, fontSize = 13.sp)
                        Text(text = "${voucher.customerPhone} • $dateStr", color = TextMuted, fontSize = 11.sp)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "TSh %,d".format(voucher.amount.toLong()), color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(
                            text = "+%,d Commission".format((voucher.amount * 0.1).toLong()),
                            color = EmeraldGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cash-In Fulfilled!", color = EmeraldGreen, fontWeight = FontWeight.Black, fontSize = 16.sp)
                }
            },
            text = {
                Column {
                    Text("Customer wallet credited successfully.", color = TextPrimary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Voucher PIN: $lastIssuedVoucherCode", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Commission added to Agent Float: TSh %,d".format(((cashinAmountInput.toDoubleOrNull() ?: 10000.0) * 0.1).toLong()), color = EmeraldGreen, fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSuccessDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = LuxuryBlack)
                ) {
                    Text("Done", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = DarkSurfaceVariant
        )
    }
}
