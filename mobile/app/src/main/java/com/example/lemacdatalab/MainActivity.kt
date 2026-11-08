package com.example.lemacdatalab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lemacdatalab.datos.BaseDatosRoom
import com.example.lemacdatalab.datos.RepositorioRegistros
import com.example.lemacdatalab.datos.RepositorioUsuarios
import com.example.lemacdatalab.datos.SesionUsuario
import com.example.lemacdatalab.navigation.Rutas
import com.example.lemacdatalab.ui.pantallas.PantallaDetalle
import com.example.lemacdatalab.ui.pantallas.PantallaHistorial
import com.example.lemacdatalab.ui.pantallas.PantallaInicio
import com.example.lemacdatalab.ui.pantallas.PantallaLogin
import com.example.lemacdatalab.ui.pantallas.PantallaRecordatorios
import com.example.lemacdatalab.ui.pantallas.PantallaRegistro
import com.example.lemacdatalab.ui.pantallas.PantallaResultado
import com.example.lemacdatalab.ui.theme.LemacDataLabTheme
import com.example.lemacdatalab.viewmodel.DetalleViewModel
import com.example.lemacdatalab.viewmodel.HistorialViewModel
import com.example.lemacdatalab.viewmodel.InicioViewModel
import com.example.lemacdatalab.viewmodel.LoginViewModel
import com.example.lemacdatalab.viewmodel.RegistroViewModel

class MainActivity : ComponentActivity() {

    private val baseDatos by lazy {
        BaseDatosRoom.obtenerInstancia(applicationContext)
    }

    private val repositorio by lazy {
        RepositorioRegistros(baseDatos.registroDao())
    }

    private val repositorioUsuarios by lazy {
        RepositorioUsuarios(baseDatos.usuarioDao())
    }

    private val sesion by lazy {
        SesionUsuario(applicationContext)
    }

    private val fabricaViewModels by lazy {
        viewModelFactory {
            initializer {
                LoginViewModel(repositorioUsuarios, sesion)
            }
            initializer {
                InicioViewModel(repositorio, sesion)
            }
            initializer {
                RegistroViewModel(repositorio, sesion)
            }
            initializer {
                HistorialViewModel(repositorio, sesion)
            }
            initializer {
                DetalleViewModel(repositorio, sesion)
            }
        }
    }

    private val loginViewModel by lazy {
        ViewModelProvider(this, fabricaViewModels)[LoginViewModel::class.java]
    }

    private val inicioViewModel by lazy {
        ViewModelProvider(this, fabricaViewModels)[InicioViewModel::class.java]
    }

    private val registroViewModel by lazy {
        ViewModelProvider(this, fabricaViewModels)[RegistroViewModel::class.java]
    }

    private val historialViewModel by lazy {
        ViewModelProvider(this, fabricaViewModels)[HistorialViewModel::class.java]
    }

    private val detalleViewModel by lazy {
        ViewModelProvider(this, fabricaViewModels)[DetalleViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LemacDataLabTheme {
                val navController = rememberNavController()
                val destinoInicial = remember {
                    if (sesion.haySesion) Rutas.INICIO else Rutas.LOGIN
                }

                NavHost(
                    navController = navController,
                    startDestination = destinoInicial
                ) {
                    composable(Rutas.LOGIN) {
                        PantallaLogin(
                            viewModel = loginViewModel,
                            alEntrar = {
                                navController.navigate(Rutas.INICIO) {
                                    popUpTo(Rutas.LOGIN) {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }

                    composable(Rutas.INICIO) {
                        PantallaInicio(
                            viewModel = inicioViewModel,
                            alias = inicioViewModel.alias,
                            alRegistrar = {
                                registroViewModel.comenzarNuevoRegistro()
                                navController.navigate(Rutas.REGISTRO)
                            },
                            alVerHistorial = {
                                navController.navigate(Rutas.HISTORIAL)
                            },
                            alVerRegistro = { id ->
                                navController.navigate(Rutas.detalle(id))
                            },
                            alVerRecordatorios = {
                                navController.navigate(Rutas.RECORDATORIOS)
                            },
                            alCerrarSesion = {
                                sesion.cerrar()
                                navController.navigate(Rutas.LOGIN) {
                                    popUpTo(navController.graph.id) {
                                        inclusive = true
                                    }
                                }
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
                            alVerDetalle = { id ->
                                navController.navigate(Rutas.detalle(id))
                            },
                            alVolver = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(
                        route = Rutas.DETALLE,
                        arguments = listOf(
                            navArgument("idRegistro") { type = NavType.IntType }
                        )
                    ) { entrada ->
                        val idRegistro = entrada.arguments?.getInt("idRegistro") ?: -1

                        LaunchedEffect(idRegistro) {
                            detalleViewModel.mostrar(idRegistro)
                        }

                        PantallaDetalle(
                            registro = detalleViewModel.registro,
                            cargando = detalleViewModel.cargando,
                            eliminando = detalleViewModel.eliminando,
                            mensajeError = detalleViewModel.mensajeError,
                            alEditar = { registro ->
                                registroViewModel.comenzarEditarRegistro(registro)
                                navController.navigate(Rutas.REGISTRO)
                            },
                            alEliminar = {
                                detalleViewModel.eliminar {
                                    navController.popBackStack()
                                }
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