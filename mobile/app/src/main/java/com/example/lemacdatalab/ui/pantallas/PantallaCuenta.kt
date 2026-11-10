package com.example.lemacdatalab.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.lemacdatalab.model.Consentimiento
import com.example.lemacdatalab.model.ContactoApoyo
import com.example.lemacdatalab.ui.componentes.CampoContrasena
import com.example.lemacdatalab.viewmodel.CuentaViewModel

@Composable
fun PantallaCuenta(
    viewModel: CuentaViewModel,
    consentimiento: Consentimiento?,
    contactoApoyo: ContactoApoyo?,
    alVerConsentimiento: () -> Unit,
    alGuardarContacto: (ContactoApoyo?) -> Unit,
    alCerrarSesion: () -> Unit,
    alCuentaEliminada: () -> Unit,
    modoOscuro: Boolean,
    alCambiarTema: (Boolean) -> Unit,
    alVerRecordatorios: () -> Unit
) {
    var confirmarSalida by rememberSaveable { mutableStateOf(false) }
    var confirmarEliminar by rememberSaveable { mutableStateOf(false) }
    var editarContacto by rememberSaveable { mutableStateOf(false) }

    if (editarContacto) {
        DialogoContacto(
            actual = contactoApoyo,
            alGuardar = { contacto ->
                alGuardarContacto(contacto)
                editarContacto = false
            },
            alCancelar = { editarContacto = false }
        )
    }

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
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Perfil",
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

            TarjetaCuenta(titulo = "PREFERENCIAS") {
                FilaAjuste(
                    icono = Icons.Rounded.NotificationsActive,
                    titulo = "Recordatorios",
                    descripcion = "Programa un aviso diario",
                    alPresionar = alVerRecordatorios
                ) {
                    Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null)
                }
                FilaAjuste(
                    icono = Icons.Rounded.DarkMode,
                    titulo = "Modo oscuro",
                    descripcion = if (modoOscuro) "Activado" else "Desactivado",
                    alPresionar = { alCambiarTema(!modoOscuro) }
                ) {
                    Switch(checked = modoOscuro, onCheckedChange = alCambiarTema)
                }
            }

            TarjetaCuenta(titulo = "MIS DATOS") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (consentimiento != null) Icons.Rounded.VerifiedUser else Icons.Rounded.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = consentimiento?.let {
                            "Consentimiento versión ${it.version}, aceptado el ${formatearFechaHora(it.fechaAceptacion)}"
                        } ?: "Aún no aceptas el consentimiento informado. Se te pedirá en tu primer registro.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
                OutlinedButton(
                    onClick = alVerConsentimiento,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                ) {
                    Text(if (consentimiento != null) "Ver consentimiento" else "Leer y aceptar")
                }
            }

            TarjetaCuenta(titulo = "CONTACTO DE APOYO") {
                Text(
                    text = contactoApoyo?.let { "${it.nombre} · ${it.telefono}" }
                        ?: "Una persona de confianza a quien llamar si un registro muestra un momento difícil.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (contactoApoyo != null) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                OutlinedButton(
                    onClick = { editarContacto = true },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                ) {
                    Icon(Icons.Rounded.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (contactoApoyo != null) "Cambiar contacto" else "Agregar contacto")
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
private fun FilaAjuste(
    icono: ImageVector,
    titulo: String,
    descripcion: String,
    alPresionar: () -> Unit,
    accion: @Composable () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = alPresionar)
            .padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp)
        ) {
            Text(titulo, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = descripcion,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        accion()
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

@Composable
private fun DialogoContacto(
    actual: ContactoApoyo?,
    alGuardar: (ContactoApoyo?) -> Unit,
    alCancelar: () -> Unit
) {
    var nombre by rememberSaveable { mutableStateOf(actual?.nombre.orEmpty()) }
    var telefono by rememberSaveable { mutableStateOf(actual?.telefono.orEmpty()) }
    val telefonoValido = telefono.count { it.isDigit() } >= 8
    val puedeGuardar = nombre.isNotBlank() && telefonoValido

    AlertDialog(
        onDismissRequest = alCancelar,
        icon = { Icon(Icons.Rounded.Person, contentDescription = null) },
        title = { Text("Contacto de apoyo") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it.take(40) },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { valor -> telefono = valor.filter { it.isDigit() || it == '+' || it == ' ' }.take(16) },
                    label = { Text("Teléfono") },
                    placeholder = { Text("+56 9 1234 5678") },
                    isError = telefono.isNotEmpty() && !telefonoValido,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { alGuardar(ContactoApoyo(nombre.trim(), telefono.trim())) },
                enabled = puedeGuardar
            ) { Text("Guardar") }
        },
        dismissButton = {
            Row {
                if (actual != null) {
                    TextButton(onClick = { alGuardar(null) }) {
                        Text("Quitar", color = MaterialTheme.colorScheme.error)
                    }
                }
                TextButton(onClick = alCancelar) { Text("Cancelar") }
            }
        }
    )
}
