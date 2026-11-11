package com.tecsup.productos.fakes

import com.tecsup.productos.notification.Notificador

/** Notificador falso: en vez de mostrar, anota lo que se pidió. */
class FakeNotificador : Notificador {

    val mostradas = mutableListOf<Pair<String, String>>()

    override fun mostrar(titulo: String, texto: String) {
        mostradas += titulo to texto
    }
}
