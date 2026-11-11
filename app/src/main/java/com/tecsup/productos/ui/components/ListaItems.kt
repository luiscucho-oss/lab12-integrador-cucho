package com.tecsup.productos.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tecsup.productos.domain.model.Item
import com.tecsup.productos.ui.theme.ProductosTheme

/** Lista reutilizada por Catálogo y Favoritos. */
@Composable
fun ListaItems(
    items: List<Item>,
    onAbrirDetalle: (Int) -> Unit,
    onFavorito: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items, key = { it.id }) { item ->
            ItemFila(
                item = item,
                onClick = { onAbrirDetalle(item.id) },
                onFavorito = { onFavorito(item.id) }
            )
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
private fun ListaItemsPreview() {
    ProductosTheme {
        ListaItems(items = itemsDePrueba, onAbrirDetalle = {}, onFavorito = {})
    }
}
