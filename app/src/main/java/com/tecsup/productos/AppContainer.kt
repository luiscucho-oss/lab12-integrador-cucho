package com.tecsup.productos

import com.tecsup.productos.data.remote.ApiService
import com.tecsup.productos.data.remote.RetrofitClient
import com.tecsup.productos.data.repository.ItemRepository
import com.tecsup.productos.data.repository.ItemRepositoryImpl

/** Crea la API y los repositorios una sola vez para toda la app. */
class AppContainer {

    private val api: ApiService by lazy { RetrofitClient.crearApi() }

    val itemRepository: ItemRepository by lazy { ItemRepositoryImpl(api, CATEGORIA) }

    companion object {
        /** Variante del tema: categoría de DummyJSON que se descarga. */
        const val CATEGORIA = "smartphones"
    }
}
