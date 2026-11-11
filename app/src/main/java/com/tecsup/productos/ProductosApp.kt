package com.tecsup.productos

import android.app.Application

/** El contenedor vive en Application para no recrearse al girar la pantalla. */
class ProductosApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer()
    }
}
