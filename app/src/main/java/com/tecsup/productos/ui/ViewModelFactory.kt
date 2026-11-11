package com.tecsup.productos.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.tecsup.productos.AppContainer
import com.tecsup.productos.ui.lista.ListaViewModel

/** Crea cada ViewModel pasándole sus dependencias desde el AppContainer. */
class ViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val viewModel = when (modelClass) {
            ListaViewModel::class.java -> ListaViewModel(container.itemRepository)
            else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
        return viewModel as T
    }
}
