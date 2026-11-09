package com.example.lemacdatalab.ui.componentes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Mood
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.lemacdatalab.model.RegistroActividad
import com.example.lemacdatalab.model.TipoDetalle
import com.example.lemacdatalab.model.detallesDeRegistro

private fun iconoDetalle(tipo: TipoDetalle): ImageVector = when (tipo) {
    TipoDetalle.EMOCION -> Icons.Rounded.Mood
    TipoDetalle.SUENO -> Icons.Rounded.Bedtime
    TipoDetalle.RIESGO -> Icons.Rounded.WarningAmber
    TipoDetalle.HABILIDAD -> Icons.Rounded.SelfImprovement
    TipoDetalle.ESTRATEGIA -> Icons.Rounded.Lightbulb
    TipoDetalle.DESENCADENANTE -> Icons.Rounded.Bolt
    TipoDetalle.ACCION_PROTECTORA -> Icons.Rounded.Shield
    TipoDetalle.RESULTADO -> Icons.Rounded.CheckCircle
}

@Composable
fun FilasDetallesAmbito(registro: RegistroActividad) {
    detallesDeRegistro(registro).forEach { detalle ->
        FilaDato(
            icono = iconoDetalle(detalle.tipo),
            etiqueta = detalle.etiqueta,
            valor = detalle.valor
        )
    }
}
