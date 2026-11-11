package com.tecsup.productos.data.remote

import com.tecsup.productos.data.remote.dto.ItemDto
import com.tecsup.productos.data.remote.dto.ProductosResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    // Lista: https://dummyjson.com/products/category/smartphones
    @GET("products/category/{slug}")
    suspend fun listar(@Path("slug") categoria: String): ProductosResponse

    // Detalle: https://dummyjson.com/products/121
    @GET("products/{id}")
    suspend fun detalle(@Path("id") id: Int): ItemDto
}
