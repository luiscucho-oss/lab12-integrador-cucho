package com.tecsup.productos.data.remote.dto

import kotlinx.serialization.Serializable

/** DummyJSON envuelve la lista: { "products": [...], "total": 16, ... } */
@Serializable
data class ProductosResponse(
    val products: List<ItemDto> = emptyList(),
    val total: Int = 0
)

/** Lo que responde la API para cada producto (solo los campos que usamos). */
@Serializable
data class ItemDto(
    val id: Int,
    val title: String,
    val description: String = "",
    val category: String = "",
    val price: Double = 0.0,
    val rating: Double = 0.0,
    val stock: Int = 0,
    val brand: String? = null,
    val thumbnail: String? = null,
    val images: List<String> = emptyList()
)
