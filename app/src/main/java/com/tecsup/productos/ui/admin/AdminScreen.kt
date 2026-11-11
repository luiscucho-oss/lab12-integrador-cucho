package com.tecsup.productos.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.productos.ui.UiState
import com.tecsup.productos.ui.components.Cargando

@Composable
fun AdminScreen(
    viewModel: AdminViewModel,
    modifier: Modifier = Modifier
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val ocupado by viewModel.ocupado.collectAsStateWithLifecycle()
    val mensaje by viewModel.mensaje.collectAsStateWithLifecycle()

    val snackbar = remember { SnackbarHostState() }
    var confirmarLimpieza by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbar.showSnackbar(it)
            viewModel.mensajeMostrado()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (val e = estado) {
            UiState.Cargando -> Cargando()
            is UiState.Error -> Text(e.mensaje, modifier = Modifier.padding(24.dp))
            is UiState.Exito -> PanelAdmin(
                resumen = e.datos,
                ocupado = ocupado,
                onSincronizar = viewModel::sincronizar,
                onLimpiar = { confirmarLimpieza = true }
            )
        }
        SnackbarHost(snackbar, modifier = Modifier.align(Alignment.BottomCenter))
    }

    if (confirmarLimpieza) {
        AlertDialog(
            onDismissRequest = { confirmarLimpieza = false },
            title = { Text("Limpiar caché") },
            text = { Text("Se borrarán del celular los productos guardados y los favoritos. Puedes volver a descargarlos con Sincronizar.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarLimpieza = false
                    viewModel.limpiarCache()
                }) { Text("Limpiar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarLimpieza = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun PanelAdmin(
    resumen: ResumenAdmin,
    ocupado: Boolean,
    onSincronizar: () -> Unit,
    onLimpiar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Panel de administración", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TarjetaTotal("Guardados", resumen.totalGuardados, Modifier.weight(1f))
            TarjetaTotal("Favoritos", resumen.totalFavoritos, Modifier.weight(1f))
        }
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onSincronizar,
            enabled = !ocupado,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (ocupado) {
                CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
            } else {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Sincronizar")
            }
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = onLimpiar,
            enabled = !ocupado,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(Icons.Default.Delete, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Limpiar caché")
        }
    }
}

@Composable
private fun TarjetaTotal(titulo: String, valor: Int, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = valor.toString(),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
