package com.tecsup.productos.data.mapper

import com.tecsup.productos.data.remote.dto.ItemDto
import com.tecsup.productos.domain.model.Item

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
