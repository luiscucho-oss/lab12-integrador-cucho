package com.tecsup.productos.ui.lista

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.productos.ui.UiState
import com.tecsup.productos.ui.components.Cargando
import com.tecsup.productos.ui.components.ErrorView
import com.tecsup.productos.ui.components.ListaItems
import com.tecsup.productos.ui.components.Vacio

@Composable
fun ListaScreen(
    viewModel: ListaViewModel,
    onAbrirDetalle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    when (val e = estado) {
        UiState.Cargando -> Cargando(modifier, texto = "Descargando productos...")
        is UiState.Error -> ErrorView(
            mensaje = e.mensaje,
            onReintentar = viewModel::refrescar,
            modifier = modifier
        )
        is UiState.Exito -> {
            if (e.datos.isEmpty()) {
                Vacio(
                    mensaje = "No hay productos para mostrar",
                    modifier = modifier,
                    textoAccion = "Actualizar",
                    onAccion = viewModel::refrescar
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
