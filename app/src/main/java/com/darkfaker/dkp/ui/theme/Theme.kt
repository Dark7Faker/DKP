package com.darkfaker.dkp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ArcadeColorScheme = darkColorScheme(
    primary = ArcadeColors.CyanPrimary,
    background = ArcadeColors.Background,
    surface = ArcadeColors.CardBackground,
    onBackground = ArcadeColors.TextWhite,
    onSurface = ArcadeColors.TextWhite,
)

@Composable
fun DKPaceTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ArcadeColorScheme,
        typography = Typography,
        content = content
    )
}
