package com.tecsup.productos.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.productos.data.repository.ItemRepository
import com.tecsup.productos.ui.UiState
import com.tecsup.productos.ui.mensajeDeError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ResumenAdmin(
    val totalGuardados: Int,
    val totalFavoritos: Int
)

class AdminViewModel(
    private val repo: ItemRepository
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<ResumenAdmin>>(UiState.Cargando)
    val estado: StateFlow<UiState<ResumenAdmin>> = _estado.asStateFlow()

    // true mientras se sincroniza o se limpia la caché
    private val _ocupado = MutableStateFlow(false)
    val ocupado: StateFlow<Boolean> = _ocupado.asStateFlow()

    // Mensaje corto para el Snackbar
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    init {
        // Cada vez que cambia la tabla (sincronizar, favorito, limpiar) se recalculan los totales
        viewModelScope.launch {
            repo.items.collect { cargarTotales() }
        }
    }

    fun sincronizar() {
        if (_ocupado.value) return
        viewModelScope.launch {
            _ocupado.value = true
            repo.refrescar()
                .onSuccess { n -> _mensaje.value = "Se guardaron $n elementos" }
                .onFailure { e -> _mensaje.value = "No se pudo sincronizar: ${mensajeDeError(e)}" }
            cargarTotales()
            _ocupado.value = false
        }
    }

    fun limpiarCache() {
        if (_ocupado.value) return
        viewModelScope.launch {
            _ocupado.value = true
            repo.limpiarCache()
            cargarTotales()
            _mensaje.value = "Caché limpiada"
            _ocupado.value = false
        }
    }

    fun mensajeMostrado() {
        _mensaje.value = null
    }

    private suspend fun cargarTotales() {
        _estado.value = UiState.Exito(
            ResumenAdmin(
                totalGuardados = repo.cantidadGuardada(),
                totalFavoritos = repo.cantidadFavoritos()
            )
        )
    }
}
