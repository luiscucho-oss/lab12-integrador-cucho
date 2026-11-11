package com.tecsup.productos

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.tecsup.productos.ui.ViewModelFactory
import com.tecsup.productos.ui.navigation.AppNav
import com.tecsup.productos.ui.theme.ProductosTheme

class MainActivity : ComponentActivity() {

    // Si la persona niega el permiso, la app funciona igual pero sin notificaciones
    private val pedirPermiso =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Solo la primera vez, no al girar la pantalla
        if (savedInstanceState == null) pedirPermisoNotificaciones()

        val factory = ViewModelFactory((application as ProductosApp).container)
        setContent {
            ProductosTheme {
                AppNav(factory = factory)
            }
        }
    }

    private fun pedirPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            pedirPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
