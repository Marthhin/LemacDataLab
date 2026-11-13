package com.example.lemacdatalab

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lemacdatalab.datos.BaseDatosRoom
import com.example.lemacdatalab.datos.PreferenciasApp
import com.example.lemacdatalab.datos.RepositorioContenidos
import com.example.lemacdatalab.datos.RepositorioRegistros
import com.example.lemacdatalab.datos.RepositorioUsuarios
import com.example.lemacdatalab.datos.SesionUsuario
import com.example.lemacdatalab.datos.api.ClienteApi
import com.example.lemacdatalab.datos.api.ServidorSimulado
import com.example.lemacdatalab.datos.api.Sincronizador
import com.example.lemacdatalab.datos.remoto.ClienteRetrofit
import com.example.lemacdatalab.model.Ambito
import com.example.lemacdatalab.model.TextoConsentimiento
import com.example.lemacdatalab.navigation.Rutas
import com.example.lemacdatalab.notificaciones.ProgramadorRecordatorios
import com.example.lemacdatalab.ui.componentes.BarraNavegacion
import com.example.lemacdatalab.ui.componentes.RUTAS_PESTANAS
import com.example.lemacdatalab.ui.pantallas.PantallaBienvenida
import com.example.lemacdatalab.ui.pantallas.PantallaConsentimiento
import com.example.lemacdatalab.ui.pantallas.PantallaCuenta
import com.example.lemacdatalab.ui.pantallas.PantallaDetalle
import com.example.lemacdatalab.ui.pantallas.PantallaEvolucionAmbito
import com.example.lemacdatalab.ui.pantallas.PantallaHistorial
import com.example.lemacdatalab.ui.pantallas.PantallaInicio
import com.example.lemacdatalab.ui.pantallas.PantallaLogin
import com.example.lemacdatalab.ui.pantallas.PantallaRecordatorios
import com.example.lemacdatalab.ui.pantallas.PantallaRecursos
import com.example.lemacdatalab.ui.pantallas.PantallaRegistro
import com.example.lemacdatalab.ui.pantallas.PantallaRegistroHoy
import com.example.lemacdatalab.ui.pantallas.PantallaResultado
import com.example.lemacdatalab.ui.theme.LemacDataLabTheme
import com.example.lemacdatalab.viewmodel.CuentaViewModel
import com.example.lemacdatalab.viewmodel.DetalleViewModel
import com.example.lemacdatalab.viewmodel.HistorialViewModel
import com.example.lemacdatalab.viewmodel.InicioViewModel
import com.example.lemacdatalab.viewmodel.LoginViewModel
import com.example.lemacdatalab.viewmodel.RecordatoriosViewModel
import com.example.lemacdatalab.viewmodel.RecursosViewModel
import com.example.lemacdatalab.viewmodel.RegistroHoyViewModel
import com.example.lemacdatalab.viewmodel.RegistroViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

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

private const val REVISION_INACTIVIDAD_MS = 15_000L

private fun AnimatedContentTransitionScope<NavBackStackEntry>.entrePestanas(): Boolean =
    initialState.destination.route in RUTAS_PESTANAS && targetState.destination.route in RUTAS_PESTANAS

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
        RepositorioContenidos(ClienteRetrofit.recursosApi)
    }

    private val sesion by lazy {
        SesionUsuario(applicationContext)
    }

    private val preferencias by lazy {
        PreferenciasApp(applicationContext)
    }

    private val servidorSimulado by lazy {
        ServidorSimulado(applicationContext, repositorioUsuarios)
    }

    private val clienteApi by lazy {
        ClienteApi(servidorSimulado)
    }

    private val sincronizador by lazy {
        Sincronizador(applicationContext, baseDatos.registroDao(), clienteApi, sesion)
    }

    private val programador by lazy {
        ProgramadorRecordatorios(applicationContext)
    }

    private val fabricaViewModels by lazy {
        viewModelFactory {
            initializer {
                LoginViewModel(repositorioUsuarios, sesion, clienteApi)
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
                RegistroHoyViewModel(repositorio, sesion)
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

    private val registroHoyViewModel by lazy {
        ViewModelProvider(this, fabricaViewModels)[RegistroHoyViewModel::class.java]
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

    override fun onUserInteraction() {
        super.onUserInteraction()
        sesion.registrarActividad()
    }

    override fun onStop() {
        sesion.guardarActividad()
        super.onStop()
    }

    private fun ocultarEnRecientes() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            setRecentsScreenshotEnabled(false)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        programador.programar(preferencias.recordatorio.value)
        ocultarEnRecientes()

        lifecycleScope.launch(Dispatchers.Default) {
            try {
                repositorioUsuarios.sembrarDataset()
            } catch (error: Exception) {
                Log.e("MainActivity", "No se pudo cargar el dataset sintético", error)
            }
        }

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

                var destinoTrasConsentimiento by rememberSaveable { mutableStateOf(Rutas.REGISTRO) }

                val abrirFormulario: (String) -> Unit = { destino ->
                    if (destino == Rutas.REGISTRO_HOY) {
                        registroHoyViewModel.comenzar()
                    } else {
                        registroViewModel.comenzarNuevoRegistro()
                    }
                }

                val irAFormulario: (String) -> Unit = { destino ->
                    if (consentimiento == null) {
                        destinoTrasConsentimiento = destino
                        navController.navigate(Rutas.CONSENTIMIENTO)
                    } else {
                        abrirFormulario(destino)
                        navController.navigate(destino)
                    }
                }

                val irAlLogin: () -> Unit = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }

                val alcance = rememberCoroutineScope()
                val estadoSincronizacion by sincronizador.estado.collectAsState()
                val usuarioActual by remember(idUsuario) {
                    idUsuario?.let(repositorioUsuarios::observar) ?: flowOf(null)
                }.collectAsState(initial = null)

                val cerrarPorInactividad: () -> Unit = {
                    if (sesion.haySesion) {
                        sesion.cerrar()
                        Toast.makeText(
                            this@MainActivity,
                            "Cerramos tu sesión por inactividad",
                            Toast.LENGTH_LONG
                        ).show()
                        irAlLogin()
                    }
                }

                LifecycleEventEffect(Lifecycle.Event.ON_START) {
                    if (sesion.expirada()) cerrarPorInactividad()
                }

                LaunchedEffect(idUsuario) {
                    if (idUsuario == null) return@LaunchedEffect
                    sincronizador.cargarEstado(idUsuario)
                    sincronizador.sincronizar()
                    while (true) {
                        delay(REVISION_INACTIVIDAD_MS)
                        if (sesion.expirada()) {
                            cerrarPorInactividad()
                            break
                        }
                    }
                }

                val entradaActual by navController.currentBackStackEntryAsState()
                val rutaActual = entradaActual?.destination?.route

                val irAPestana: (String) -> Unit = { ruta ->
                    navController.navigate(ruta) {
                        popUpTo(Rutas.INICIO) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }

                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentWindowInsets = WindowInsets(0),
                    bottomBar = {
                        AnimatedVisibility(
                            visible = rutaActual in RUTAS_PESTANAS,
                            enter = slideInVertically { it },
                            exit = slideOutVertically { it }
                        ) {
                            BarraNavegacion(
                                rutaActual = rutaActual,
                                alElegir = { ruta ->
                                    if (ruta == Rutas.HISTORIAL) historialViewModel.cambiarFiltro(null)
                                    irAPestana(ruta)
                                }
                            )
                        }
                    }
                ) { relleno ->
                    NavHost(
                        navController = navController,
                        startDestination = destinoInicial,
                        modifier = Modifier
                            .padding(relleno)
                            .consumeWindowInsets(relleno),
                        enterTransition = { if (entrePestanas()) fundidoEntrada() else entrarAvanzando() },
                        exitTransition = { if (entrePestanas()) fundidoSalida() else salirAvanzando() },
                        popEnterTransition = { if (entrePestanas()) fundidoEntrada() else entrarVolviendo() },
                        popExitTransition = { if (entrePestanas()) fundidoSalida() else salirVolviendo() }
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
                                if (initialState.destination.route == Rutas.LOGIN || entrePestanas()) {
                                    fundidoEntrada()
                                } else {
                                    entrarAvanzando()
                                }
                            },
                            exitTransition = {
                                if (targetState.destination.route == Rutas.LOGIN || entrePestanas()) {
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
                                mostrarAvisoPermiso = !permisoPreguntado,
                                alMarcarPermisoPreguntado = preferencias::marcarPermisoPreguntado,
                                alRegistroHoy = { irAFormulario(Rutas.REGISTRO_HOY) },
                                alVerAmbito = { ambito ->
                                    navController.navigate(Rutas.evolucion(ambito.id))
                                },
                                alVerPerfil = { irAPestana(Rutas.CUENTA) }
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
                                objetivo = usuarioActual?.objetivo,
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
                                alRegistrar = { irAFormulario(Rutas.REGISTRO) }
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
                            val estadoRecursos by recursosViewModel.estado.collectAsState()

                            PantallaRecursos(
                                estado = estadoRecursos,
                                alCambiarFiltro = recursosViewModel::cambiarFiltro,
                                alAlternar = recursosViewModel::alternar,
                                alReintentar = recursosViewModel::cargar
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

                        composable(
                            route = Rutas.REGISTRO_HOY,
                            enterTransition = { subirFormulario() },
                            popExitTransition = { bajarFormulario() }
                        ) {
                            PantallaRegistroHoy(
                                viewModel = registroHoyViewModel,
                                contactoApoyo = contactoApoyo,
                                objetivo = usuarioActual?.objetivo,
                                alTerminar = {
                                    navController.popBackStack(Rutas.INICIO, inclusive = false)
                                }
                            )
                        }

                        composable(Rutas.CONSENTIMIENTO) {
                            PantallaConsentimiento(
                                aceptado = consentimiento,
                                alAceptar = {
                                    idUsuario?.let(preferencias::aceptarConsentimiento)
                                    val token = sesion.token
                                    if (token != null) {
                                        alcance.launch {
                                            clienteApi.aceptarConsentimiento(
                                                token,
                                                TextoConsentimiento.VERSION,
                                                System.currentTimeMillis()
                                            )
                                        }
                                    }
                                    abrirFormulario(destinoTrasConsentimiento)
                                    navController.navigate(destinoTrasConsentimiento) {
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
                                modoOscuro = usarOscuro,
                                alCambiarTema = preferencias::cambiarModoOscuro,
                                alVerRecordatorios = {
                                    navController.navigate(Rutas.RECORDATORIOS)
                                },
                                objetivo = usuarioActual?.objetivo,
                                estadoSincronizacion = estadoSincronizacion,
                                alSincronizar = {
                                    alcance.launch { sincronizador.sincronizar() }
                                }
                            )
                        }

                        composable(
                            route = Rutas.EVOLUCION,
                            arguments = listOf(
                                navArgument("idAmbito") { type = NavType.IntType }
                            )
                        ) { entrada ->
                            val ambito = Ambito.desdeId(entrada.arguments?.getInt("idAmbito") ?: -1)
                                ?: Ambito.NEURODESARROLLO

                            PantallaEvolucionAmbito(
                                ambito = ambito,
                                evolucion = inicioViewModel.evolucionDe(ambito),
                                registros = inicioViewModel.registrosDe(ambito),
                                periodo = inicioViewModel.periodo,
                                alCambiarPeriodo = inicioViewModel::cambiarPeriodo,
                                alVerRegistro = { id ->
                                    navController.navigate(Rutas.detalle(id))
                                },
                                alVerHistorial = {
                                    historialViewModel.cambiarFiltro(ambito)
                                    irAPestana(Rutas.HISTORIAL)
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