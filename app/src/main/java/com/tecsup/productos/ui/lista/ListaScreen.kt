package com.tecsup.productos.ui.lista

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.productos.domain.model.Item
import com.tecsup.productos.ui.UiState
import com.tecsup.productos.ui.components.Cargando
import com.tecsup.productos.ui.components.ErrorView
import com.tecsup.productos.ui.components.ItemFila
import com.tecsup.productos.ui.components.Vacio
import com.tecsup.productos.ui.theme.ProductosTheme

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
                ListaContenido(
                    items = e.datos,
                    onAbrirDetalle = onAbrirDetalle,
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
fun ListaContenido(
    items: List<Item>,
    onAbrirDetalle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items, key = { it.id }) { item ->
            ItemFila(item = item, onClick = { onAbrirDetalle(item.id) })
        }
    }
}

private val itemsDePrueba = listOf(
    Item(1, "iPhone 13 Pro", "Apple", 1099.99, null),
    Item(2, "Galaxy S10", "Samsung", 699.99, null, esFavorito = true),
    Item(3, "Oppo F19 Pro+", "Oppo", 399.99, null),
    Item(4, "Realme XT", "Realme", 349.99, null)
)

@Preview(showBackground = true)
@Composable
private fun ListaPreview() {
    ProductosTheme {
        ListaContenido(items = itemsDePrueba, onAbrirDetalle = {})
    }
}
