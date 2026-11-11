package com.tecsup.productos.data.mapper

import com.tecsup.productos.data.local.ItemEntity
import com.tecsup.productos.data.remote.dto.ItemDto
import com.tecsup.productos.domain.model.Item

/** Dto (API) -> Entity (Room). El favorito lo decide el repositorio. */
fun ItemDto.aEntity(esFavorito: Boolean): ItemEntity = ItemEntity(
    id = id,
    titulo = title,
    subtitulo = brand ?: category,
    precio = price,
    imagenUrl = thumbnail,
    esFavorito = esFavorito
)

/** Entity (Room) -> modelo de la UI. */
fun ItemEntity.aModelo(): Item = Item(
    id = id,
    titulo = titulo,
    subtitulo = subtitulo,
    precio = precio,
    imagenUrl = imagenUrl,
    esFavorito = esFavorito
)

/** Dto -> modelo con todos los campos del detalle. */
fun ItemDto.aModelo(esFavorito: Boolean = false): Item = Item(
    id = id,
    titulo = title,
    subtitulo = brand ?: category,
    precio = price,
    imagenUrl = thumbnail,
    esFavorito = esFavorito,
    descripcion = description,
    categoria = category,
    calificacion = rating,
    stock = stock,
    imagenes = images
)
