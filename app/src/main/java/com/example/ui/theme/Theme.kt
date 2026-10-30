package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

// --- Material 3 Luxury Sportsbook Color Scheme ---
private val ElishaBetDarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = LuxuryBlack,
    primaryContainer = Color(0xFF382A05),
    onPrimaryContainer = GoldLight,
    inversePrimary = GoldDark,

    secondary = EmeraldGreen,
    onSecondary = LuxuryBlack,
    secondaryContainer = EmeraldSurface,
    onSecondaryContainer = EmeraldLight,

    tertiary = OddsBlue,
    onTertiary = LuxuryBlack,
    tertiaryContainer = Color(0xFF0C2738),
    onTertiaryContainer = Color(0xFFBAE6FD),

    background = LuxuryBlack,
    onBackground = TextPrimary,

    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    surfaceTint = GoldPrimary,

    inverseSurface = Color(0xFFE2E8F0),
    inverseOnSurface = LuxuryBlack,

    outline = CardBorder,
    outlineVariant = Color(0xFF1E2833),

    error = LiveRed,
    onError = Color.White,
    errorContainer = Color(0xFF450A0A),
    onErrorContainer = Color(0xFFFCA5A5),

    scrim = Color.Black
)

// --- Extended Brand Palette for ELISHA BET Components ---
@Immutable
data class ElishaBetExtendedColors(
    val luxuryBlack: Color = LuxuryBlack,
    val darkSurface: Color = DarkSurface,
    val cardBackground: Color = CardBackground,
    val cardBorder: Color = CardBorder,
    val goldPrimary: Color = GoldPrimary,
    val goldLight: Color = GoldLight,
    val goldDark: Color = GoldDark,
    val goldMuted: Color = GoldMuted,
    val emeraldGreen: Color = EmeraldGreen,
    val emeraldLight: Color = EmeraldLight,
    val emeraldDark: Color = EmeraldDark,
    val emeraldSurface: Color = EmeraldSurface,
    val liveRed: Color = LiveRed,
    val oddsBlue: Color = OddsBlue,
    val accentOrange: Color = AccentOrange,
    val textPrimary: Color = TextPrimary,
    val textSecondary: Color = TextSecondary,
    val textMuted: Color = TextMuted,
    val pitchGreen: Color = PitchGreen,
    val pitchGreenLight: Color = PitchGreenLight,
    val pitchLine: Color = PitchLine,
    val pitchBall: Color = PitchBall
)

val LocalElishaBetColors = staticCompositionLocalOf { ElishaBetExtendedColors() }

// --- Material 3 Shapes ---
val ElishaBetShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

/**
 * Primary theme for ELISHA BET Sportsbook.
 * Implements luxury black background, emerald green, and gold branding.
 */
@Composable
fun ElishaBetTheme(
    extendedColors: ElishaBetExtendedColors = ElishaBetExtendedColors(),
    content: @Composable () -> Unit
) {
    val colorScheme = ElishaBetDarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                // Keep status bar and navigation bar dark with light icons
                window.statusBarColor = LuxuryBlack.hashCode()
                window.navigationBarColor = DarkSurface.hashCode()
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = false
                    isAppearanceLightNavigationBars = false
                }
            }
        }
    }

    CompositionLocalProvider(
        LocalElishaBetColors provides extendedColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = ElishaBetShapes,
            content = content
        )
    }
}

/**
 * Compatibility alias for templates or previews referencing MyApplicationTheme.
 */
@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    ElishaBetTheme(content = content)
}

/**
 * Direct accessor object for custom extended brand tokens.
 */
object ElishaBetTheme {
    val extendedColors: ElishaBetExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalElishaBetColors.current
}
