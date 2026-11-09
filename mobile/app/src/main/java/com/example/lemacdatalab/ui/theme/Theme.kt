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
    surfaceContainerLow = Color.White,
    surfaceContainer = Color(0xFFF1EEE8),
    surfaceContainerHigh = Color(0xFFECE8E1),
    surfaceContainerHighest = Color(0xFFE6E2DA)
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
    surfaceVariant = BordeSuaveOscuro,
    onSurfaceVariant = TextoVarianteOscuro,
    outline = BordeOscuro,
    outlineVariant = BordeSuaveOscuro,
    surfaceContainerLowest = Color(0xFF081110),
    surfaceContainerLow = Color(0xFF13201F),
    surfaceContainer = Color(0xFF172625),
    surfaceContainerHigh = Color(0xFF1D2D2B),
    surfaceContainerHighest = Color(0xFF243533)
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
