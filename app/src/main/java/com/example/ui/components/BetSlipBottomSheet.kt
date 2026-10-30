package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BetSlipItem
import com.example.ui.theme.*

@Composable
fun BetSlipFloatingBar(
    itemCount: Int,
    totalOdds: Double,
    accaBonusPercentage: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (itemCount == 0) return

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .clickable { onClick() }
            .testTag("floating_bet_slip_bar"),
        shape = RoundedCornerShape(16.dp),
        color = CardBackground,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF1B232D),
                            Color(0xFF142B20)
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(GoldPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$itemCount",
                        color = LuxuryBlack,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "BET SLIP",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Odds: %.2f".format(totalOdds) + if (accaBonusPercentage > 0) " • +%.0f%% Boost".format(accaBonusPercentage) else "",
                        color = EmeraldGreen,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "View Slip",
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Expand",
                    tint = GoldPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BetSlipModal(
    items: List<BetSlipItem>,
    stake: Double,
    totalOdds: Double,
    accaBonusPercentage: Double,
    potentialReturn: Double,
    onStakeChange: (Double) -> Unit,
    onRemoveItem: (String) -> Unit,
    onClearAll: () -> Unit,
    onPlaceBet: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = CardBorder) },
        modifier = Modifier.testTag("bet_slip_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "BET SLIP",
                        color = GoldPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(EmeraldGreen.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${items.size} Selections",
                            color = EmeraldGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row {
                    TextButton(
                        onClick = onClearAll,
                        modifier = Modifier.testTag("clear_bet_slip_button")
                    ) {
                        Text("Clear All", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Acca Bonus Notice if applicable
            if (items.size >= 2) {
                Surface(
                    color = EmeraldSurface,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🔥", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ACCUMULATOR BOOST: +%.0f%% Extra Cash Added!".format(accaBonusPercentage),
                            color = EmeraldGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Selections List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 220.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items, key = { it.optionId }) { item ->
                    SelectionItemRow(item = item, onRemove = { onRemoveItem(item.optionId) })
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stake Input and Quick Chips
            Text(
                text = "Stake Amount (TSh)",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))

            var stakeText by remember(stake) { mutableStateOf(stake.toLong().toString()) }

            OutlinedTextField(
                value = stakeText,
                onValueChange = { newText ->
                    val clean = newText.filter { it.isDigit() }
                    stakeText = clean
                    clean.toDoubleOrNull()?.let { onStakeChange(it) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("stake_input_field"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = {
                    Text(
                        text = "TSh",
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = CardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = CardBackground,
                    unfocusedContainerColor = CardBackground
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(1000.0, 2000.0, 5000.0, 10000.0, 50000.0).forEach { chipAmount ->
                    SuggestionChip(
                        onClick = { onStakeChange(chipAmount) },
                        label = {
                            Text(
                                text = "+${chipAmount.toLong() / 1000}K",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (stake == chipAmount) LuxuryBlack else TextPrimary
                            )
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = if (stake == chipAmount) GoldPrimary else DarkSurfaceVariant
                        ),
                        border = SuggestionChipDefaults.suggestionChipBorder(
                            enabled = true,
                            borderColor = if (stake == chipAmount) GoldPrimary else CardBorder
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Payout Summary Card
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
                        Text(text = "Total Odds", color = TextSecondary, fontSize = 12.sp)
                        Text(text = "%.2f".format(totalOdds), color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    if (accaBonusPercentage > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Acca Boost Bonus", color = EmeraldGreen, fontSize = 12.sp)
                            Text(text = "+%.0f%%".format(accaBonusPercentage), color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    Divider(color = CardBorder, modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Potential Payout", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            text = "TSh %,d".format(potentialReturn.toLong()),
                            color = GoldPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Big Place Bet Button
            Button(
                onClick = onPlaceBet,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("place_bet_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldGreen,
                    contentColor = LuxuryBlack
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "PLACE BET (TSh %,d)".format(stake.toLong()),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun SelectionItemRow(
    item: BetSlipItem,
    onRemove: () -> Unit
) {
    Surface(
        color = DarkSurfaceVariant,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.matchTitle,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.optionName,
                        color = GoldLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${item.marketName})",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "%.2f".format(item.odds),
                    color = EmeraldGreen,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(10.dp))
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove Selection",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
