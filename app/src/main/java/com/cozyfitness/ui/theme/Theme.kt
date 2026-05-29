package com.cozyfitness.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = SageGreen,
    onPrimary = PureWhite,
    primaryContainer = MintWhisper,
    onPrimaryContainer = Charcoal,
    secondary = SkyBlue,
    onSecondary = PureWhite,
    secondaryContainer = CloudBlue,
    onSecondaryContainer = Charcoal,
    tertiary = CoralPeach,
    onTertiary = PureWhite,
    background = SoftWhite,
    onBackground = Charcoal,
    surface = PureWhite,
    onSurface = Charcoal,
    surfaceVariant = MistGray,
    onSurfaceVariant = SlateGray,
    outline = Silver,
    error = SoftCoral,
    onError = PureWhite
)

@Composable
fun CozyFitnessTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}