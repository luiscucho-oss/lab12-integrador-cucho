package com.tecsup.productos.fakes

import com.tecsup.productos.data.repository.ItemRepository
import com.tecsup.productos.domain.model.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Repositorio falso: guarda los datos en memoria (hace de Room)
 * y la "API" responde lo que el test ponga en [respuestaApi].
 */
class FakeItemRepository(
    guardadosIniciales: List<Item> = emptyList()
) : ItemRepository {

    private val guardados = MutableStateFlow(guardadosIniciales)

    /** Lo que devolverá la API en el próximo refrescar(). */
    var respuestaApi: Result<List<Item>> = Result.success(emptyList())

    var vecesRefrescado = 0
        private set

    override val items: Flow<List<Item>> = guardados

    override val favoritos: Flow<List<Item>> =
        guardados.map { lista -> lista.filter { it.esFavorito } }

    override suspend fun refrescar(): Result<Int> {
        vecesRefrescado++
        return respuestaApi.map { nuevos ->
            // Igual que el repositorio real: se conservan los favoritos
            val favoritos = guardados.value.filter { it.esFavorito }.map { it.id }.toSet()
            guardados.value = nuevos.map { it.copy(esFavorito = it.id in favoritos) }
            nuevos.size
        }
    }

    override suspend fun obtenerDetalle(id: Int): Result<Item> =
        guardados.value.find { it.id == id }
            ?.let { Result.success(it) }
            ?: Result.failure(NoSuchElementException("No existe el id $id"))

    override suspend fun alternarFavorito(id: Int) {
        guardados.update { lista ->
            lista.map { if (it.id == id) it.copy(esFavorito = !it.esFavorito) else it }
        }
    }

    override suspend fun cantidadGuardada(): Int = guardados.value.size

    override suspend fun cantidadFavoritos(): Int = guardados.value.count { it.esFavorito }

    override suspend fun limpiarCache() {
        guardados.value = emptyList()
    }
}
