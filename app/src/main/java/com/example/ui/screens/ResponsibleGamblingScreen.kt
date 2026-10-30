package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.model.UserProfile
import com.example.ui.theme.*

@Composable
fun ResponsibleGamblingScreen(
    user: UserProfile,
    onUpdateDepositLimit: (Double) -> Unit,
    onToggleSelfExclusion: (Boolean) -> Unit
) {
    var depositLimitSlider by remember(user.dailyDepositLimit) { mutableStateOf(user.dailyDepositLimit.toFloat()) }
    var selfExclusionEnabled by remember(user.selfExclusionActive) { mutableStateOf(user.selfExclusionActive) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("responsible_gambling_screen"),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
    ) {
        item {
            Text(
                text = "RESPONSIBLE GAMBLING & COMPLIANCE",
                color = GoldPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Regulatory & 18+ Strict Banner
        item {
            Surface(
                color = CardBackground,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, LiveRed.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(LiveRed)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("18+", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "STRICT AGE RESTRICTION & LICENSING",
                            color = LiveRed,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Gambling is prohibited for minors under 18 years old. ELISHA BET operates strictly in Sandbox Compliance Mode under Gaming Board of Tanzania regulatory guidelines. Real-money wagering is held disabled pending full operational licensing.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Daily Deposit Limit Controller
        item {
            Surface(
                color = CardBackground,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth().testTag("deposit_limit_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Deposit Limit",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "TSh %,d".format(depositLimitSlider.toLong()),
                            color = GoldPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Prevent excessive expenditure by setting a firm cap on your daily deposits.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Slider(
                        value = depositLimitSlider,
                        onValueChange = { depositLimitSlider = it },
                        valueRange = 50000f..5000000f,
                        steps = 9,
                        colors = SliderDefaults.colors(
                            thumbColor = GoldPrimary,
                            activeTrackColor = GoldPrimary,
                            inactiveTrackColor = DarkSurfaceVariant
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("50K TSh", color = TextMuted, fontSize = 10.sp)
                        Text("2.5M TSh", color = TextMuted, fontSize = 10.sp)
                        Text("5M TSh", color = TextMuted, fontSize = 10.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { onUpdateDepositLimit(depositLimitSlider.toDouble()) },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = LuxuryBlack),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("save_deposit_limit_button")
                    ) {
                        Text("SAVE DAILY LIMIT", fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Self-Exclusion / Cool-Off Mode
        item {
            Surface(
                color = CardBackground,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth().testTag("self_exclusion_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Self-Exclusion Cool-Off",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = if (selfExclusionEnabled) "Active: Betting and deposits are locked" else "Take a break from sports betting (24h - 30 days)",
                                color = if (selfExclusionEnabled) LiveRed else TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Switch(
                            checked = selfExclusionEnabled,
                            onCheckedChange = {
                                selfExclusionEnabled = it
                                onToggleSelfExclusion(it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = LiveRed,
                                checkedTrackColor = LiveRed.copy(alpha = 0.3f),
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = DarkSurfaceVariant
                            ),
                            modifier = Modifier.testTag("self_exclusion_switch")
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Support & Helplines
        item {
            Surface(
                color = DarkSurfaceVariant,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "HELP & ADVICE RESOURCES",
                        color = GoldLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• Gaming Board of Tanzania Responsible Gaming Helpline: 0800 110 055\n• GamCare International Support: www.gamcare.org.uk\n• Gamblers Anonymous East Africa: support@ga-ea.org\n• All bets are meant for entertainment. Never wager funds required for daily necessities.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
