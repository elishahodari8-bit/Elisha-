package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Match
import com.example.model.Sport
import com.example.ui.theme.*

@Composable
fun LivePitchTracker(
    match: Match,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pitch_anim")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceVariant)
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .padding(10.dp)
            .testTag("live_pitch_tracker")
    ) {
        // Status Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(LiveRed.copy(alpha = pulseAlpha))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${match.liveMinute}' • ${match.livePeriod}",
                    color = LiveRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = when (match.stats.attackingSide) {
                    "HOME" -> "⚡ ${match.homeTeam} Dangerous Attack"
                    "AWAY" -> "⚡ ${match.awayTeam} Counter Attack"
                    else -> "Midfield Possession"
                },
                color = GoldPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Canvas Pitch
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(PitchGreen, PitchGreenLight, PitchGreen)
                    )
                )
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val lineColor = PitchLine

                // Outer boundary
                drawRect(
                    color = lineColor,
                    topLeft = Offset(8f, 8f),
                    size = Size(w - 16f, h - 16f),
                    style = Stroke(width = 2f)
                )

                // Halfway Line
                drawLine(
                    color = lineColor,
                    start = Offset(w / 2f, 8f),
                    end = Offset(w / 2f, h - 8f),
                    strokeWidth = 2f
                )

                // Center Circle
                drawCircle(
                    color = lineColor,
                    radius = h * 0.22f,
                    center = Offset(w / 2f, h / 2f),
                    style = Stroke(width = 2f)
                )

                // Left Penalty Area
                val boxWidth = w * 0.16f
                val boxHeight = h * 0.55f
                val boxTop = (h - boxHeight) / 2f
                drawRect(
                    color = lineColor,
                    topLeft = Offset(8f, boxTop),
                    size = Size(boxWidth, boxHeight),
                    style = Stroke(width = 2f)
                )

                // Right Penalty Area
                drawRect(
                    color = lineColor,
                    topLeft = Offset(w - 8f - boxWidth, boxTop),
                    size = Size(boxWidth, boxHeight),
                    style = Stroke(width = 2f)
                )

                // Goal Boxes
                val goalWidth = w * 0.06f
                val goalHeight = h * 0.28f
                val goalTop = (h - goalHeight) / 2f
                drawRect(
                    color = lineColor.copy(alpha = 0.7f),
                    topLeft = Offset(8f, goalTop),
                    size = Size(goalWidth, goalHeight),
                    style = Stroke(width = 1.5f)
                )
                drawRect(
                    color = lineColor.copy(alpha = 0.7f),
                    topLeft = Offset(w - 8f - goalWidth, goalTop),
                    size = Size(goalWidth, goalHeight),
                    style = Stroke(width = 1.5f)
                )

                // Ball position according to attackingSide
                val ballX = when (match.stats.attackingSide) {
                    "HOME" -> w * 0.78f
                    "AWAY" -> w * 0.22f
                    else -> w * 0.50f
                }
                val ballY = h / 2f

                // Ball Glow
                drawCircle(
                    color = GoldPrimary.copy(alpha = 0.35f * pulseAlpha),
                    radius = 16f,
                    center = Offset(ballX, ballY)
                )
                // Ball
                drawCircle(
                    color = PitchBall,
                    radius = 6f,
                    center = Offset(ballX, ballY)
                )
            }

            // Teams overlays on pitch
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = match.homeTeam,
                    color = TextPrimary.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = match.awayTeam,
                    color = TextPrimary.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Match Live Telemetry Stats Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = "${match.stats.possessionHome}%",
                    color = GoldPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "Possession", color = TextMuted, fontSize = 9.sp)
            }

            // Possession visual bar
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .padding(horizontal = 12.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF334155))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = (match.stats.possessionHome / 100f).coerceIn(0.1f, 0.9f))
                        .background(GoldPrimary)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${match.stats.possessionAway}%",
                    color = EmeraldGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "Possession", color = TextMuted, fontSize = 9.sp)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(label = "Shots", homeVal = match.stats.shotsHome.toString(), awayVal = match.stats.shotsAway.toString())
            StatItem(label = "Fouls", homeVal = match.stats.foulsHome.toString(), awayVal = match.stats.foulsAway.toString())
            StatItem(label = "Yellows", homeVal = match.stats.yellowCardsHome.toString(), awayVal = match.stats.yellowCardsAway.toString())
        }
    }
}

@Composable
private fun StatItem(label: String, homeVal: String, awayVal: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = homeVal, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, color = TextMuted, fontSize = 10.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = awayVal, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}
