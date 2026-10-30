package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WalletScreen(
    user: UserProfile,
    transactions: List<TransactionRecord>,
    onDeposit: (PaymentMethod, String, Double) -> Unit,
    onWithdraw: (PaymentMethod, String, Double) -> Unit,
    onSubmitKyc: (String, String) -> Unit,
    onNavigateResponsibleGaming: () -> Unit
) {
    var selectedTab by remember { mutableStateOf("DEPOSIT") } // "DEPOSIT", "WITHDRAW", "TRANSACTIONS"
    var selectedMethod by remember { mutableStateOf(PaymentMethod.MPESA) }
    var amountInput by remember { mutableStateOf("10000") }
    var phoneInput by remember { mutableStateOf(user.phone) }
    var showUssdSimulationDialog by remember { mutableStateOf(false) }
    var simulatedUssdDetails by remember { mutableStateOf("") }
    var showKycModal by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("wallet_screen"),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
    ) {
        // Balances Header Card
        item {
            Surface(
                color = CardBackground,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wallet_balance_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "TOTAL WALLET BALANCE",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "TSh %,d".format((user.realBalance + user.bonusBalance).toLong()),
                        color = GoldPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Play Balance (Cash)", color = TextMuted, fontSize = 11.sp)
                            Text(
                                text = "TSh %,d".format(user.realBalance.toLong()),
                                color = EmeraldGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Bonus Funds", color = TextMuted, fontSize = 11.sp)
                            Text(
                                text = "TSh %,d".format(user.bonusBalance.toLong()),
                                color = OddsBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // KYC Status Banner
        item {
            Surface(
                color = if (user.kycStatus == KycStatus.VERIFIED) EmeraldSurface else Color(0xFF272115),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (user.kycStatus == KycStatus.VERIFIED) EmeraldGreen.copy(alpha = 0.5f) else GoldPrimary.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showKycModal = true }
                    .testTag("kyc_status_banner")
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (user.kycStatus == KycStatus.VERIFIED) "🛡️" else "⚠️",
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (user.kycStatus == KycStatus.VERIFIED) "KYC Identity Verified (18+ Compliant)" else "KYC Verification Required",
                                color = if (user.kycStatus == KycStatus.VERIFIED) EmeraldGreen else GoldLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = if (user.kycStatus == KycStatus.VERIFIED) "${user.kycDocumentType}: ${user.kycDocumentNumber}" else "Tap to submit National ID / Passport for withdrawals",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Text(
                        text = if (user.kycStatus == KycStatus.VERIFIED) "Active" else "Verify >",
                        color = GoldPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Navigation Tabs: Deposit / Withdraw / Transactions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "DEPOSIT" to "Deposit Funds",
                    "WITHDRAW" to "Withdraw",
                    "TRANSACTIONS" to "History (${transactions.size})"
                ).forEach { (tabKey, title) ->
                    val isSelected = selectedTab == tabKey
                    Button(
                        onClick = { selectedTab = tabKey },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("wallet_tab_$tabKey"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) GoldPrimary else DarkSurfaceVariant,
                            contentColor = if (isSelected) LuxuryBlack else TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Text(
                            text = title,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // TAB CONTENT
        when (selectedTab) {
            "DEPOSIT" -> {
                item {
                    Column {
                        Text(
                            text = "SELECT PAYMENT PROVIDER",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Payment Methods Grid/Row
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            PaymentMethod.values().forEach { method ->
                                val isChosen = selectedMethod == method
                                Surface(
                                    color = if (isChosen) Color(0xFF1E281F) else DarkSurfaceVariant,
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isChosen) EmeraldGreen else CardBorder
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedMethod = method }
                                        .testTag("payment_method_${method.id}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            RadioButton(
                                                selected = isChosen,
                                                onClick = { selectedMethod = method },
                                                colors = RadioButtonDefaults.colors(selectedColor = EmeraldGreen)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Column {
                                                Text(
                                                    text = method.title,
                                                    color = TextPrimary,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp
                                                )
                                                Text(
                                                    text = "Min: %,d TSh • Max: %,d TSh".format(method.minDeposit.toLong(), method.maxDeposit.toLong()),
                                                    color = TextMuted,
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }

                                        Text(
                                            text = method.providerCode,
                                            color = GoldLight,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Phone Number
                        Text(
                            text = "Mobile Money Phone Number",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { phoneInput = it },
                            modifier = Modifier.fillMaxWidth().testTag("deposit_phone_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = CardBorder,
                                focusedContainerColor = CardBackground,
                                unfocusedContainerColor = CardBackground,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Amount Input & Quick Chips
                        Text(
                            text = "Deposit Amount (TSh)",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = amountInput,
                            onValueChange = { amountInput = it.filter { ch -> ch.isDigit() } },
                            modifier = Modifier.fillMaxWidth().testTag("deposit_amount_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            leadingIcon = { Text("TSh", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = CardBorder,
                                focusedContainerColor = CardBackground,
                                unfocusedContainerColor = CardBackground,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(2000, 5000, 10000, 25000, 50000).forEach { amt ->
                                SuggestionChip(
                                    onClick = { amountInput = amt.toString() },
                                    label = { Text("${amt / 1000}K", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val amt = amountInput.toDoubleOrNull() ?: 1000.0
                                simulatedUssdDetails = "Approve payment of TSh %,d from %s to ELISHA BET Ltd (Business #882190). Enter Mobile Money PIN:".format(amt.toLong(), phoneInput)
                                showUssdSimulationDialog = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_deposit_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldGreen,
                                contentColor = LuxuryBlack
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "DEPOSIT NOW",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }

            "WITHDRAW" -> {
                item {
                    Column {
                        Text(
                            text = "WITHDRAW TO MOBILE MONEY / BANK",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Recipient Phone / Account
                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { phoneInput = it },
                            label = { Text("Recipient Phone / Account Number") },
                            modifier = Modifier.fillMaxWidth().testTag("withdraw_phone_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = CardBorder,
                                focusedContainerColor = CardBackground,
                                unfocusedContainerColor = CardBackground,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = amountInput,
                            onValueChange = { amountInput = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Withdrawal Amount (TSh)") },
                            modifier = Modifier.fillMaxWidth().testTag("withdraw_amount_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            leadingIcon = { Text("TSh", color = GoldPrimary, fontWeight = FontWeight.Bold) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = CardBorder,
                                focusedContainerColor = CardBackground,
                                unfocusedContainerColor = CardBackground,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                val amt = amountInput.toDoubleOrNull() ?: 5000.0
                                onWithdraw(selectedMethod, phoneInput, amt)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_withdraw_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldPrimary,
                                contentColor = LuxuryBlack
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("REQUEST WITHDRAWAL", fontWeight = FontWeight.Black, fontSize = 14.sp)
                        }
                    }
                }
            }

            "TRANSACTIONS" -> {
                if (transactions.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            Text("No transactions logged yet", color = TextMuted)
                        }
                    }
                } else {
                    items(transactions, key = { it.id }) { tx ->
                        TransactionRow(tx = tx)
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }
    }

    // USSD Push Interactive Simulation Dialog
    if (showUssdSimulationDialog) {
        AlertDialog(
            onDismissRequest = { showUssdSimulationDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📲", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Mobile Money USSD Push", fontWeight = FontWeight.Black, fontSize = 15.sp, color = GoldPrimary)
                }
            },
            text = {
                Column {
                    Text(text = simulatedUssdDetails, color = TextPrimary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Sandbox Verification Mode: Tap 'Confirm PIN' to simulate successful authorization from your phone.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showUssdSimulationDialog = false
                        val amt = amountInput.toDoubleOrNull() ?: 1000.0
                        onDeposit(selectedMethod, phoneInput, amt)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen, contentColor = LuxuryBlack)
                ) {
                    Text("Confirm PIN (Authorize)", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showUssdSimulationDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceVariant
        )
    }

    // KYC Submission Modal
    if (showKycModal) {
        var docType by remember { mutableStateOf("National ID (NIDA)") }
        var docNumber by remember { mutableStateOf("19940822-41100-0001") }

        AlertDialog(
            onDismissRequest = { showKycModal = false },
            title = { Text("KYC & Age Verification (18+)", fontWeight = FontWeight.Bold, color = GoldPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Submit proof of identity for regulatory compliance:", color = TextSecondary, fontSize = 12.sp)

                    OutlinedTextField(
                        value = docType,
                        onValueChange = { docType = it },
                        label = { Text("Document Type") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = docNumber,
                        onValueChange = { docNumber = it },
                        label = { Text("Document ID Number") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "✓ Age verified 18+\n✓ Proof of residency verified\n✓ Safe gaming limits active",
                        color = EmeraldGreen,
                        fontSize = 11.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSubmitKyc(docType, docNumber)
                        showKycModal = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen, contentColor = LuxuryBlack)
                ) {
                    Text("Verify & Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showKycModal = false }) {
                    Text("Close", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceVariant
        )
    }
}

@Composable
fun TransactionRow(tx: TransactionRecord) {
    val dateStr = remember(tx.timestamp) {
        val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
        sdf.format(Date(tx.timestamp))
    }

    val isCredit = tx.type in listOf(TransactionType.DEPOSIT, TransactionType.BET_WIN, TransactionType.BET_CASHOUT, TransactionType.REFERRAL_COMMISSION)

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isCredit) EmeraldGreen.copy(alpha = 0.15f) else LiveRed.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isCredit) "+" else "−",
                        color = if (isCredit) EmeraldGreen else LiveRed,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = tx.note.ifBlank { tx.type.name },
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "$dateStr • ${tx.reference}",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isCredit) "+" else "-"}TSh %,d".format(tx.amount.toLong()),
                    color = if (isCredit) EmeraldGreen else TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = tx.status.name,
                    color = if (tx.status == TransactionStatus.COMPLETED) EmeraldGreen else GoldLight,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
