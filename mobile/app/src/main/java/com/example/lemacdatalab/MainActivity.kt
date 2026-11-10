package com.example.lemacdatalab

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lemacdatalab.datos.BaseDatosRoom
import com.example.lemacdatalab.datos.PreferenciasApp
import com.example.lemacdatalab.datos.RepositorioContenidos
import com.example.lemacdatalab.datos.RepositorioRegistros
import com.example.lemacdatalab.datos.RepositorioUsuarios
import com.example.lemacdatalab.datos.SesionUsuario
import com.example.lemacdatalab.navigation.Rutas
import com.example.lemacdatalab.notificaciones.ProgramadorRecordatorios
import com.example.lemacdatalab.ui.pantallas.PantallaBienvenida
import com.example.lemacdatalab.ui.pantallas.PantallaConsentimiento
import com.example.lemacdatalab.ui.pantallas.PantallaCuenta
import com.example.lemacdatalab.ui.pantallas.PantallaDetalle
import com.example.lemacdatalab.ui.pantallas.PantallaHistorial
import com.example.lemacdatalab.ui.pantallas.PantallaInicio
import com.example.lemacdatalab.ui.pantallas.PantallaLogin
import com.example.lemacdatalab.ui.pantallas.PantallaRecordatorios
import com.example.lemacdatalab.ui.pantallas.PantallaRecursos
import com.example.lemacdatalab.ui.pantallas.PantallaRegistro
import com.example.lemacdatalab.ui.pantallas.PantallaResultado
import com.example.lemacdatalab.ui.theme.LemacDataLabTheme
import com.example.lemacdatalab.viewmodel.CuentaViewModel
import com.example.lemacdatalab.viewmodel.DetalleViewModel
import com.example.lemacdatalab.viewmodel.HistorialViewModel
import com.example.lemacdatalab.viewmodel.InicioViewModel
import com.example.lemacdatalab.viewmodel.LoginViewModel
import com.example.lemacdatalab.viewmodel.RecordatoriosViewModel
import com.example.lemacdatalab.viewmodel.RecursosViewModel
import com.example.lemacdatalab.viewmodel.RegistroViewModel

private const val DURACION_TRANSICION = 300

private typealias Transiciones = AnimatedContentTransitionScope<NavBackStackEntry>

private fun Transiciones.entrarAvanzando(): EnterTransition =
    slideIntoContainer(SlideDirection.Start, tween(DURACION_TRANSICION)) +
            fadeIn(tween(DURACION_TRANSICION))

private fun Transiciones.salirAvanzando(): ExitTransition =
    slideOutOfContainer(
        SlideDirection.Start,
        tween(DURACION_TRANSICION),
        targetOffset = { it / 4 }
    ) + fadeOut(tween(DURACION_TRANSICION))

private fun Transiciones.entrarVolviendo(): EnterTransition =
    slideIntoContainer(
        SlideDirection.End,
        tween(DURACION_TRANSICION),
        initialOffset = { it / 4 }
    ) + fadeIn(tween(DURACION_TRANSICION))

private fun Transiciones.salirVolviendo(): ExitTransition =
    slideOutOfContainer(SlideDirection.End, tween(DURACION_TRANSICION)) +
            fadeOut(tween(DURACION_TRANSICION))

private fun Transiciones.subirFormulario(): EnterTransition =
    slideIntoContainer(SlideDirection.Up, tween(DURACION_TRANSICION)) +
            fadeIn(tween(DURACION_TRANSICION))

private fun Transiciones.bajarFormulario(): ExitTransition =
    slideOutOfContainer(SlideDirection.Down, tween(DURACION_TRANSICION)) +
            fadeOut(tween(DURACION_TRANSICION))

private fun fundidoEntrada(): EnterTransition = fadeIn(tween(DURACION_TRANSICION))

private fun fundidoSalida(): ExitTransition = fadeOut(tween(DURACION_TRANSICION))

class MainActivity : ComponentActivity() {

    private val baseDatos by lazy {
        BaseDatosRoom.obtenerInstancia(applicationContext)
    }

    private val repositorio by lazy {
        RepositorioRegistros(baseDatos.registroDao())
    }

    private val repositorioUsuarios by lazy {
        RepositorioUsuarios(baseDatos)
    }

    private val repositorioContenidos by lazy {
        RepositorioContenidos()
    }

    private val sesion by lazy {
        SesionUsuario(applicationContext)
    }

    private val preferencias by lazy {
        PreferenciasApp(applicationContext)
    }

    private val programador by lazy {
        ProgramadorRecordatorios(applicationContext)
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
            initializer {
                RecursosViewModel(repositorioContenidos)
            }
            initializer {
                RecordatoriosViewModel(preferencias, programador)
            }
            initializer {
                CuentaViewModel(repositorioUsuarios, repositorio, sesion)
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

    private val recursosViewModel by lazy {
        ViewModelProvider(this, fabricaViewModels)[RecursosViewModel::class.java]
    }

    private val recordatoriosViewModel by lazy {
        ViewModelProvider(this, fabricaViewModels)[RecordatoriosViewModel::class.java]
    }

    private val cuentaViewModel by lazy {
        ViewModelProvider(this, fabricaViewModels)[CuentaViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        programador.programar(preferencias.recordatorio.value)

        setContent {
            val modoOscuroGuardado by preferencias.modoOscuro.collectAsState()
            val usarOscuro = modoOscuroGuardado ?: isSystemInDarkTheme()

            DisposableEffect(usarOscuro) {
                val estilo = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { usarOscuro }
                enableEdgeToEdge(statusBarStyle = estilo, navigationBarStyle = estilo)
                onDispose { }
            }

            LemacDataLabTheme(darkTheme = usarOscuro) {
                val navController = rememberNavController()
                val destinoInicial = remember {
                    when {
                        sesion.haySesion -> Rutas.INICIO
                        !preferencias.bienvenidaVista.value -> Rutas.BIENVENIDA
                        else -> Rutas.LOGIN
                    }
                }

                val datosSesion by sesion.actual.collectAsState()
                val revisionUsuario by preferencias.revisionUsuario.collectAsState()
                val idUsuario = datosSesion?.idUsuario
                val consentimiento = remember(idUsuario, revisionUsuario) {
                    idUsuario?.let(preferencias::consentimientoDe)
                }
                val contactoApoyo = remember(idUsuario, revisionUsuario) {
                    idUsuario?.let(preferencias::contactoDe)
                }

                val irARegistrar: () -> Unit = {
                    if (consentimiento == null) {
                        navController.navigate(Rutas.CONSENTIMIENTO)
                    } else {
                        registroViewModel.comenzarNuevoRegistro()
                        navController.navigate(Rutas.REGISTRO)
                    }
                }

                val irAlLogin: () -> Unit = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = destinoInicial,
                        enterTransition = { entrarAvanzando() },
                        exitTransition = { salirAvanzando() },
                        popEnterTransition = { entrarVolviendo() },
                        popExitTransition = { salirVolviendo() }
                    ) {
                        composable(
                            route = Rutas.BIENVENIDA,
                            exitTransition = { fundidoSalida() }
                        ) {
                            PantallaBienvenida(
                                alTerminar = {
                                    preferencias.marcarBienvenidaVista()
                                    irAlLogin()
                                }
                            )
                        }

                        composable(
                            route = Rutas.LOGIN,
                            enterTransition = { fundidoEntrada() },
                            exitTransition = { fundidoSalida() }
                        ) {
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

                        composable(
                            route = Rutas.INICIO,
                            enterTransition = {
                                if (initialState.destination.route == Rutas.LOGIN) {
                                    fundidoEntrada()
                                } else {
                                    entrarAvanzando()
                                }
                            },
                            exitTransition = {
                                if (targetState.destination.route == Rutas.LOGIN) {
                                    fundidoSalida()
                                } else {
                                    salirAvanzando()
                                }
                            }
                        ) {
                            val permisoPreguntado by preferencias.permisoPreguntado.collectAsState()

                            PantallaInicio(
                                viewModel = inicioViewModel,
                                alias = inicioViewModel.alias,
                                modoOscuro = usarOscuro,
                                mostrarAvisoPermiso = !permisoPreguntado,
                                alMarcarPermisoPreguntado = preferencias::marcarPermisoPreguntado,
                                alCambiarTema = {
                                    preferencias.cambiarModoOscuro(!usarOscuro)
                                },
                                alRegistrar = irARegistrar,
                                alVerHistorial = {
                                    navController.navigate(Rutas.HISTORIAL)
                                },
                                alVerRegistro = { id ->
                                    navController.navigate(Rutas.detalle(id))
                                },
                                alVerRecursos = {
                                    navController.navigate(Rutas.RECURSOS)
                                },
                                alVerRecordatorios = {
                                    navController.navigate(Rutas.RECORDATORIOS)
                                },
                                alVerCuenta = {
                                    navController.navigate(Rutas.CUENTA)
                                }
                            )
                        }

                        composable(
                            route = Rutas.REGISTRO,
                            enterTransition = { subirFormulario() },
                            popExitTransition = { bajarFormulario() },
                            exitTransition = { fundidoSalida() }
                        ) {
                            PantallaRegistro(
                                viewModel = registroViewModel,
                                alGuardar = {
                                    registroViewModel.guardarRegistro {
                                        navController.navigate(Rutas.RESULTADO) {
                                            popUpTo(Rutas.REGISTRO) {
                                                inclusive = true
                                            }
                                        }
                                    }
                                },
                                alCerrar = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable(
                            route = Rutas.RESULTADO,
                            enterTransition = { fundidoEntrada() }
                        ) {
                            PantallaResultado(
                                registro = registroViewModel.registroActual,
                                editando = registroViewModel.editando,
                                contactoApoyo = contactoApoyo,
                                alVolver = {
                                    navController.navigate(Rutas.INICIO) {
                                        popUpTo(Rutas.INICIO) {
                                            inclusive = false
                                        }
                                        launchSingleTop = true
                                    }
                                },
                                alRegistrarOtra = {
                                    registroViewModel.comenzarNuevoRegistro()
                                    navController.navigate(Rutas.REGISTRO) {
                                        popUpTo(Rutas.RESULTADO) {
                                            inclusive = true
                                        }
                                    }
                                }
                            )
                        }

                        composable(Rutas.HISTORIAL) {
                            PantallaHistorial(
                                viewModel = historialViewModel,
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
                                versiones = detalleViewModel.versiones,
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

                        composable(Rutas.RECURSOS) {
                            PantallaRecursos(
                                contenidos = recursosViewModel.contenidosVisibles,
                                filtro = recursosViewModel.filtro,
                                idExpandido = recursosViewModel.idExpandido,
                                alCambiarFiltro = recursosViewModel::cambiarFiltro,
                                alAlternar = recursosViewModel::alternar,
                                alVolver = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable(Rutas.RECORDATORIOS) {
                            val config by recordatoriosViewModel.config.collectAsState()

                            PantallaRecordatorios(
                                config = config,
                                alActivar = recordatoriosViewModel::activar,
                                alDesactivar = recordatoriosViewModel::desactivar,
                                alCambiarHora = recordatoriosViewModel::cambiarHora,
                                alAlternarDia = recordatoriosViewModel::alternarDia,
                                alCambiarOmitir = recordatoriosViewModel::cambiarOmitir,
                                alVolver = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable(Rutas.CONSENTIMIENTO) {
                            PantallaConsentimiento(
                                aceptado = consentimiento,
                                alAceptar = {
                                    idUsuario?.let(preferencias::aceptarConsentimiento)
                                    registroViewModel.comenzarNuevoRegistro()
                                    navController.navigate(Rutas.REGISTRO) {
                                        popUpTo(Rutas.CONSENTIMIENTO) {
                                            inclusive = true
                                        }
                                    }
                                },
                                alVolver = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable(Rutas.CUENTA) {
                            LaunchedEffect(Unit) {
                                cuentaViewModel.reiniciarFormulario()
                            }

                            PantallaCuenta(
                                viewModel = cuentaViewModel,
                                consentimiento = consentimiento,
                                contactoApoyo = contactoApoyo,
                                alVerConsentimiento = {
                                    navController.navigate(Rutas.CONSENTIMIENTO)
                                },
                                alGuardarContacto = { contacto ->
                                    idUsuario?.let { preferencias.guardarContacto(it, contacto) }
                                },
                                alCerrarSesion = {
                                    sesion.cerrar()
                                    irAlLogin()
                                },
                                alCuentaEliminada = {
                                    idUsuario?.let(preferencias::olvidarUsuario)
                                    irAlLogin()
                                },
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
}