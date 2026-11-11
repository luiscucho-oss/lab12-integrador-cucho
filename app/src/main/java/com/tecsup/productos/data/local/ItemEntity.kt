package com.tecsup.productos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Tabla de Room: lo que se guarda en el celular. */
@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey val id: Int,
    val titulo: String,
    val subtitulo: String,
    val precio: Double,
    val imagenUrl: String?,
    val esFavorito: Boolean = false
)
