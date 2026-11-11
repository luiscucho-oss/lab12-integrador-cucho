package com.tecsup.productos.domain.model

/**
 * Modelo que usa la UI. No sabe nada de Retrofit ni de Room.
 * Los campos opcionales solo llegan desde el detalle (segundo endpoint).
 */
data class Item(
    val id: Int,
    val titulo: String,
    val subtitulo: String,
    val precio: Double,
    val imagenUrl: String?,
    val esFavorito: Boolean = false,
    val descripcion: String? = null,
    val categoria: String? = null,
    val calificacion: Double? = null,
    val stock: Int? = null,
    val imagenes: List<String> = emptyList()
)
