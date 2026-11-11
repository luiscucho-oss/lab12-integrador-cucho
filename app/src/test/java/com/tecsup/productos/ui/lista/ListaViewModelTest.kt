package com.tecsup.productos.ui.lista

import com.tecsup.productos.domain.model.Item
import com.tecsup.productos.fakes.FakeItemRepository
import com.tecsup.productos.fakes.FakeNotificador
import com.tecsup.productos.fakes.MainDispatcherRule
import com.tecsup.productos.ui.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ListaViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val notificador = FakeNotificador()

    private val productos = listOf(
        Item(1, "iPhone 13 Pro", "Apple", 1099.99, null),
        Item(2, "Galaxy S10", "Samsung", 699.99, null),
        Item(3, "Realme XT", "Realme", 349.99, null)
    )

    @Test
    fun `al refrescar con exito muestra la lista y notifica cuantos guardo`() = runTest {
        val repo = FakeItemRepository().apply { respuestaApi = Result.success(productos) }

        val viewModel = ListaViewModel(repo, notificador)
        advanceUntilIdle()

        assertEquals(UiState.Exito(productos), viewModel.estado.value)
        assertNull(viewModel.aviso.value)
        assertEquals(
            listOf("Datos actualizados" to "Se guardaron 3 elementos"),
            notificador.mostradas
        )
    }

    @Test
    fun `sin internet y sin datos guardados muestra el error`() = runTest {
        val repo = FakeItemRepository().apply { respuestaApi = Result.failure(IOException()) }

        val viewModel = ListaViewModel(repo, notificador)
        advanceUntilIdle()

        assertEquals(UiState.Error("No hay conexión a internet"), viewModel.estado.value)
        assertTrue(notificador.mostradas.isEmpty())
    }

    @Test
    fun `sin internet con datos guardados muestra los datos y un aviso`() = runTest {
        val repo = FakeItemRepository(guardadosIniciales = productos).apply {
            respuestaApi = Result.failure(IOException())
        }

        val viewModel = ListaViewModel(repo, notificador)
        advanceUntilIdle()

        assertEquals(UiState.Exito(productos), viewModel.estado.value)
        assertEquals("Sin conexión: mostrando datos guardados", viewModel.aviso.value)
        assertTrue(notificador.mostradas.isEmpty())
    }

    @Test
    fun `si la api responde una lista vacia queda el estado vacio`() = runTest {
        val repo = FakeItemRepository().apply { respuestaApi = Result.success(emptyList()) }

        val viewModel = ListaViewModel(repo, notificador)
        advanceUntilIdle()

        assertEquals(UiState.Exito(emptyList<Item>()), viewModel.estado.value)
    }

    @Test
    fun `error del servidor muestra el codigo`() = runTest {
        val error500 = HttpException(Response.error<Any>(500, "".toResponseBody()))
        val repo = FakeItemRepository().apply { respuestaApi = Result.failure(error500) }

        val viewModel = ListaViewModel(repo, notificador)
        advanceUntilIdle()

        assertEquals(UiState.Error("El servidor respondió con error 500"), viewModel.estado.value)
    }

    @Test
    fun `reintentar despues de un error carga la lista`() = runTest {
        val repo = FakeItemRepository().apply { respuestaApi = Result.failure(IOException()) }
        val viewModel = ListaViewModel(repo, notificador)
        advanceUntilIdle()

        repo.respuestaApi = Result.success(productos)
        viewModel.refrescar()
        advanceUntilIdle()

        assertEquals(UiState.Exito(productos), viewModel.estado.value)
        assertEquals(2, repo.vecesRefrescado)
    }

    @Test
    fun `el favorito se conserva al volver a sincronizar`() = runTest {
        val repo = FakeItemRepository().apply { respuestaApi = Result.success(productos) }
        val viewModel = ListaViewModel(repo, notificador)
        advanceUntilIdle()

        viewModel.alternarFavorito(2)
        viewModel.refrescar()
        advanceUntilIdle()

        val lista = (viewModel.estado.value as UiState.Exito).datos
        assertTrue(lista.first { it.id == 2 }.esFavorito)
        assertEquals(1, lista.count { it.esFavorito })
    }
}
