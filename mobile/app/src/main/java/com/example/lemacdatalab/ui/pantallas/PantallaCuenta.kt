package com.example.lemacdatalab.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lemacdatalab.ui.componentes.CampoContrasena
import com.example.lemacdatalab.viewmodel.CuentaViewModel

@Composable
fun PantallaCuenta(
    viewModel: CuentaViewModel,
    alCerrarSesion: () -> Unit,
    alCuentaEliminada: () -> Unit,
    alVolver: () -> Unit
) {
    var confirmarSalida by rememberSaveable { mutableStateOf(false) }
    var confirmarEliminar by rememberSaveable { mutableStateOf(false) }

    if (confirmarSalida) {
        AlertDialog(
            onDismissRequest = { confirmarSalida = false },
            icon = { Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null) },
            title = { Text("¿Cerrar sesión?") },
            text = { Text("Tus registros quedan guardados en este dispositivo.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmarSalida = false
                        alCerrarSesion()
                    }
                ) { Text("Cerrar sesión") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarSalida = false }) { Text("Cancelar") }
            }
        )
    }

    if (confirmarEliminar) {
        DialogoEliminarCuenta(
            totalRegistros = viewModel.totalRegistros,
            eliminando = viewModel.eliminando,
            error = viewModel.errorEliminar,
            alConfirmar = { contrasena ->
                viewModel.eliminarCuenta(contrasena) {
                    confirmarEliminar = false
                    alCuentaEliminada()
                }
            },
            alCambiarContrasena = { viewModel.limpiarErrorEliminar() },
            alCancelar = {
                confirmarEliminar = false
                viewModel.limpiarErrorEliminar()
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp)
        ) {
            IconButton(onClick = alVolver) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver al inicio")
            }
            Text(
                text = "Mi cuenta",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = viewModel.alias.take(1).uppercase().ifEmpty { "?" },
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(
                        text = viewModel.alias,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.PhoneAndroid,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Cuenta local · ${viewModel.totalRegistros} " +
                                    if (viewModel.totalRegistros == 1) "registro" else "registros",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }
                }
            }

            TarjetaCuenta(titulo = "CAMBIAR CONTRASEÑA") {
                CampoContrasena(
                    valor = viewModel.contrasenaActual,
                    alCambiar = viewModel::cambiarActual,
                    etiqueta = "Contraseña actual",
                    error = viewModel.errorActual,
                    esUltimo = false,
                    alConfirmar = {}
                )
                CampoContrasena(
                    valor = viewModel.contrasenaNueva,
                    alCambiar = viewModel::cambiarNueva,
                    etiqueta = "Nueva contraseña",
                    error = viewModel.errorNueva,
                    esUltimo = false,
                    alConfirmar = {}
                )
                CampoContrasena(
                    valor = viewModel.confirmacion,
                    alCambiar = viewModel::cambiarConfirmacion,
                    etiqueta = "Confirmación",
                    error = viewModel.errorConfirmacion,
                    esUltimo = true,
                    alConfirmar = viewModel::cambiarContrasena
                )

                if (viewModel.mensaje.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = viewModel.mensaje,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                Button(
                    onClick = viewModel::cambiarContrasena,
                    enabled = !viewModel.cambiando,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp)
                ) {
                    if (viewModel.cambiando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Guardar contraseña")
                    }
                }
            }

            OutlinedButton(
                onClick = { confirmarSalida = true },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
            ) {
                Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar sesión")
            }

            TarjetaCuenta(titulo = "ELIMINAR CUENTA") {
                Text(
                    text = "Se borrarán tu cuenta y todos tus registros de este dispositivo. Esta acción no se puede deshacer.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedButton(
                    onClick = { confirmarEliminar = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp)
                ) {
                    Icon(Icons.Rounded.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Eliminar mi cuenta y mis datos")
                }
            }
        }
    }
}

@Composable
private fun TarjetaCuenta(
    titulo: String,
    contenido: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            contenido()
        }
    }
}

@Composable
private fun DialogoEliminarCuenta(
    totalRegistros: Int,
    eliminando: Boolean,
    error: String?,
    alConfirmar: (String) -> Unit,
    alCambiarContrasena: () -> Unit,
    alCancelar: () -> Unit
) {
    var contrasena by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { if (!eliminando) alCancelar() },
        icon = {
            Icon(
                imageVector = Icons.Rounded.DeleteForever,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = { Text("¿Eliminar tu cuenta?") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Se borrarán tu cuenta y tus $totalRegistros " +
                            (if (totalRegistros == 1) "registro" else "registros") +
                            ". Escribe tu contraseña para confirmar."
                )
                CampoContrasena(
                    valor = contrasena,
                    alCambiar = {
                        contrasena = it
                        alCambiarContrasena()
                    },
                    etiqueta = "Contraseña",
                    error = error,
                    esUltimo = true,
                    alConfirmar = { alConfirmar(contrasena) }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { alConfirmar(contrasena) },
                enabled = !eliminando
            ) {
                Text("Eliminar", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = alCancelar, enabled = !eliminando) {
                Text("Cancelar")
            }
        }
    )
}
