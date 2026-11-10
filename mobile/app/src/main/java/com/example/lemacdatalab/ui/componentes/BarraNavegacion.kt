package com.example.lemacdatalab.ui.componentes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.lemacdatalab.navigation.Rutas

private data class Pestana(val ruta: String, val nombre: String, val icono: ImageVector)

private val PESTANAS = listOf(
    Pestana(Rutas.INICIO, "Inicio", Icons.Rounded.Home),
    Pestana(Rutas.HISTORIAL, "Historial", Icons.Rounded.History),
    Pestana(Rutas.RECURSOS, "Recursos", Icons.Rounded.Spa),
    Pestana(Rutas.CUENTA, "Perfil", Icons.Rounded.Person)
)

val RUTAS_PESTANAS: Set<String> = PESTANAS.map { it.ruta }.toSet()

@Composable
fun BarraNavegacion(
    rutaActual: String?,
    alElegir: (String) -> Unit
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
        PESTANAS.forEach { pestana ->
            NavigationBarItem(
                selected = rutaActual == pestana.ruta,
                onClick = { if (rutaActual != pestana.ruta) alElegir(pestana.ruta) },
                icon = { Icon(pestana.icono, contentDescription = null) },
                label = { Text(pestana.nombre) }
            )
        }
    }
}
