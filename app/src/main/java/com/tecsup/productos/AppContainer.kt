package com.tecsup.productos

import android.content.Context
import com.tecsup.productos.data.auth.AuthRepository
import com.tecsup.productos.data.auth.FirebaseAuthRepository
import com.tecsup.productos.data.local.AppDatabase
import com.tecsup.productos.data.remote.ApiService
import com.tecsup.productos.data.remote.RetrofitClient
import com.tecsup.productos.data.repository.ItemRepository
import com.tecsup.productos.data.repository.ItemRepositoryImpl
import com.tecsup.productos.notification.Notificador
import com.tecsup.productos.notification.NotificadorAndroid

/** Crea la API, la base de datos y los repositorios una sola vez para toda la app. */
class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    private val api: ApiService by lazy { RetrofitClient.crearApi() }

    private val database: AppDatabase by lazy { AppDatabase.crear(appContext) }

    val itemRepository: ItemRepository by lazy {
        ItemRepositoryImpl(api, database.itemDao(), CATEGORIA)
    }

    val authRepository: AuthRepository by lazy { FirebaseAuthRepository() }

    val notificador: Notificador by lazy { NotificadorAndroid(appContext) }

    companion object {
        /** Variante del tema: categoría de DummyJSON que se descarga. */
        const val CATEGORIA = "smartphones"
    }
}
