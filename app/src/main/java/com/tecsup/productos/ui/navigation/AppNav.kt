package com.tecsup.productos.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModelProvider
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
import com.tecsup.productos.ui.detalle.DetalleScreen
import com.tecsup.productos.ui.favoritos.FavoritosScreen
import com.tecsup.productos.ui.lista.ListaScreen

object Rutas {
    const val LISTA = "lista"
    const val DETALLE = "detalle/{id}"
    const val FAVORITOS = "favoritos"
    fun detalle(id: Int) = "detalle/$id"
}

private data class Pestana(val ruta: String, val titulo: String, val icono: ImageVector)

private val pestanas = listOf(
    Pestana(Rutas.LISTA, "Catálogo", Icons.Default.Home),
    Pestana(Rutas.FAVORITOS, "Favoritos", Icons.Default.Favorite)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNav(factory: ViewModelProvider.Factory) {
    val nav = rememberNavController()
    val entrada by nav.currentBackStackEntryAsState()
    val ruta = entrada?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tituloDe(ruta)) },
                navigationIcon = {
                    if (ruta == Rutas.DETALLE) {
                        IconButton(onClick = { nav.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
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
}

private fun tituloDe(ruta: String?) = when (ruta) {
    Rutas.DETALLE -> "Detalle"
    Rutas.FAVORITOS -> "Favoritos"
    else -> "Productos"
}

/** Cambia de pestaña sin apilar pantallas y conservando el estado de cada una. */
private fun NavHostController.irAPestana(ruta: String) {
    navigate(ruta) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
