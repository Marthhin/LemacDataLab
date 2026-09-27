package com.example.lemacdatalab.ui.componentes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BotonPrincipal(
    texto: String,
    alPresionar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = alPresionar,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(text = texto)
    }
}