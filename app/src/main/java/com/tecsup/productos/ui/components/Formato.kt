package com.tecsup.productos.ui.components

import java.util.Locale

fun formatearPrecio(precio: Double): String = String.format(Locale.US, "$ %.2f", precio)
