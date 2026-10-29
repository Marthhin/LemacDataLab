package com.example.lemacdatalab.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaClaro = lightColorScheme(
    primary = AzulPrimario,
    onPrimary = Color.White,
    primaryContainer = AzulContenedor,
    onPrimaryContainer = AzulSobreContenedor,
    secondary = GrisAzulSecundario,
    onSecondary = Color.White,
    secondaryContainer = GrisAzulContenedor,
    onSecondaryContainer = GrisAzulSobreContenedor,
    tertiary = DuraznoTerciario,
    onTertiary = Color.White,
    tertiaryContainer = DuraznoContenedor,
    onTertiaryContainer = DuraznoSobreContenedor,
    background = FondoClaro,
    onBackground = TextoClaro,
    surface = FondoClaro,
    onSurface = TextoClaro,
    surfaceVariant = VarianteClaro,
    onSurfaceVariant = TextoVarianteClaro,
    outline = BordeClaro,
    outlineVariant = BordeSuaveClaro,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF1F3F8),
    surfaceContainer = Color(0xFFEBEEF3),
    surfaceContainerHigh = Color(0xFFE6E8ED),
    surfaceContainerHighest = Color(0xFFE0E2E8)
)

private val EsquemaOscuro = darkColorScheme(
    primary = AzulPrimarioOscuro,
    onPrimary = AzulSobrePrimarioOscuro,
    primaryContainer = AzulContenedorOscuro,
    onPrimaryContainer = AzulContenedor,
    secondary = GrisAzulSecundarioOscuro,
    onSecondary = GrisAzulSobreSecundarioOscuro,
    secondaryContainer = GrisAzulContenedorOscuro,
    onSecondaryContainer = GrisAzulContenedor,
    tertiary = DuraznoTerciarioOscuro,
    onTertiary = DuraznoSobreTerciarioOscuro,
    tertiaryContainer = DuraznoContenedorOscuro,
    onTertiaryContainer = DuraznoContenedor,
    background = FondoOscuro,
    onBackground = TextoOscuro,
    surface = FondoOscuro,
    onSurface = TextoOscuro,
    surfaceVariant = TextoVarianteClaro,
    onSurfaceVariant = BordeSuaveClaro,
    outline = BordeOscuro,
    outlineVariant = TextoVarianteClaro,
    surfaceContainerLowest = Color(0xFF0C0E12),
    surfaceContainerLow = Color(0xFF191C20),
    surfaceContainer = Color(0xFF1D2024),
    surfaceContainerHigh = Color(0xFF282A2F),
    surfaceContainerHighest = Color(0xFF33353A)
)

@Composable
fun LemacDataLabTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro,
        typography = Typography,
        content = content
    )
}