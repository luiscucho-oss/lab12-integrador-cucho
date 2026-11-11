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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.productos.ui.ViewModelFactory
import com.tecsup.productos.ui.lista.ListaScreen
import com.tecsup.productos.ui.lista.ListaViewModel
import com.tecsup.productos.ui.theme.ProductosTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val factory = ViewModelFactory((application as ProductosApp).container)
        setContent {
            ProductosTheme {
                Scaffold(topBar = { TopAppBar(title = { Text("Productos") }) }) { padding ->
                    val listaViewModel: ListaViewModel = viewModel(factory = factory)
                    ListaScreen(
                        viewModel = listaViewModel,
                        onAbrirDetalle = {},
                        modifier = Modifier.padding(padding)
                    )
                }
            }
        }
    }
}
