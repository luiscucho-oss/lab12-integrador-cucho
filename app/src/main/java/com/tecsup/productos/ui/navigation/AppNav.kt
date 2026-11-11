package com.tecsup.productos.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.productos.ui.detalle.DetalleScreen
import com.tecsup.productos.ui.lista.ListaScreen

object Rutas {
    const val LISTA = "lista"
    const val DETALLE = "detalle/{id}"
    fun detalle(id: Int) = "detalle/$id"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNav(factory: ViewModelProvider.Factory) {
    val nav = rememberNavController()
    val entrada by nav.currentBackStackEntryAsState()
    val ruta = entrada?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (ruta == Rutas.DETALLE) "Detalle" else "Productos") },
                navigationIcon = {
                    if (ruta == Rutas.DETALLE) {
                        IconButton(onClick = { nav.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                }
            )
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
        }
    }
}
