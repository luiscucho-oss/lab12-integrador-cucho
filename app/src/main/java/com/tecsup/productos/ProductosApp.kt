package com.tecsup.productos

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory

/** El contenedor vive en Application para no recrearse al girar la pantalla. */
class ProductosApp : Application(), ImageLoaderFactory {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }

    /**
     * El CDN de DummyJSON responde "Cache-Control: no-store", así que Coil no guardaba
     * las imágenes y sin internet la lista se veía sin fotos. Se ignoran esas cabeceras
     * para que las imágenes queden en la caché de disco y se vean en modo sin conexión.
     */
    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .respectCacheHeaders(false)
            .build()
}
