package com.tecsup.productos.ui.favoritos

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.productos.ui.UiState
import com.tecsup.productos.ui.components.Cargando
import com.tecsup.productos.ui.components.ListaItems
import com.tecsup.productos.ui.components.Vacio

@Composable
fun FavoritosScreen(
    viewModel: FavoritosViewModel,
    onAbrirDetalle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    when (val e = estado) {
        UiState.Cargando -> Cargando(modifier)
        is UiState.Error -> Vacio(mensaje = e.mensaje, modifier = modifier)
        is UiState.Exito -> {
            if (e.datos.isEmpty()) {
                Vacio(
                    mensaje = "Aún no tienes favoritos.\nToca el corazón de un producto para guardarlo aquí.",
                    modifier = modifier
                )
            } else {
                ListaItems(
                    items = e.datos,
                    onAbrirDetalle = onAbrirDetalle,
                    onFavorito = viewModel::alternarFavorito,
                    modifier = modifier
                )
            }
        }
    }
}
