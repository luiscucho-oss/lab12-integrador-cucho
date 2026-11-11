package com.tecsup.productos.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.productos.domain.model.Rol
import com.tecsup.productos.domain.model.Sesion
import com.tecsup.productos.ui.UiState
import com.tecsup.productos.ui.components.Cargando
import com.tecsup.productos.ui.detalle.DetalleScreen
import com.tecsup.productos.ui.favoritos.FavoritosScreen
import com.tecsup.productos.ui.lista.ListaScreen
import com.tecsup.productos.ui.login.LoginScreen
import com.tecsup.productos.ui.login.LoginViewModel

object Rutas {
    const val LISTA = "lista"
    const val DETALLE = "detalle/{id}"
    const val FAVORITOS = "favoritos"
    fun detalle(id: Int) = "detalle/$id"
}

private data class Pestana(val ruta: String, val titulo: String, val icono: ImageVector)

/**
 * Punto de entrada de la UI: sin sesión muestra el login;
 * con sesión muestra la zona principal.
 */
@Composable
fun AppNav(factory: ViewModelProvider.Factory) {
    val loginViewModel: LoginViewModel = viewModel(factory = factory)
    val verificando by loginViewModel.verificando.collectAsStateWithLifecycle()
    val estado by loginViewModel.estado.collectAsStateWithLifecycle()
    val sesion = (estado as? UiState.Exito)?.datos

    Surface(modifier = Modifier.fillMaxSize()) {
        when {
            verificando -> Cargando(texto = "Verificando sesión...")
            sesion == null -> LoginScreen(viewModel = loginViewModel)
            else -> ZonaPrincipal(
                sesion = sesion,
                factory = factory,
                onCerrarSesion = loginViewModel::cerrarSesion
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ZonaPrincipal(
    sesion: Sesion,
    factory: ViewModelProvider.Factory,
    onCerrarSesion: () -> Unit
) {
    val nav = rememberNavController()
    val entrada by nav.currentBackStackEntryAsState()
    val ruta = entrada?.destination?.route
    var confirmarSalida by rememberSaveable { mutableStateOf(false) }

    val pestanas = listOf(
        Pestana(Rutas.LISTA, "Catálogo", Icons.Default.Home),
        Pestana(Rutas.FAVORITOS, "Favoritos", Icons.Default.Favorite)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(tituloDe(ruta))
                        Text(
                            text = "${sesion.correo} · ${nombreDe(sesion.rol)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    if (ruta == Rutas.DETALLE) {
                        IconButton(onClick = { nav.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { confirmarSalida = true }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar sesión")
                    }
                }
            )
        },
        bottomBar = {
            if (ruta != Rutas.DETALLE) {
                NavigationBar {
                    pestanas.forEach { pestana ->
                        NavigationBarItem(
                            selected = entrada?.destination?.hierarchy?.any { it.route == pestana.ruta } == true,
                            onClick = { nav.irAPestana(pestana.ruta) },
                            icon = { Icon(pestana.icono, contentDescription = null) },
                            label = { Text(pestana.titulo) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Rutas.LISTA,
            modifier = Modifier.padding(padding)
        ) {
            composable(Rutas.LISTA) {
                ListaScreen(
                    viewModel = viewModel(factory = factory),
                    onAbrirDetalle = { id -> nav.navigate(Rutas.detalle(id)) }
                )
            }
            composable(
                route = Rutas.DETALLE,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) {
                DetalleScreen(viewModel = viewModel(factory = factory))
            }
            composable(Rutas.FAVORITOS) {
                FavoritosScreen(
                    viewModel = viewModel(factory = factory),
                    onAbrirDetalle = { id -> nav.navigate(Rutas.detalle(id)) }
                )
            }
        }
    }

    if (confirmarSalida) {
        AlertDialog(
            onDismissRequest = { confirmarSalida = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Quieres salir de tu cuenta?") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarSalida = false
                    onCerrarSesion()
                }) { Text("Salir") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarSalida = false }) { Text("Cancelar") }
            }
        )
    }
}

private fun tituloDe(ruta: String?) = when (ruta) {
    Rutas.DETALLE -> "Detalle"
    Rutas.FAVORITOS -> "Favoritos"
    else -> "Productos"
}

private fun nombreDe(rol: Rol) = when (rol) {
    Rol.ADMIN -> "Administrador"
    Rol.USUARIO -> "Usuario"
}

/** Cambia de pestaña sin apilar pantallas y conservando el estado de cada una. */
private fun NavHostController.irAPestana(ruta: String) {
    navigate(ruta) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
