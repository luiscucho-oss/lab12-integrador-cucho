package com.tecsup.productos.data.auth

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.tecsup.productos.domain.model.Rol
import com.tecsup.productos.domain.model.Sesion
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AuthRepository {

    override suspend fun iniciarSesion(correo: String, clave: String): Result<Sesion> = try {
        val uid = auth.signInWithEmailAndPassword(correo, clave).await().user!!.uid
        Result.success(leerSesion(uid, correo))
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        // Si entró a Auth pero no se pudo leer el rol, no se deja la sesión a medias
        auth.signOut()
        Result.failure(Exception(mensajeDe(e), e))
    }

    override suspend fun sesionActual(): Sesion? {
        val u = auth.currentUser ?: return null
        return try {
            leerSesion(u.uid, u.email ?: "")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Sin conexión y sin el rol en caché: se entra con el rol de menor permiso
            Sesion(u.uid, u.email ?: "", Rol.USUARIO)
        }
    }

    override fun cerrarSesion() = auth.signOut()

    /** El rol NO se decide en la app: se lee de Firestore en usuarios/{uid}. */
    private suspend fun leerSesion(uid: String, correo: String): Sesion {
        val doc = db.collection("usuarios").document(uid).get().await()
        val rol = if (doc.getString("rol") == "admin") Rol.ADMIN else Rol.USUARIO
        return Sesion(uid, correo, rol)
    }

    private fun mensajeDe(e: Exception): String = when (e) {
        is FirebaseAuthInvalidCredentialsException,
        is FirebaseAuthInvalidUserException -> "Correo o contraseña incorrectos"
        is FirebaseNetworkException -> "No hay conexión a internet"
        is FirebaseTooManyRequestsException -> "Demasiados intentos. Espera un momento y vuelve a intentar"
        is FirebaseFirestoreException -> "No se pudo leer el rol del usuario"
        else -> "No se pudo iniciar sesión"
    }
}
