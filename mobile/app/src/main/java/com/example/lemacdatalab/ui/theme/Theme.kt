package com.example.lemacdatalab.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaClaro = lightColorScheme(
    primary = VerdeMarca,
    onPrimary = Color.White,
    primaryContainer = VerdeContenedor,
    onPrimaryContainer = VerdeSobreContenedor,
    secondary = SalviaSecundario,
    onSecondary = Color.White,
    secondaryContainer = SalviaContenedor,
    onSecondaryContainer = SalviaSobreContenedor,
    tertiary = TerracotaTerciario,
    onTertiary = Color.White,
    tertiaryContainer = TerracotaContenedor,
    onTertiaryContainer = TerracotaSobreContenedor,
    background = FondoClaro,
    onBackground = TextoClaro,
    surface = FondoClaro,
    onSurface = TextoClaro,
    surfaceVariant = VarianteClaro,
    onSurfaceVariant = TextoVarianteClaro,
    outline = BordeClaro,
    outlineVariant = BordeSuaveClaro,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFEFF5F4),
    surfaceContainer = Color(0xFFE9EFEE),
    surfaceContainerHigh = Color(0xFFE3EAE8),
    surfaceContainerHighest = Color(0xFFDDE4E3)
)

private val EsquemaOscuro = darkColorScheme(
    primary = VerdePrimarioOscuro,
    onPrimary = VerdeSobrePrimarioOscuro,
    primaryContainer = VerdeContenedorOscuro,
    onPrimaryContainer = VerdeContenedor,
    secondary = SalviaSecundarioOscuro,
    onSecondary = SalviaSobreSecundarioOscuro,
    secondaryContainer = SalviaContenedorOscuro,
    onSecondaryContainer = SalviaContenedor,
    tertiary = TerracotaTerciarioOscuro,
    onTertiary = TerracotaSobreTerciarioOscuro,
    tertiaryContainer = TerracotaContenedorOscuro,
    onTertiaryContainer = TerracotaContenedor,
    background = FondoOscuro,
    onBackground = TextoOscuro,
    surface = FondoOscuro,
    onSurface = TextoOscuro,
    surfaceVariant = TextoVarianteClaro,
    onSurfaceVariant = BordeSuaveClaro,
    outline = BordeOscuro,
    outlineVariant = TextoVarianteClaro,
    surfaceContainerLowest = Color(0xFF090F0F),
    surfaceContainerLow = Color(0xFF171D1C),
    surfaceContainer = Color(0xFF1B2120),
    surfaceContainerHigh = Color(0xFF252B2A),
    surfaceContainerHighest = Color(0xFF303635)
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