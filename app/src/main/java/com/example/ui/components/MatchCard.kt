package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BetMarket
import com.example.model.BetOption
import com.example.model.BetSlipItem
import com.example.model.Match
import com.example.model.MatchStats
import com.example.model.Sport
import com.example.ui.theme.ElishaBetTheme

/**
 * MatchCard composable representing a fixture in the ELISHA BET sportsbook.
 * Styled with luxury black surfaces, emerald green accents, and gold typography from Theme.kt.
 */
@Composable
fun MatchCard(
    match: Match,
    selectedOptionIds: Set<String>,
    onOptionSelected: (BetSlipItem) -> Unit,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = ElishaBetTheme.extendedColors
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val shapes = MaterialTheme.shapes

    // Animated pulsing for live match beacon
    val infiniteTransition = rememberInfiniteTransition(label = "live_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beacon_alpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("match_card_${match.id}"),
        colors = CardDefaults.cardColors(
            containerColor = extendedColors.cardBackground
        ),
        shape = shapes.medium,
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (match.isLive) colorScheme.secondary.copy(alpha = 0.35f) else colorScheme.outline
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header: League / Sport & Live Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = match.sport.iconSymbol,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = match.league,
                        style = typography.labelMedium,
                        color = colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (match.isLive) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(colorScheme.secondaryContainer)
                            .border(1.dp, colorScheme.secondary.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(colorScheme.secondary.copy(alpha = pulseAlpha))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${match.liveMinute}' LIVE",
                            style = typography.labelSmall,
                            color = colorScheme.secondary,
                            fontWeight = FontWeight.Black
                        )
                    }
                } else {
                    Text(
                        text = match.displayStatus,
                        style = typography.labelSmall,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Teams & Scores Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = match.homeTeam,
                        style = typography.titleMedium,
                        color = colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = match.awayTeam,
                        style = typography.titleMedium,
                        color = colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (match.isLive || match.isFinished) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = "${match.homeScore}",
                            style = typography.titleLarge,
                            color = colorScheme.secondary,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${match.awayScore}",
                            style = typography.titleLarge,
                            color = colorScheme.secondary,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Betting Market Odds Selector (1X2 / Match Winner)
            val mainMarket = match.markets.firstOrNull()
            if (mainMarket != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    mainMarket.options.forEach { option ->
                        val isSelected = selectedOptionIds.contains(option.id)
                        OddsButton(
                            option = option,
                            isSelected = isSelected,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onOptionSelected(
                                    BetSlipItem(
                                        matchId = match.id,
                                        matchTitle = "${match.homeTeam} vs ${match.awayTeam}",
                                        sport = match.sport,
                                        marketName = mainMarket.name,
                                        optionId = option.id,
                                        optionName = option.name,
                                        odds = option.odds
                                    )
                                )
                            }
                        )
                    }

                    // More Markets Pill Button (+36, +42, etc.)
                    Box(
                        modifier = Modifier
                            .height(42.dp)
                            .clip(shapes.small)
                            .background(colorScheme.surfaceVariant)
                            .border(1.dp, colorScheme.outline, shapes.small)
                            .clickable { onCardClick() }
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+${(match.markets.size * 3).coerceAtLeast(3)}",
                            style = typography.labelSmall,
                            color = colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Interactive Odds selection button with dynamic selection styling and live status lock.
 */
@Composable
fun OddsButton(
    option: BetOption,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val extendedColors = ElishaBetTheme.extendedColors
    val typography = MaterialTheme.typography
    val shapes = MaterialTheme.shapes

    val targetBgColor = when {
        option.suspended -> Color(0xFF1E232A)
        isSelected -> colorScheme.primary // Gold highlight when chosen
        else -> colorScheme.surfaceVariant
    }

    val targetTextColor = when {
        option.suspended -> colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        isSelected -> extendedColors.luxuryBlack
        else -> colorScheme.onSurface
    }

    val targetOddsColor = when {
        option.suspended -> colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        isSelected -> extendedColors.luxuryBlack
        else -> colorScheme.secondary // Emerald green odds
    }

    val bgColor by animateColorAsState(targetValue = targetBgColor, animationSpec = tween(150), label = "odds_bg")
    val textColor by animateColorAsState(targetValue = targetTextColor, animationSpec = tween(150), label = "odds_txt")
    val oddsColor by animateColorAsState(targetValue = targetOddsColor, animationSpec = tween(150), label = "odds_num")

    Surface(
        modifier = modifier
            .height(42.dp)
            .clip(shapes.small)
            .clickable(enabled = !option.suspended) { onClick() }
            .testTag("odds_button_${option.id}"),
        color = bgColor,
        shape = shapes.small,
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, colorScheme.outline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = option.name.take(9),
                style = typography.labelMedium,
                color = textColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (option.suspended) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Market Locked",
                    tint = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(12.dp)
                )
            } else {
                Text(
                    text = "%.2f".format(option.odds),
                    style = typography.labelLarge,
                    color = oddsColor,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Preview
@Composable
fun MatchCardPreview() {
    ElishaBetTheme {
        MatchCard(
            match = Match(
                id = "preview_1",
                sport = Sport.FOOTBALL,
                league = "Premier League",
                homeTeam = "Arsenal FC",
                awayTeam = "Chelsea FC",
                startTime = "LIVE",
                isLive = true,
                liveMinute = 67,
                livePeriod = "2nd Half",
                homeScore = 2,
                awayScore = 1,
                stats = MatchStats(),
                markets = listOf(
                    BetMarket(
                        id = "mkt_1",
                        name = "Full Time 1X2",
                        category = "Main",
                        options = listOf(
                            BetOption("1", "Arsenal", 1.45),
                            BetOption("X", "Draw", 3.80),
                            BetOption("2", "Chelsea", 6.50)
                        )
                    )
                )
            ),
            selectedOptionIds = setOf("1"),
            onOptionSelected = {},
            onCardClick = {}
        )
    }
}
