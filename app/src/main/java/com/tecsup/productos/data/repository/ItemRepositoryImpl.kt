package com.tecsup.productos.data.repository

import com.tecsup.productos.data.mapper.aModelo
import com.tecsup.productos.data.remote.ApiService
import com.tecsup.productos.domain.model.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

class ItemRepositoryImpl(
    private val api: ApiService,
    private val categoria: String
) : ItemRepository {

    // Por ahora los datos viven en memoria
    private val cache = MutableStateFlow<List<Item>>(emptyList())

    override val items: Flow<List<Item>> = cache
    override val favoritos: Flow<List<Item>> = cache.map { lista -> lista.filter { it.esFavorito } }

    override suspend fun refrescar(): Result<Int> = try {
        val nuevos = api.listar(categoria).products.map { it.aModelo() }
        cache.value = nuevos
        Result.success(nuevos.size)
    } catch (e: IOException) {          // sin internet
        Result.failure(e)
    } catch (e: HttpException) {        // 404, 500...
        Result.failure(e)
    } catch (e: SerializationException) { // la respuesta no tiene el formato esperado
        Result.failure(e)
    }

    override suspend fun obtenerDetalle(id: Int): Result<Item> = try {
        val esFavorito = cache.value.any { it.id == id && it.esFavorito }
        Result.success(api.detalle(id).aModelo(esFavorito))
    } catch (e: IOException) {
        Result.failure(e)
    } catch (e: HttpException) {
        Result.failure(e)
    } catch (e: SerializationException) {
        Result.failure(e)
    }

    override suspend fun alternarFavorito(id: Int) {
        cache.update { lista ->
            lista.map { if (it.id == id) it.copy(esFavorito = !it.esFavorito) else it }
        }
    }

    override suspend fun cantidadGuardada(): Int = cache.value.size

    override suspend fun cantidadFavoritos(): Int = cache.value.count { it.esFavorito }

    override suspend fun limpiarCache() {
        cache.value = emptyList()
    }
}
