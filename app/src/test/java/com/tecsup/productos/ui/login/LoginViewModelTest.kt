package com.tecsup.productos.ui.login

import com.tecsup.productos.domain.model.Rol
import com.tecsup.productos.domain.model.Sesion
import com.tecsup.productos.fakes.FakeAuthRepository
import com.tecsup.productos.fakes.MainDispatcherRule
import com.tecsup.productos.ui.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val admin = Sesion("uid-admin", "admin@prueba.com", Rol.ADMIN)

    @Test
    fun `campos vacios muestran validacion y no llaman a firebase`() = runTest {
        val auth = FakeAuthRepository()
        val viewModel = LoginViewModel(auth)
        advanceUntilIdle()

        viewModel.iniciarSesion("  ", "")

        assertEquals(UiState.Error("Ingresa tu correo y tu contraseña"), viewModel.estado.value)
        assertEquals(0, auth.llamadasLogin)
    }

    @Test
    fun `credenciales incorrectas muestran el mensaje del repositorio`() = runTest {
        val auth = FakeAuthRepository().apply {
            resultadoLogin = Result.failure(Exception("Correo o contraseña incorrectos"))
        }
        val viewModel = LoginViewModel(auth)
        advanceUntilIdle()

        viewModel.iniciarSesion("usuario@prueba.com", "mala")
        advanceUntilIdle()

        assertEquals(UiState.Error("Correo o contraseña incorrectos"), viewModel.estado.value)
        assertEquals(1, auth.llamadasLogin)
    }

    @Test
    fun `login correcto entrega la sesion con su rol`() = runTest {
        val auth = FakeAuthRepository().apply { resultadoLogin = Result.success(admin) }
        val viewModel = LoginViewModel(auth)
        advanceUntilIdle()

        viewModel.iniciarSesion("admin@prueba.com", "clave123")
        advanceUntilIdle()

        assertEquals(UiState.Exito(admin), viewModel.estado.value)
    }

    @Test
    fun `si ya habia sesion entra directo y cerrar sesion vuelve al login`() = runTest {
        val viewModel = LoginViewModel(FakeAuthRepository(sesionGuardada = admin))
        advanceUntilIdle()

        assertFalse(viewModel.verificando.value)
        assertEquals(UiState.Exito(admin), viewModel.estado.value)

        viewModel.cerrarSesion()

        assertEquals(UiState.Exito(null), viewModel.estado.value)
    }
}
