package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import com.example.model.BetSlipItem
import com.example.model.Match
import com.example.model.Sport
import com.example.ui.components.LivePitchTracker
import com.example.ui.components.MatchCard
import com.example.ui.theme.*

@Composable
fun SportsDashboardScreen(
    matches: List<Match>,
    selectedSport: Sport?,
    selectedOptionIds: Set<String>,
    searchQuery: String,
    filterLiveOnly: Boolean,
    onSportSelect: (Sport?) -> Unit,
    onSearchChange: (String) -> Unit,
    onToggleLiveOnly: () -> Unit,
    onOptionSelected: (BetSlipItem) -> Unit,
    onMatchClick: (String) -> Unit,
    onNavigatePromotions: () -> Unit
) {
    val filteredMatches = remember(matches, selectedSport, searchQuery, filterLiveOnly) {
        matches.filter { match ->
            val matchesSport = selectedSport == null || match.sport == selectedSport
            val matchesSearch = searchQuery.isBlank() ||
                    match.homeTeam.contains(searchQuery, ignoreCase = true) ||
                    match.awayTeam.contains(searchQuery, ignoreCase = true) ||
                    match.league.contains(searchQuery, ignoreCase = true)
            val matchesLive = !filterLiveOnly || match.isLive
            matchesSport && matchesSearch && matchesLive
        }
    }

    val liveMatches = remember(matches) { matches.filter { it.isLive && !it.isFinished } }
    val featuredLiveMatch = liveMatches.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("sports_dashboard_screen"),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Promotional Hero Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF1E281F),
                                Color(0xFF161F28),
                                Color(0xFF281E10)
                            )
                        )
                    )
                    .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .clickable { onNavigatePromotions() }
                    .padding(14.dp)
                    .testTag("hero_promo_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🏆 WELCOME OFFER",
                                color = GoldPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "100% FIRST DEPOSIT BONUS",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Up to TSh 500,000 + 500% Acca Boost on Multi-bets!",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Button(
                        onClick = onNavigatePromotions,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = LuxuryBlack
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("CLAIM", fontWeight = FontWeight.Black, fontSize = 11.sp)
                    }
                }
            }
        }

        // Search & Filters Row
        item {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("sports_search_bar"),
                    placeholder = { Text("Search teams, leagues, tournaments...", fontSize = 12.sp, color = TextMuted) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = GoldPrimary, modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = CardBorder,
                        focusedContainerColor = CardBackground,
                        unfocusedContainerColor = CardBackground,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Live Filter Ticker & Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = filterLiveOnly,
                        onClick = onToggleLiveOnly,
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(LiveRed)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("LIVE (${liveMatches.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LiveRed.copy(alpha = 0.2f),
                            selectedLabelColor = LiveRed,
                            containerColor = DarkSurfaceVariant,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = filterLiveOnly,
                            borderColor = if (filterLiveOnly) LiveRed else CardBorder
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Text(
                        text = "${filteredMatches.size} Matches available",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // 10 Sports Category Horizontal Slider
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    SportCategoryChip(
                        name = "All Sports",
                        icon = "🌐",
                        isSelected = selectedSport == null,
                        onClick = { onSportSelect(null) }
                    )
                }

                items(Sport.values()) { sport ->
                    SportCategoryChip(
                        name = sport.displayName,
                        icon = sport.iconSymbol,
                        isSelected = selectedSport == sport,
                        onClick = { onSportSelect(sport) }
                    )
                }
            }
        }

        // Live Pitch Visualizer for Top Live Match (if any)
        if (featuredLiveMatch != null && (selectedSport == null || selectedSport == featuredLiveMatch.sport)) {
            item {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                    Text(
                        text = "LIVE MATCH TRACKER",
                        color = GoldPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LivePitchTracker(match = featuredLiveMatch)
                }
            }
        }

        // Section Title: Available Events
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedSport != null) "${selectedSport.displayName.uppercase()} FIXTURES" else "TOP SPORTS EVENTS",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Text(
                    text = "1X2 Markets",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        // Matches List
        if (filteredMatches.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🏟️", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No matches found", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Try selecting another sport or clearing your search.", color = TextMuted, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(filteredMatches, key = { it.id }) { match ->
                Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)) {
                    MatchCard(
                        match = match,
                        selectedOptionIds = selectedOptionIds,
                        onOptionSelected = onOptionSelected,
                        onCardClick = { onMatchClick(match.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun SportCategoryChip(
    name: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .testTag("sport_tab_$name"),
        color = if (isSelected) GoldPrimary else DarkSurfaceVariant,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) GoldPrimary else CardBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 13.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = name,
                color = if (isSelected) LuxuryBlack else TextPrimary,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
            )
        }
    }
}
