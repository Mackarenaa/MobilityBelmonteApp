package com.mobilitybelmonteapp.data

import com.mobilitybelmonteapp.data.remote.Rol

data class  Sesion (
    val usuarioId: Long,
    val nombre: String,
    val correo: String,
    val rol: Rol
)

object SessionManager {
    var sesion: Sesion? = null
        private set

    fun iniciar (s: Sesion) {
        sesion = s
    }
    fun cerrar(){
        sesion = null
    }
}

