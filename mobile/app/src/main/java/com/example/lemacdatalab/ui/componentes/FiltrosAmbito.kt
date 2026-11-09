package com.example.lemacdatalab.ui.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lemacdatalab.model.Ambito

@Composable
fun FiltrosAmbito(
    filtro: Ambito?,
    alCambiarFiltro: (Ambito?) -> Unit
) {
    val opciones = listOf<Ambito?>(null) + Ambito.entries

    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(opciones) { opcion ->
            val seleccionado = filtro == opcion
            FilterChip(
                selected = seleccionado,
                onClick = { alCambiarFiltro(opcion) },
                label = { Text(opcion?.nombre ?: "Todos") },
                leadingIcon = if (seleccionado) {
                    { Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                } else {
                    null
                }
            )
        }
    }
}