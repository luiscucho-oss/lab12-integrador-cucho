package com.tecsup.productos.fakes

import com.tecsup.productos.data.auth.AuthRepository
import com.tecsup.productos.domain.model.Sesion

/** Auth falso: cuenta cuántas veces se llamó a "Firebase". */
class FakeAuthRepository(
    private var sesionGuardada: Sesion? = null
) : AuthRepository {

    var resultadoLogin: Result<Sesion> = Result.failure(Exception("Sin configurar"))

    var llamadasLogin = 0
        private set

    override suspend fun iniciarSesion(correo: String, clave: String): Result<Sesion> {
        llamadasLogin++
        return resultadoLogin.onSuccess { sesionGuardada = it }
    }

    override suspend fun sesionActual(): Sesion? = sesionGuardada

    override fun cerrarSesion() {
        sesionGuardada = null
    }
}
