package com.tecsup.productos.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.tecsup.productos.AppContainer
import com.tecsup.productos.ui.admin.AdminViewModel
import com.tecsup.productos.ui.detalle.DetalleViewModel
import com.tecsup.productos.ui.favoritos.FavoritosViewModel
import com.tecsup.productos.ui.lista.ListaViewModel
import com.tecsup.productos.ui.login.LoginViewModel

/** Crea cada ViewModel pasándole sus dependencias desde el AppContainer. */
class ViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val viewModel = when (modelClass) {
            LoginViewModel::class.java -> LoginViewModel(container.authRepository)
            ListaViewModel::class.java -> ListaViewModel(
                container.itemRepository,
                container.notificador
            )
            DetalleViewModel::class.java -> DetalleViewModel(
                container.itemRepository,
                extras.createSavedStateHandle()
            )
            FavoritosViewModel::class.java -> FavoritosViewModel(container.itemRepository)
            AdminViewModel::class.java -> AdminViewModel(
                container.itemRepository,
                container.notificador
            )
            else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
        return viewModel as T
    }
}
