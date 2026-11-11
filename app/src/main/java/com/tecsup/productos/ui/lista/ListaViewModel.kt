package com.tecsup.productos.ui.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.productos.data.repository.ItemRepository
import com.tecsup.productos.domain.model.Item
import com.tecsup.productos.notification.Notificador
import com.tecsup.productos.ui.UiState
import com.tecsup.productos.ui.mensajeDeError
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class ListaViewModel(
    private val repo: ItemRepository,
    private val notificador: Notificador
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<List<Item>>>(UiState.Cargando)
    val estado: StateFlow<UiState<List<Item>>> = _estado.asStateFlow()

    // Aviso que se muestra encima de la lista cuando no se pudo actualizar
    private val _aviso = MutableStateFlow<String?>(null)
    val aviso: StateFlow<String?> = _aviso.asStateFlow()

    private var refrescoTerminado = false
    private var trabajoRefresco: Job? = null

    init {
        // La lista siempre sale de Room
        viewModelScope.launch {
            repo.items.collect { lista ->
                if (lista.isNotEmpty() || refrescoTerminado) _estado.value = UiState.Exito(lista)
            }
        }
        refrescar()
    }

    fun refrescar() {
        if (trabajoRefresco?.isActive == true) return
        trabajoRefresco = viewModelScope.launch {
            if (_estado.value is UiState.Error) _estado.value = UiState.Cargando
            val resultado = repo.refrescar()
            refrescoTerminado = true
            resultado
                .onSuccess { n ->
                    if (_estado.value is UiState.Cargando) _estado.value = UiState.Exito(emptyList())
                    _aviso.value = null
                    notificador.mostrar("Datos actualizados", "Se guardaron $n elementos")
                }
                .onFailure { e ->
                    if (repo.cantidadGuardada() == 0) {
                        // Sin internet y sin nada guardado: pantalla de error con Reintentar
                        _estado.value = UiState.Error(mensajeDeError(e))
                    } else {
                        // Sin internet pero con datos guardados: se muestran con un aviso
                        _aviso.value = if (e is IOException) {
                            "Sin conexión: mostrando datos guardados"
                        } else {
                            "No se pudo actualizar: mostrando datos guardados"
                        }
                    }
                }
        }
    }

    fun alternarFavorito(id: Int) {
        viewModelScope.launch { repo.alternarFavorito(id) }
    }
}
