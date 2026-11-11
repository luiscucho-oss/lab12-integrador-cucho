package com.tecsup.productos.notification

/** El ViewModel pide la notificación por esta interface, sin conocer Context. */
interface Notificador {
    fun mostrar(titulo: String, texto: String)
}
