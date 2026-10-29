package com.example.lemacdatalab.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lemacdatalab.model.RegistroActividad
import com.example.lemacdatalab.ui.componentes.BotonPrincipal
import com.example.lemacdatalab.ui.componentes.EncabezadoApp

@Composable
fun PantallaHistorial(
    registros: List<RegistroActividad>,
    cargando: Boolean,
    mensajeError: String,
    alEditar: (RegistroActividad) -> Unit,
    alVolver: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        EncabezadoApp(
            titulo = "Historial",
            descripcion = "Actividades de prueba guardadas en este dispositivo."
        )

        when {
            cargando -> Text("Cargando registros...")
            mensajeError.isNotEmpty() -> Text(mensajeError)
            registros.isEmpty() -> Text("Todavía no hay registros.")
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(registros, key = { it.idRegistro }) { registro ->
                val nombreAmbito = when (registro.idAmbito) {
                    1 -> "Neurodesarrollo"
                    2 -> "DBT"
                    3 -> "Estado de ánimo"
                    4 -> "Adicciones"
                    else -> "Sin identificar"
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(registro.tipoActividad)
                    Text("Ámbito: $nombreAmbito")
                    Text("Fecha: ${registro.fechaHora}")
                    Text("Intensidad: ${registro.intensidad}")
                    Text("Contexto: ${registro.contexto}")

                    if (!registro.nota.isNullOrBlank()) {
                        Text("Nota: ${registro.nota}")
                    }

                    BotonPrincipal(
                        texto = "Editar",
                        alPresionar = { alEditar(registro) }
                    )

                    HorizontalDivider()
                }
            }
        }

        BotonPrincipal(
            texto = "Volver al inicio",
            alPresionar = alVolver
        )
    }
}