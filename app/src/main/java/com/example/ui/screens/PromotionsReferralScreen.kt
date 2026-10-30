package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Promotion
import com.example.model.UserProfile
import com.example.ui.theme.*

@Composable
fun PromotionsReferralScreen(
    user: UserProfile,
    promotions: List<Promotion>,
    onClaimPromo: (String) -> Unit,
    onCopyReferral: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("promotions_referral_screen"),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
    ) {
        item {
            Text(
                text = "PROMOTIONS & REWARDS",
                color = GoldPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Referral Card
        item {
            Surface(
                color = CardBackground,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("referral_program_card")
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF1E2822), Color(0xFF181F26))
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🎁 REFER & EARN 10,000 TSh",
                            color = GoldPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(EmeraldGreen)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("VIP TIER", color = LuxuryBlack, fontWeight = FontWeight.Black, fontSize = 10.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Share your unique referral code with friends. Earn 10,000 TSh instantly when they deposit and place their first bet!",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Code Display and Share Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurfaceVariant)
                            .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Your Referral Code", color = TextMuted, fontSize = 10.sp)
                            Text(
                                text = user.referralCode,
                                color = GoldPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                letterSpacing = 1.sp
                            )
                        }

                        Button(
                            onClick = onCopyReferral,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = LuxuryBlack),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp).testTag("copy_referral_code_button")
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy Code", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Referral Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Invited Friends", color = TextMuted, fontSize = 11.sp)
                            Text(text = "${user.referralCount} Bettors", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Total Commission Earned", color = TextMuted, fontSize = 11.sp)
                            Text(
                                text = "TSh %,d".format(user.referralEarnings.toLong()),
                                color = EmeraldGreen,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Promotions Header
        item {
            Text(
                text = "ACTIVE SPORTSBOOK PROMOTIONS",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Promotions List
        items(promotions, key = { it.id }) { promo ->
            Surface(
                color = CardBackground,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("promo_card_${promo.id}")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(GoldPrimary.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = promo.tag,
                                color = GoldPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Text(
                            text = "Code: ${promo.code}",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = promo.title,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = promo.description,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Max Bonus: TSh %,d".format(promo.maxBonusTsh.toLong()),
                            color = EmeraldGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Button(
                            onClick = { onClaimPromo(promo.id) },
                            enabled = !promo.isClaimed,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (promo.isClaimed) DarkSurfaceVariant else GoldPrimary,
                                contentColor = if (promo.isClaimed) TextMuted else LuxuryBlack
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp).testTag("claim_promo_button_${promo.id}")
                        ) {
                            Text(
                                text = if (promo.isClaimed) "CLAIMED ✓" else "CLAIM BONUS",
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
