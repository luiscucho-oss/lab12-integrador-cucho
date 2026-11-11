package com.tecsup.productos.data.repository

import com.tecsup.productos.data.local.ItemDao
import com.tecsup.productos.data.mapper.aEntity
import com.tecsup.productos.data.mapper.aModelo
import com.tecsup.productos.data.remote.ApiService
import com.tecsup.productos.domain.model.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

/**
 * Room es la fuente única de verdad: la UI siempre lee de Room.
 * Retrofit solo descarga y actualiza lo guardado.
 */
class ItemRepositoryImpl(
    private val api: ApiService,
    private val dao: ItemDao,
    private val categoria: String
) : ItemRepository {

    override val items: Flow<List<Item>> =
        dao.todos().map { lista -> lista.map { it.aModelo() } }

    override val favoritos: Flow<List<Item>> =
        dao.favoritos().map { lista -> lista.map { it.aModelo() } }

    override suspend fun refrescar(): Result<Int> = capturarErrores {
        // Si insertas sin cuidado, pierdes los favoritos. Se conservan aquí:
        val favoritos = dao.idsFavoritos().toSet()
        val nuevos = api.listar(categoria).products
            .map { it.aEntity(esFavorito = it.id in favoritos) }
        dao.guardarTodos(nuevos)
        nuevos.size
    }

    override suspend fun obtenerDetalle(id: Int): Result<Item> = capturarErrores {
        val esFavorito = dao.buscarPorId(id)?.esFavorito ?: false
        api.detalle(id).aModelo(esFavorito)
    }

    override suspend fun alternarFavorito(id: Int) = dao.alternarFavorito(id)

    override suspend fun cantidadGuardada(): Int = dao.contar()

    override suspend fun cantidadFavoritos(): Int = dao.contarFavoritos()

    override suspend fun limpiarCache() = dao.borrarTodo()

    /** Convierte los errores de red en Result.failure para que la app no se caiga. */
    private inline fun <T> capturarErrores(bloque: () -> T): Result<T> = try {
        Result.success(bloque())
    } catch (e: IOException) {            // sin internet
        Result.failure(e)
    } catch (e: HttpException) {          // 404, 500...
        Result.failure(e)
    } catch (e: SerializationException) { // el JSON no coincide con el DTO
        Result.failure(e)
    }
}
