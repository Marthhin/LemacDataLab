package com.example.lemacdatalab.ui.pantallas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.lemacdatalab.ui.componentes.CampoContrasena
import com.example.lemacdatalab.viewmodel.LoginViewModel

@Composable
fun PantallaLogin(
    viewModel: LoginViewModel,
    alEntrar: () -> Unit
) {
    val enviar = { viewModel.enviar(alEntrar) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Spa,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "UN ESPACIO PARA SEGUIR TU ÁNIMO",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = buildAnnotatedString {
                append("El ánimo cambia.\n")
                withStyle(
                    SpanStyle(
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.primary
                    )
                ) {
                    append("Tu historia importa.")
                }
            },
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = if (viewModel.creandoCuenta) {
                "Crea una cuenta de prueba para guardar tus registros."
            } else {
                "Ingresa para continuar con tus registros."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = viewModel.alias,
                    onValueChange = viewModel::cambiarAlias,
                    label = { Text("Alias") },
                    leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = null) },
                    isError = viewModel.errorAlias != null,
                    supportingText = viewModel.errorAlias?.let { { Text(it) } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth()
                )

                CampoContrasena(
                    valor = viewModel.contrasena,
                    alCambiar = viewModel::cambiarContrasena,
                    etiqueta = "Contraseña",
                    error = viewModel.errorContrasena,
                    esUltimo = !viewModel.creandoCuenta,
                    alConfirmar = enviar
                )

                AnimatedVisibility(visible = viewModel.creandoCuenta) {
                    CampoContrasena(
                        valor = viewModel.confirmacion,
                        alCambiar = viewModel::cambiarConfirmacion,
                        etiqueta = "Confirmación",
                        error = viewModel.errorConfirmacion,
                        esUltimo = true,
                        alConfirmar = enviar
                    )
                }

                if (viewModel.mensajeError.isNotEmpty()) {
                    Text(
                        text = viewModel.mensajeError,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Button(
                    onClick = enviar,
                    enabled = !viewModel.cargando,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    if (viewModel.cargando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = if (viewModel.creandoCuenta) "Crear cuenta" else "Ingresar",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }

        TextButton(
            onClick = viewModel::cambiarModo,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                if (viewModel.creandoCuenta) {
                    "¿Ya tienes cuenta? Ingresar"
                } else {
                    "¿No tienes cuenta? Crear una"
                }
            )
        }

        Row(verticalAlignment = Alignment.Top) {
            Icon(
                imageVector = Icons.Rounded.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "Proyecto académico con datos ficticios. El registro no reemplaza la atención profesional.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
