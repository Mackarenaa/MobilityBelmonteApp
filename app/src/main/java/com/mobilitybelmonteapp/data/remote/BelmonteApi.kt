package com.mobilitybelmonteapp.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface BelmonteApi {
    @GET("api/ping")
    suspend fun ping(): Map<String, String>

    @POST("api/auth/login")
    suspend fun login(@Body req: LoginRequest): LoginResponse

    @POST("api/viajes")
    suspend fun crearViaje(@Body req: ViajeRequest) : ViajeResponse

    @GET("api/viajes/{id}")
    suspend fun obtenerViaje (@Path("id") id: Long) : ViajeResponse

    @GET("api/viajes/pasajero/{id}")
    suspend fun viajeDePasajero(@Path("id") id: Long) : List<ViajeResponse>

    @GET("api/viajes/pendientes")
    suspend fun pendientes() : List<ViajeResponse>

    @PUT("api/viajes/{id}/asignar")
    suspend fun asignar (@Path("id") id: Long, @Body req: AsignarRequest) : ViajeResponse

    @PATCH("api/viajes/{id}/estado")
    suspend fun cambiarEstado (@Path ("id") id: Long, @Body req: EstadoRequest) : ViajeResponse

    @GET("api/conductores/disponibles")
    suspend fun conductoresDisponibles() : List<ConductorResponse>

    @GET("api/conductores/{id}/viajes")
    suspend fun viajesDeConductor(
        @Path("id") id: Long,
        @Query("fecha") fecha:  String? = null
    ) : List<ViajeResponse>
}