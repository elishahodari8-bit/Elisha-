package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
import com.example.model.BetStatus
import com.example.model.PlacedBet
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BetHistoryScreen(
    placedBets: List<PlacedBet>,
    onCashOut: (String) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredBets = remember(placedBets, selectedFilter) {
        when (selectedFilter) {
            "OPEN" -> placedBets.filter { it.status == BetStatus.OPEN }
            "WON" -> placedBets.filter { it.status == BetStatus.WON }
            "SETTLED" -> placedBets.filter { it.status != BetStatus.OPEN }
            else -> placedBets
        }
    }

    val openCount = remember(placedBets) { placedBets.count { it.status == BetStatus.OPEN } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("bet_history_screen"),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
    ) {
        item {
            Text(
                text = "MY BETTING HISTORY",
                color = GoldPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Filter Tabs
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                val filters = listOf(
                    "ALL" to "All Bets (${placedBets.size})",
                    "OPEN" to "Open ($openCount)",
                    "WON" to "Won",
                    "SETTLED" to "Settled"
                )

                items(filters) { (key, label) ->
                    val isSelected = selectedFilter == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = key },
                        label = { Text(label, fontWeight = FontWeight.Bold, fontSize = 12.sp) },
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
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
        }

        if (filteredBets.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎫", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No bets found", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Select matches from the sports dashboard to place bets.", color = TextMuted, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(filteredBets, key = { it.id }) { bet ->
                PlacedBetCard(bet = bet, onCashOut = { onCashOut(bet.id) })
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun PlacedBetCard(
    bet: PlacedBet,
    onCashOut: () -> Unit
) {
    val dateStr = remember(bet.timestamp) {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        sdf.format(Date(bet.timestamp))
    }

    Surface(
        color = CardBackground,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            when (bet.status) {
                BetStatus.WON -> EmeraldGreen.copy(alpha = 0.7f)
                BetStatus.OPEN -> GoldPrimary.copy(alpha = 0.5f)
                else -> CardBorder
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("placed_bet_card_${bet.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Bet ID, Date & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = bet.id,
                        color = GoldPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                    Text(
                        text = dateStr,
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when (bet.status) {
                                BetStatus.OPEN -> Color(0xFF1E293B)
                                BetStatus.WON -> EmeraldGreen.copy(alpha = 0.2f)
                                BetStatus.LOST -> LiveRed.copy(alpha = 0.2f)
                                BetStatus.CASHED_OUT -> AccentOrange.copy(alpha = 0.2f)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = when (bet.status) {
                            BetStatus.OPEN -> "OPEN"
                            BetStatus.WON -> "WON ✓"
                            BetStatus.LOST -> "LOST"
                            BetStatus.CASHED_OUT -> "CASHED OUT"
                        },
                        color = when (bet.status) {
                            BetStatus.OPEN -> GoldLight
                            BetStatus.WON -> EmeraldGreen
                            BetStatus.LOST -> LiveRed
                            BetStatus.CASHED_OUT -> AccentOrange
                        },
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                    )
                }
            }

            Divider(color = CardBorder, modifier = Modifier.padding(vertical = 10.dp))

            // Selections list
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                bet.items.forEach { leg ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${leg.sport.iconSymbol} ${leg.matchTitle}",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${leg.marketName}: ${leg.optionName}",
                                color = GoldLight,
                                fontSize = 11.sp
                            )
                        }
                        Text(
                            text = "@ %.2f".format(leg.odds),
                            color = EmeraldGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Divider(color = CardBorder, modifier = Modifier.padding(vertical = 10.dp))

            // Financial Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Stake: TSh %,d".format(bet.stake.toLong()), color = TextSecondary, fontSize = 11.sp)
                    Text(text = "Total Odds: %.2f".format(bet.totalOdds), color = TextMuted, fontSize = 11.sp)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (bet.status == BetStatus.WON) "Paid Out" else "Potential Return",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "TSh %,d".format(bet.potentialReturn.toLong()),
                        color = if (bet.status == BetStatus.WON) EmeraldGreen else GoldPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    )
                }
            }

            // Cash Out Action Button (if OPEN and cashoutValue > 0)
            if (bet.status == BetStatus.OPEN && bet.cashoutValue > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onCashOut,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFC2410C),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("cashout_button_${bet.id}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "⚡ CASH OUT NOW (TSh %,d)".format(bet.cashoutValue.toLong()),
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
