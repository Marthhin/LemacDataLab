package com.example.lemacdatalab.ui.componentes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lemacdatalab.model.Escala
import com.example.lemacdatalab.model.OPCION_OTRO

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorEscala(
    valor: Int,
    alCambiar: (Int) -> Unit
) {
    Row(verticalAlignment = Alignment.Bottom) {
        Text(
            text = valor.toString(),
            style = MaterialTheme.typography.displaySmall,
            color = if (valor >= Escala.CRITICO) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.primary
            }
        )
        Text(
            text = " / ${Escala.MAXIMO}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Text(
            text = Escala.etiqueta(valor).uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 10.dp)
        )
    }

    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        (Escala.MINIMO..Escala.MAXIMO).forEach { opcion ->
            SegmentedButton(
                selected = opcion == valor,
                onClick = { alCambiar(opcion) },
                shape = SegmentedButtonDefaults.itemShape(
                    index = opcion - Escala.MINIMO,
                    count = Escala.MAXIMO - Escala.MINIMO + 1
                ),
                icon = {},
                label = { Text(opcion.toString()) },
                modifier = Modifier.semantics {
                    contentDescription = Escala.describir(opcion)
                }
            )
        }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "0 · ${Escala.etiqueta(Escala.MINIMO)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "${Escala.etiqueta(Escala.MAXIMO)} · ${Escala.MAXIMO}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectorOpciones(
    opciones: List<String>,
    seleccionada: String,
    alElegir: (String) -> Unit,
    permitirQuitar: Boolean
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        opciones.forEach { opcion ->
            val elegida = opcion == seleccionada
            FilterChip(
                selected = elegida,
                onClick = {
                    when {
                        !elegida -> alElegir(opcion)
                        permitirQuitar -> alElegir("")
                    }
                },
                label = { Text(opcion) },
                leadingIcon = if (elegida) {
                    { Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                } else {
                    null
                }
            )
        }
    }
}

@Composable
fun SelectorCatalogo(
    opciones: List<String>,
    seleccionada: String,
    otro: String,
    alElegir: (String) -> Unit,
    alCambiarOtro: (String) -> Unit,
    permitirQuitar: Boolean
) {
    SelectorOpciones(
        opciones = opciones,
        seleccionada = seleccionada,
        alElegir = alElegir,
        permitirQuitar = permitirQuitar
    )

    AnimatedVisibility(visible = seleccionada == OPCION_OTRO) {
        OutlinedTextField(
            value = otro,
            onValueChange = alCambiarOtro,
            label = { Text("Especifica") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
