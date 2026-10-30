package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AuditLog
import com.example.model.Match
import com.example.model.PlatformMetrics
import com.example.model.TransactionRecord
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminPortalScreen(
    metrics: PlatformMetrics,
    matches: List<Match>,
    pendingWithdrawals: List<TransactionRecord>,
    auditLogs: List<AuditLog>,
    onUpdateOdds: (String, String, String, Double) -> Unit,
    onToggleMarket: (String, String) -> Unit,
    onSettleMatch: (String, Int, Int) -> Unit,
    onApproveWithdrawal: (String) -> Unit,
    onRejectWithdrawal: (String, String) -> Unit
) {
    var adminSubTab by remember { mutableStateOf("OVERVIEW") } // "OVERVIEW", "ODDS_MATCHES", "PAYMENTS", "AUDIT"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_portal_screen"),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
    ) {
        // Admin Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ELISHA BET SUPER ADMIN HQ",
                        color = GoldPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(text = "Risk Management, Odds & Financial Controls", color = TextSecondary, fontSize = 11.sp)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(LiveRed)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("HQ ROOT", color = Color.White, fontWeight = FontWeight.Black, fontSize = 10.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Sub Tabs
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                val tabs = listOf(
                    "OVERVIEW" to "Overview & Risk",
                    "ODDS_MATCHES" to "Match & Odds (${matches.size})",
                    "PAYMENTS" to "Payout Approvals",
                    "AUDIT" to "Audit Logs (${auditLogs.size})"
                )
                items(tabs) { (key, label) ->
                    val isSelected = adminSubTab == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { adminSubTab = key },
                        label = { Text(label, fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldPrimary,
                            selectedLabelColor = LuxuryBlack,
                            containerColor = DarkSurfaceVariant,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) GoldPrimary else CardBorder
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            }
        }

        when (adminSubTab) {
            "OVERVIEW" -> {
                // KPI Metrics Grid
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            MetricBox(title = "Total Registered Users", value = "%,d".format(metrics.totalUsers), sub = "+240 today", modifier = Modifier.weight(1f))
                            MetricBox(title = "Active Bettors", value = "%,d".format(metrics.activeBettors), sub = "85% on Mobile", modifier = Modifier.weight(1f))
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            MetricBox(title = "Gross Gaming Revenue", value = "TSh %,dM".format((metrics.ggr / 1000000).toLong()), sub = "Margin: 13.4%", color = EmeraldGreen, modifier = Modifier.weight(1f))
                            MetricBox(title = "Platform Float Reserves", value = "TSh %,dM".format((metrics.platformFloat / 1000000).toLong()), sub = "Bank Guarantee OK", color = GoldPrimary, modifier = Modifier.weight(1f))
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            MetricBox(title = "Total Deposits", value = "TSh %,dM".format((metrics.totalDeposits / 1000000).toLong()), sub = "Mobile Money 94%", modifier = Modifier.weight(1f))
                            MetricBox(title = "Total Withdrawals", value = "TSh %,dM".format((metrics.totalWithdrawals / 1000000).toLong()), sub = "Avg payout: 4 mins", modifier = Modifier.weight(1f))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Risk Management Panel
                    Surface(
                        color = CardBackground,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                        modifier = Modifier.fillMaxWidth().testTag("risk_management_card")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("🛡️ RISK & LIABILITY CONTROLS", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("• Max Single Match Liability Cap: TSh 50,000,000 (Safe)", color = TextPrimary, fontSize = 12.sp)
                            Text("• Max Payout Multiplier per Ticket: 10,000x / TSh 100,000,000", color = TextSecondary, fontSize = 12.sp)
                            Text("• Automated Arbitrage / Bot Detection: ENABLED", color = EmeraldGreen, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("• Regulatory Sanctions Screening: Passed (0 Flags)", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }

            "ODDS_MATCHES" -> {
                item {
                    Text("ACTIVE MATCHES & ODDS CONTROLS", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(matches, key = { it.id }) { match ->
                    Surface(
                        color = CardBackground,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                        modifier = Modifier.fillMaxWidth().testTag("admin_match_card_${match.id}")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "${match.homeTeam} vs ${match.awayTeam}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                if (match.isLive) {
                                    Text(text = "LIVE ${match.homeScore}-${match.awayScore}", color = LiveRed, fontWeight = FontWeight.Black, fontSize = 11.sp)
                                } else {
                                    Text(text = match.startTime, color = TextMuted, fontSize = 10.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // First market odds manipulation
                            val market = match.markets.firstOrNull()
                            if (market != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    market.options.forEach { opt ->
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(DarkSurfaceVariant)
                                                .padding(6.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(text = opt.name.take(7), color = TextMuted, fontSize = 10.sp)
                                            Text(text = "%.2f".format(opt.odds), color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)

                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row {
                                                // +0.10
                                                Button(
                                                    onClick = { onUpdateOdds(match.id, market.id, opt.id, opt.odds + 0.10) },
                                                    contentPadding = PaddingValues(0.dp),
                                                    modifier = Modifier.size(24.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = LuxuryBlack)
                                                ) {
                                                    Text("+", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                }
                                                Spacer(modifier = Modifier.width(4.dp))
                                                // -0.10
                                                Button(
                                                    onClick = { onUpdateOdds(match.id, market.id, opt.id, (opt.odds - 0.10).coerceAtLeast(1.05)) },
                                                    contentPadding = PaddingValues(0.dp),
                                                    modifier = Modifier.size(24.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = CardBorder, contentColor = TextPrimary)
                                                ) {
                                                    Text("-", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Suspend / Resume Market
                                    Button(
                                        onClick = { onToggleMarket(match.id, market.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant, contentColor = GoldLight),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1f).height(32.dp)
                                    ) {
                                        Text("Toggle Suspension", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }

                                    // Settle Match (Quick Test)
                                    Button(
                                        onClick = { onSettleMatch(match.id, match.homeScore + 1, match.awayScore) },
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark, contentColor = Color.White),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1f).height(32.dp)
                                    ) {
                                        Text("Settle Final Score", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            "PAYMENTS" -> {
                item {
                    Text("PENDING WITHDRAWAL APPROVALS", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (pendingWithdrawals.isEmpty()) {
                    item {
                        Surface(
                            color = CardBackground,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().padding(20.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("✓", color = EmeraldGreen, fontSize = 28.sp, fontWeight = FontWeight.Black)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("All pending payouts cleared", color = TextPrimary, fontWeight = FontWeight.Bold)
                                Text("No transactions waiting in the queue.", color = TextMuted, fontSize = 11.sp)
                            }
                        }
                    }
                } else {
                    items(pendingWithdrawals, key = { it.id }) { tx ->
                        Surface(
                            color = CardBackground,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = tx.reference, color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(text = "TSh %,d".format(tx.amount.toLong()), color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                }
                                Text(text = tx.note, color = TextSecondary, fontSize = 11.sp)

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { onApproveWithdrawal(tx.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen, contentColor = LuxuryBlack),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1f).height(32.dp)
                                    ) {
                                        Text("Approve Payout", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                    Button(
                                        onClick = { onRejectWithdrawal(tx.id, "KYC discrepancy") },
                                        colors = ButtonDefaults.buttonColors(containerColor = LiveRed.copy(alpha = 0.2f), contentColor = LiveRed),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1f).height(32.dp)
                                    ) {
                                        Text("Reject", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }

            "AUDIT" -> {
                item {
                    Text("AUTOMATED REGULATORY AUDIT LOGS", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(auditLogs, key = { it.id }) { log ->
                    val dateStr = remember(log.timestamp) {
                        val sdf = SimpleDateFormat("dd MMM, HH:mm:ss", Locale.getDefault())
                        sdf.format(Date(log.timestamp))
                    }

                    Surface(
                        color = CardBackground,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "[${log.action}]", color = GoldLight, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text(text = dateStr, color = TextMuted, fontSize = 10.sp)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = log.details, color = TextPrimary, fontSize = 11.sp)
                            Text(text = "Actor: ${log.actor} (${log.role})", color = TextMuted, fontSize = 10.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    }
}

@Composable
fun MetricBox(
    title: String,
    value: String,
    sub: String,
    modifier: Modifier = Modifier,
    color: Color = TextPrimary
) {
    Surface(
        color = CardBackground,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title, color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = color, fontSize = 16.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = sub, color = TextSecondary, fontSize = 10.sp)
        }
    }
}
