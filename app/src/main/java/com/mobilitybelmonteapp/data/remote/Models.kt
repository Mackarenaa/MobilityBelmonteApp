package com.mobilitybelmonteapp.data.remote

import java.time.LocalDate
import java.time.LocalTime

enum class Rol { PASAJERO, CONDUCTOR, ADMIN }

enum class EstadoViaje { PENDIENTE, ASIGNADO, EN_CURSO, FINALIZADO }

data class LoginRequest(
    val correo: String,
    val password: String
)

data class LoginResponse(
    val usuarioId: Long?,
    val nombre: String,
    val rol: Rol,
    val perfilId: Long?
)

data class ViajeRequest(
    val pasajeroId: Long,
    val fecha: String,
    val hora: String,
    val origenDireccion: String,
    val origenLat: Double,
    val origenLng: Double,
    val destienoDireccion: String,
    val destinoLat: Double,
    val destinoLng: Double,
    val numeroVuelo: String? = null
)

data class ViajeResponse(
    val id: Long?,
    val pasajeroId: Long?,
    val pasajeroNombre: String,
    val conductorId: Long?,
    val conductorNombre: String?,
    val fecha: String,
    val hora: String,
    val origenDireccion: String,
    val origenLat: Double,
    val origenLng: Double,
    val destienoDireccion: String,
    val destinoLat: Double,
    val destinoLng: Double,
    val numeroVuelo: String?,
    val estado: EstadoViaje
)

data class ConductorResponse(
    val id: Long?,
    val nombre: String,
    val patenteVehiculo: String?,
    val disponibilidad: Boolean
)

data class AsignarRequest(
    val conductorId: Long
)

data class EstadoRequest(
    val estado: EstadoViaje
)

data class ErrorResponse(
    val status: Int,
    val mensaje: String,
    val detalles: Map<String, String>? = null
)