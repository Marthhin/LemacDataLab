package com.example.lemacdatalab.ui.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Mood
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.lemacdatalab.model.Ambito
import com.example.lemacdatalab.ui.theme.AmbitoAdiccionesClaro
import com.example.lemacdatalab.ui.theme.AmbitoAdiccionesOscuro
import com.example.lemacdatalab.ui.theme.AmbitoAnimoClaro
import com.example.lemacdatalab.ui.theme.AmbitoAnimoOscuro
import com.example.lemacdatalab.ui.theme.AmbitoDbtClaro
import com.example.lemacdatalab.ui.theme.AmbitoDbtOscuro
import com.example.lemacdatalab.ui.theme.AmbitoNeuroClaro
import com.example.lemacdatalab.ui.theme.AmbitoNeuroOscuro

val Ambito.icono: ImageVector
    get() = when (this) {
        Ambito.NEURODESARROLLO -> Icons.Rounded.Psychology
        Ambito.DBT -> Icons.Rounded.SelfImprovement
        Ambito.ANIMO -> Icons.Rounded.Mood
        Ambito.ADICCIONES -> Icons.Rounded.Favorite
    }

@Composable
fun colorAmbito(ambito: Ambito): Color {
    val oscuro = MaterialTheme.colorScheme.background.luminance() < 0.5f
    return when (ambito) {
        Ambito.NEURODESARROLLO -> if (oscuro) AmbitoNeuroOscuro else AmbitoNeuroClaro
        Ambito.DBT -> if (oscuro) AmbitoDbtOscuro else AmbitoDbtClaro
        Ambito.ANIMO -> if (oscuro) AmbitoAnimoOscuro else AmbitoAnimoClaro
        Ambito.ADICCIONES -> if (oscuro) AmbitoAdiccionesOscuro else AmbitoAdiccionesClaro
    }
}

@Composable
fun IconoAmbito(
    ambito: Ambito?,
    modifier: Modifier = Modifier,
    tamano: Dp = 44.dp
) {
    Box(
        modifier = modifier
            .size(tamano)
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = ambito?.icono ?: Icons.Rounded.Info,
            contentDescription = ambito?.nombre,
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}