package com.example.lemacdatalab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lemacdatalab.datos.BaseDatosRoom
import com.example.lemacdatalab.datos.RepositorioRegistros
import com.example.lemacdatalab.navigation.Rutas
import com.example.lemacdatalab.ui.pantallas.PantallaHistorial
import com.example.lemacdatalab.ui.pantallas.PantallaInicio
import com.example.lemacdatalab.ui.pantallas.PantallaRecordatorios
import com.example.lemacdatalab.ui.pantallas.PantallaRegistro
import com.example.lemacdatalab.ui.pantallas.PantallaResultado
import com.example.lemacdatalab.ui.theme.LemacDataLabTheme
import com.example.lemacdatalab.viewmodel.HistorialViewModel
import com.example.lemacdatalab.viewmodel.InicioViewModel
import com.example.lemacdatalab.viewmodel.RegistroViewModel

class MainActivity : ComponentActivity() {

    private val repositorio by lazy {
        RepositorioRegistros(
            BaseDatosRoom.obtenerInstancia(applicationContext).registroDao()
        )
    }

    private val fabricaViewModels by lazy {
        viewModelFactory {
            initializer {
                RegistroViewModel(repositorio)
            }
            initializer {
                HistorialViewModel(repositorio)
            }
        }
    }

    private val inicioViewModel by lazy {
        ViewModelProvider(this)[InicioViewModel::class.java]
    }

    private val registroViewModel by lazy {
        ViewModelProvider(
            this,
            fabricaViewModels
        )[RegistroViewModel::class.java]
    }

    private val historialViewModel by lazy {
        ViewModelProvider(
            this,
            fabricaViewModels
        )[HistorialViewModel::class.java]
    }

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
                                registroViewModel.comenzarNuevoRegistro()
                                navController.navigate(Rutas.REGISTRO)
                            },
                            alVerHistorial = {
                                navController.navigate(Rutas.HISTORIAL)
                            },
                            alVerRecordatorios = {
                                navController.navigate(Rutas.RECORDATORIOS)
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
                            editando = registroViewModel.editando,
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
                                registroViewModel.guardarRegistro {
                                    navController.navigate(Rutas.RESULTADO) {
                                        popUpTo(Rutas.REGISTRO) {
                                            inclusive = true
                                        }
                                    }
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
                            eliminando = historialViewModel.eliminando,
                            mensajeOperacion = historialViewModel.mensajeOperacion,
                            alEditar = { registro ->
                                registroViewModel.comenzarEditarRegistro(registro)
                                navController.navigate(Rutas.REGISTRO)
                            },
                            alEliminar = { registro ->
                                historialViewModel.eliminarRegistro(registro)
                            },
                            alVolver = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(Rutas.RECORDATORIOS) {
                        PantallaRecordatorios(
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