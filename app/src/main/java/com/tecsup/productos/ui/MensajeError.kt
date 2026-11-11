package com.tecsup.productos.ui

import retrofit2.HttpException
import java.io.IOException

/** Traduce una excepción a un mensaje entendible para la persona. */
fun mensajeDeError(e: Throwable): String = when (e) {
    is IOException -> "No hay conexión a internet"
    is HttpException -> "El servidor respondió con error ${e.code()}"
    else -> "Ocurrió un error inesperado"
}
