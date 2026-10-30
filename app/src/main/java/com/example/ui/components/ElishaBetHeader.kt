package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppRole
import com.example.model.UserProfile
import com.example.ui.Screen
import com.example.ui.theme.*

@Composable
fun ElishaBetHeader(
    user: UserProfile,
    currentRole: AppRole,
    onRoleSelected: (AppRole) -> Unit,
    onNavigateWallet: () -> Unit,
    onNavigateSupport: () -> Unit,
    onNavigateResponsibleGaming: () -> Unit
) {
    var showRoleMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        LuxuryBlack,
                        DarkSurface
                    )
                )
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("elisha_bet_header")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Logo & Slogan
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(GoldPrimary, GoldDark)
                            )
                        )
                        .border(1.dp, EmeraldGreen, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "E",
                        color = LuxuryBlack,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "ELISHA",
                            color = GoldPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "BET",
                            color = EmeraldGreen,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "BET SMART. WIN BIG.",
                        color = TextSecondary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            // Role Switcher & Balance
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Role Switcher Pill
                Box {
                    Surface(
                        color = when (currentRole) {
                            AppRole.CUSTOMER -> Color(0xFF1E293B)
                            AppRole.AGENT -> Color(0xFF14532D)
                            AppRole.ADMIN -> Color(0xFF7C2D12)
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .clickable { showRoleMenu = true }
                            .border(
                                1.dp,
                                when (currentRole) {
                                    AppRole.CUSTOMER -> GoldPrimary
                                    AppRole.AGENT -> EmeraldGreen
                                    AppRole.ADMIN -> LiveRed
                                },
                                RoundedCornerShape(16.dp)
                            )
                            .testTag("role_switcher_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = currentRole.badge,
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Switch Role",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showRoleMenu,
                        onDismissRequest = { showRoleMenu = false },
                        modifier = Modifier.background(DarkSurfaceVariant)
                    ) {
                        AppRole.values().forEach { role ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = role.title,
                                            fontWeight = if (role == currentRole) FontWeight.Bold else FontWeight.Normal,
                                            color = if (role == currentRole) GoldPrimary else TextPrimary,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = when (role) {
                                                AppRole.CUSTOMER -> "Place bets, manage wallet, live matches"
                                                AppRole.AGENT -> "Customer cash-in, commissions, vouchers"
                                                AppRole.ADMIN -> "Adjust odds, manage matches & risk"
                                            },
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                },
                                onClick = {
                                    showRoleMenu = false
                                    onRoleSelected(role)
                                }
                            )
                        }
                    }
                }

                // Balance Pill (Tap opens wallet)
                Surface(
                    color = CardBackground,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .clickable { onNavigateWallet() }
                        .border(1.dp, EmeraldGreen.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                        .testTag("wallet_balance_pill")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "TSh %,d".format(user.realBalance.toLong()),
                                color = EmeraldGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Deposit",
                                tint = LuxuryBlack,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
