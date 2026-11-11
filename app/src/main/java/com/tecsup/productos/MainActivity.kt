package com.tecsup.productos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import com.tecsup.productos.ui.lista.ListaContenido
import com.tecsup.productos.ui.lista.itemsDePrueba
import com.tecsup.productos.ui.theme.ProductosTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProductosTheme {
                Scaffold(topBar = { TopAppBar(title = { Text("Productos") }) }) { padding ->
                    ListaContenido(
                        items = itemsDePrueba,
                        onAbrirDetalle = {},
                        modifier = Modifier.padding(padding)
                    )
                }
            }
        }
    }
}
