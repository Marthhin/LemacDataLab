package com.example.lemacdatalab.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lemacdatalab.model.RegistroActividad
import com.example.lemacdatalab.ui.componentes.BotonPrincipal
import com.example.lemacdatalab.ui.componentes.EncabezadoApp

@Composable
fun PantallaResultado(
    registro: RegistroActividad?,
    alVolver: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        EncabezadoApp(
            titulo = "Resultado",
            descripcion = "Registro de prueba creado en memoria."
        )

        if (registro == null) {
            Text("Todavía no hay un registro para mostrar.")
        } else {
            val nombreAmbito = when (registro.idAmbito) {
                1 -> "Neurodesarrollo"
                2 -> "DBT"
                3 -> "Estado de ánimo"
                4 -> "Adicciones"
                else -> "Sin identificar"
            }

            Text("Ámbito: $nombreAmbito")
            Text("Fecha: ${registro.fechaHora}")
            Text("Actividad: ${registro.tipoActividad}")
            Text("Intensidad: ${registro.intensidad}")
            Text("Contexto: ${registro.contexto}")

            if (!registro.nota.isNullOrBlank()) {
                Text("Nota: ${registro.nota}")
            }
        }

        BotonPrincipal(
            texto = "Volver al inicio",
            alPresionar = alVolver
        )
    }
}