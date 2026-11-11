package com.tecsup.productos.ui.detalle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.productos.domain.model.Item
import com.tecsup.productos.ui.UiState
import com.tecsup.productos.ui.components.Cargando
import com.tecsup.productos.ui.components.ErrorView
import com.tecsup.productos.ui.components.ImagenItem
import com.tecsup.productos.ui.components.formatearPrecio

@Composable
fun DetalleScreen(
    viewModel: DetalleViewModel,
    modifier: Modifier = Modifier
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    when (val e = estado) {
        UiState.Cargando -> Cargando(modifier)
        is UiState.Error -> ErrorView(e.mensaje, onReintentar = viewModel::cargar, modifier = modifier)
        is UiState.Exito -> DetalleContenido(item = e.datos, modifier = modifier)
    }
}

@Composable
private fun DetalleContenido(
    item: Item,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val imagenes = item.imagenes.ifEmpty { listOfNotNull(item.imagenUrl) }
        if (imagenes.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(imagenes) { url ->
                    ImagenItem(
                        url = url,
                        modifier = Modifier
                            .size(260.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(item.titulo, style = MaterialTheme.typography.headlineSmall)
            Text(
                text = item.subtitulo,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = formatearPrecio(item.precio),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item.calificacion?.let { calificacion ->
                    AssistChip(
                        onClick = {},
                        label = { Text("%.1f".format(calificacion)) },
                        leadingIcon = { Icon(Icons.Default.Star, contentDescription = "Calificación") }
                    )
                }
                item.stock?.let { stock ->
                    AssistChip(onClick = {}, label = { Text("Stock: $stock") })
                }
                item.categoria?.let { categoria ->
                    AssistChip(onClick = {}, label = { Text(categoria) })
                }
            }

            if (!item.descripcion.isNullOrBlank()) {
                Spacer(Modifier.height(12.dp))
                Text("Descripción", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(item.descripcion, style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
