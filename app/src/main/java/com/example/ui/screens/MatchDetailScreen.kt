package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.model.BetOption
import com.example.model.BetSlipItem
import com.example.model.Match
import com.example.ui.components.LivePitchTracker
import com.example.ui.components.OddsButton
import com.example.ui.theme.*

@Composable
fun MatchDetailScreen(
    match: Match,
    selectedOptionIds: Set<String>,
    onOptionSelected: (BetSlipItem) -> Unit,
    onBack: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = remember(match) {
        listOf("All") + match.markets.map { it.category }.distinct()
    }

    val filteredMarkets = remember(match, selectedCategory) {
        if (selectedCategory == "All") {
            match.markets
        } else {
            match.markets.filter { it.category == selectedCategory }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("match_detail_screen"),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Top Navigation Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("match_detail_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = GoldPrimary
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = match.league,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${match.homeTeam} vs ${match.awayTeam}",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (match.isLive) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(LiveRed.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${match.liveMinute}' LIVE",
                            color = LiveRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // Live Pitch Visualizer (if match is Live)
        if (match.isLive) {
            item {
                Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)) {
                    LivePitchTracker(match = match)
                }
            }
        } else {
            // Pre-match scoreboard header
            item {
                Surface(
                    color = CardBackground,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = match.startTime, color = GoldPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = match.homeTeam,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                modifier = Modifier.weight(1f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Text(
                                text = "VS",
                                color = TextMuted,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                            Text(
                                text = match.awayTeam,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                modifier = Modifier.weight(1f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Market Categories Filter Tabs
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { selectedCategory = cat },
                        color = if (isSelected) GoldPrimary else DarkSurfaceVariant,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) GoldPrimary else CardBorder
                        )
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) LuxuryBlack else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Markets List
        items(filteredMarkets, key = { it.id }) { market ->
            Surface(
                color = CardBackground,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .testTag("market_section_${market.id}")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = market.name,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = market.category,
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Odds options grid/row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        market.options.forEach { option ->
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
                                            marketName = market.name,
                                            optionId = option.id,
                                            optionName = option.name,
                                            odds = option.odds
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
