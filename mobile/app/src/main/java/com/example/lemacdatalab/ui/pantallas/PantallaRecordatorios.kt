package com.example.lemacdatalab.ui.pantallas

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.lemacdatalab.notificaciones.NotificacionesRecordatorio
import com.example.lemacdatalab.ui.componentes.BotonPrincipal
import com.example.lemacdatalab.ui.componentes.EncabezadoApp

@Composable

fun PantallaRecordatorios(
    alVolver: () -> Unit
){
    val contexto = LocalContext.current
    var mensaje by remember {mutableStateOf("")}

    val mostrarNotificacion: () -> Unit = {
        val enviada = NotificacionesRecordatorio.mostrar(contexto)

        mensaje = if (enviada){
            "Aviso enviado. Revise la barra de notificaciones."
        } else {
            "Las notificaiones estan desactivadas. Puede habilitarlas en los ajustes de la app"
        }
    }

    val solicitarPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { permitido ->
        if (permitido) {
            mostrarNotificacion()
        } else {
            mensaje = "Permiso no concedido. Puedes seguir usando la app"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ){
        EncabezadoApp(
            titulo = "Recordatorios",
            descripcion = "Prueba un aviso discreto"
        )

        Text(
            "La notificacion mostrará un mensaje general, "+
            "sin incluir informacion de sus registros."
        )

        Text(
            "Esta prueba envia el aviso al pulsar el boton. " +
            "Todavia no programa recordatorios por horario"
        )

        BotonPrincipal(
            texto = "Probar notificacion",
            alPresionar = {
                if (
                    Build.VERSION.SDK_INT >= 33 &&
                    ContextCompat.checkSelfPermission(
                        contexto,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_DENIED
                ){
                    solicitarPermiso.launch(
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                } else {
                    mostrarNotificacion()
                }
            }
        )

        if (mensaje.isNotEmpty()) {
            Text(mensaje)
        }

        BotonPrincipal(
            texto = "Volver al inicio",
            alPresionar = alVolver
        )
    }
}