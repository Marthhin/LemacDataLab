package com.example.lemacdatalab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lemacdatalab.datos.BaseDatosRegistros
import com.example.lemacdatalab.navigation.Rutas
import com.example.lemacdatalab.ui.pantallas.PantallaHistorial
import com.example.lemacdatalab.ui.pantallas.PantallaInicio
import com.example.lemacdatalab.ui.pantallas.PantallaRegistro
import com.example.lemacdatalab.ui.pantallas.PantallaResultado
import com.example.lemacdatalab.ui.theme.LemacDataLabTheme
import com.example.lemacdatalab.viewmodel.HistorialViewModel
import com.example.lemacdatalab.viewmodel.InicioViewModel
import com.example.lemacdatalab.viewmodel.RegistroViewModel

class MainActivity : ComponentActivity() {

    private val inicioViewModel = InicioViewModel()
    private val registroViewModel = RegistroViewModel()
    private val historialViewModel = HistorialViewModel()
    private val baseDatos by lazy { BaseDatosRegistros(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LemacDataLabTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Rutas.INICIO
                ) {
                    composable(Rutas.INICIO) {
                        PantallaInicio(
                            viewModel = inicioViewModel,
                            alRegistrar = {
                                navController.navigate(Rutas.REGISTRO)
                            },
                            alVerHistorial = {
                                historialViewModel.cargarRegistros(baseDatos)
                                navController.navigate(Rutas.HISTORIAL)
                            }
                        )
                    }

                    composable(Rutas.REGISTRO) {
                        PantallaRegistro(
                            fechaHora = registroViewModel.fechaHora,
                            tipoActividad = registroViewModel.tipoActividad,
                            contexto = registroViewModel.contexto,
                            intensidad = registroViewModel.intensidad,
                            nota = registroViewModel.nota,
                            ambito = registroViewModel.ambito,
                            errorFechaHora = registroViewModel.errorFechaHora,
                            errorTipoActividad = registroViewModel.errorTipoActividad,
                            errorContexto = registroViewModel.errorContexto,
                            errorIntensidad = registroViewModel.errorIntensidad,
                            errorAmbito = registroViewModel.errorAmbito,
                            mensajeResultado = registroViewModel.mensajeResultado,
                            alCambiarFechaHora = registroViewModel::cambiarFechaHora,
                            alCambiarTipoActividad = registroViewModel::cambiarTipoActividad,
                            alCambiarContexto = registroViewModel::cambiarContexto,
                            alCambiarIntensidad = registroViewModel::cambiarIntensidad,
                            alCambiarNota = registroViewModel::cambiarNota,
                            alCambiarAmbito = registroViewModel::cambiarAmbito,
                            alGuardar = {
                                registroViewModel.guardarRegistro(baseDatos) {
                                    navController.navigate(Rutas.RESULTADO)
                                }
                            }
                        )
                    }

                    composable(Rutas.RESULTADO) {
                        PantallaResultado(
                            registro = registroViewModel.registroActual,
                            alVolver = {
                                navController.navigate(Rutas.INICIO) {
                                    popUpTo(Rutas.INICIO) {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    composable(Rutas.HISTORIAL) {
                        PantallaHistorial(
                            registros = historialViewModel.registros,
                            cargando = historialViewModel.cargando,
                            mensajeError = historialViewModel.mensajeError,
                            alVolver = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}