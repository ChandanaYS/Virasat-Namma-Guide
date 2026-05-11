package com.virasat.nammaguide.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = HeritageSaffron,
    onPrimary = Sandstone,
    secondary = TempleGold,
    onSecondary = DeepStone,
    tertiary = LeafGreen,
    background = Sandstone,
    onBackground = DeepStone,
    surface = Sandstone,
    onSurface = DeepStone,
    error = LotusRed
)

@Composable
fun VirasatNammaGuideTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
