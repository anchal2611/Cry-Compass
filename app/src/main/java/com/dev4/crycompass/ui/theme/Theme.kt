package com.dev4.crycompass.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorPalette = lightColorScheme(
    primary = BabyBlue,
    secondary = SoftPink,
    background = CreamWhite,
    surface = WarmLavender,
    onPrimary = CharcoalGray,
    onSecondary = CharcoalGray,
    onBackground = CharcoalGray,
    onSurface = CharcoalGray
)

private val DarkColorPalette = darkColorScheme(
    primary = SoftBlue,
    secondary = DustyPink,
    background = DeepSlateBlue,
    surface = MutedLavender,
    onPrimary = LightCream,
    onSecondary = LightCream,
    onBackground = LightCream,
    onSurface = LightCream
)

@Composable
fun CryCompassTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}