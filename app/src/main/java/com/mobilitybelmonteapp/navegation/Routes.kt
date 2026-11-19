package com.mobilitybelmonteapp.navegation

object Routes {
    const val LOGIN = "login"
    const val PASAJERO_VIAJES = "pasajero/viajes"
    const val PASAJERO_NUEVA = "pasajero/nueva"
    const val PASAJERO_DETALLE = "pasajero/detalle/{viajeId}"
    const val CONDUCTOR_VIAJES = " condcutor/viajes"
    const val CONDUCTOR_DETALLE = "conductor/detalle/{viajeId}"
    const val CONDUCTOR_ESCANER = "conductor/escaner"
    const val ADMIN_PENDIENTES = "admin/pendientes"

    fun pasajeroDetalle(id: Long) = "pasajero/detalle/$id"
    fun conductorDetalle(id: Long) = "conductor/detalle/$id"
}