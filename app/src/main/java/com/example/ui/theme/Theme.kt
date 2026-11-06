package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GrokpotColorScheme = darkColorScheme(
    primary = BioEmerald,
    onPrimary = Color(0xFF002A19),
    primaryContainer = Color(0xFF05482D),
    onPrimaryContainer = BioEmeraldGlow,

    secondary = BioCyan,
    onSecondary = Color(0xFF00363D),
    secondaryContainer = Color(0xFF074853),
    onSecondaryContainer = Color(0xFFA5F3FC),

    tertiary = BioViolet,
    onTertiary = Color(0xFF2E1065),
    tertiaryContainer = Color(0xFF4C1D95),
    onTertiaryContainer = BioVioletGlow,

    background = BioBackground,
    onBackground = BioTextPrimary,

    surface = BioSurface,
    onSurface = BioTextPrimary,
    surfaceVariant = BioSurfaceVariant,
    onSurfaceVariant = BioTextSecondary,

    outline = BioBorder,
    outlineVariant = BioSurfaceHigh,

    error = BioRose,
    onError = Color.White
)

@Composable
fun GrokpotTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GrokpotColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    GrokpotTheme(content = content)
}
