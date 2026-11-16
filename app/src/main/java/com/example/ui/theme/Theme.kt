package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HishhDarkColorScheme = darkColorScheme(
    primary = GoldBright,
    onPrimary = Color(0xFF17130C),
    primaryContainer = GoldChipBg,
    onPrimaryContainer = GoldBright,
    secondary = GoldPrimary,
    onSecondary = Color(0xFF17130C),
    secondaryContainer = ElevatedCharcoal,
    onSecondaryContainer = WarmIvory,
    tertiary = EmeraldIncome,
    onTertiary = Color(0xFF072113),
    error = CoralExpense,
    onError = Color(0xFF2B0808),
    background = ObsidianBg,
    onBackground = WarmIvory,
    surface = CharcoalPanel,
    onSurface = WarmIvory,
    surfaceVariant = ElevatedCharcoal,
    onSurfaceVariant = MutedStone,
    outline = BorderHairline,
    outlineVariant = GoldDeepBorder
)

@Composable
fun HishhMoneyTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HishhDarkColorScheme,
        typography = AppTypography,
        content = content
    )
}
