package com.tecsup.productos.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.productos.data.auth.AuthRepository
import com.tecsup.productos.domain.model.Sesion
import com.tecsup.productos.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val auth: AuthRepository
) : ViewModel() {

    // Exito(null) = no hay sesión (se ve el login) · Exito(sesion) = ya ingresó
    private val _estado = MutableStateFlow<UiState<Sesion?>>(UiState.Cargando)
    val estado: StateFlow<UiState<Sesion?>> = _estado.asStateFlow()

    // true solo al abrir la app, mientras se revisa si había una sesión guardada
    private val _verificando = MutableStateFlow(true)
    val verificando: StateFlow<Boolean> = _verificando.asStateFlow()

    init {
        viewModelScope.launch {
            _estado.value = UiState.Exito(auth.sesionActual())
            _verificando.value = false
        }
    }

    fun iniciarSesion(correo: String, clave: String) {
        if (_estado.value is UiState.Cargando) return

        // Se valida antes de llamar a Firebase
        val correoLimpio = correo.trim()
        val errorCampos = when {
            correoLimpio.isEmpty() && clave.isEmpty() -> "Ingresa tu correo y tu contraseña"
            correoLimpio.isEmpty() -> "Ingresa tu correo"
            clave.isEmpty() -> "Ingresa tu contraseña"
            else -> null
        }
        if (errorCampos != null) {
            _estado.value = UiState.Error(errorCampos)
            return
        }

        _estado.value = UiState.Cargando
        viewModelScope.launch {
            _estado.value = auth.iniciarSesion(correoLimpio, clave).fold(
                onSuccess = { UiState.Exito(it) },
                onFailure = { UiState.Error(it.message ?: "No se pudo iniciar sesión") }
            )
        }
    }

    fun cerrarSesion() {
        auth.cerrarSesion()
        _estado.value = UiState.Exito(null)
    }
}
