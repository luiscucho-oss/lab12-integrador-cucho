package com.tecsup.productos.ui.lista

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.productos.ui.UiState
import com.tecsup.productos.ui.components.Cargando
import com.tecsup.productos.ui.components.ErrorView
import com.tecsup.productos.ui.components.ListaItems
import com.tecsup.productos.ui.components.Vacio
import com.tecsup.productos.ui.theme.ProductosTheme

@Composable
fun ListaScreen(
    viewModel: ListaViewModel,
    onAbrirDetalle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val aviso by viewModel.aviso.collectAsStateWithLifecycle()

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
                Column(modifier = modifier.fillMaxSize()) {
                    aviso?.let { texto ->
                        AvisoSinConexion(texto = texto, onReintentar = viewModel::refrescar)
                    }
                    ListaItems(
                        items = e.datos,
                        onAbrirDetalle = onAbrirDetalle,
                        onFavorito = viewModel::alternarFavorito,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun AvisoSinConexion(texto: String, onReintentar: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Warning, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Text(
                text = texto,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onReintentar) { Text("Reintentar") }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AvisoPreview() {
    ProductosTheme {
        AvisoSinConexion(texto = "Sin conexión: mostrando datos guardados", onReintentar = {})
    }
}
