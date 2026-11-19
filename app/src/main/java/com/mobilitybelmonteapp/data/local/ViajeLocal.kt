package com.mobilitybelmonteapp.data.local



import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mobilitybelmonteapp.data.remote.EstadoViaje
import java.security.MessageDigest


@Entity(tableName = "usuarios", indices = [Index(value = ["correo"], unique = true)])
data class UsuarioLocal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val correo: String,
    val passwordHash: String,
    val rol: String,
    val patenteVehiculo: String? = null
)

@Entity(tableName = "viajes")
data class ViajeLocal (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pasajeroId: Long,
    val pasajeroNombre: String,
    val conductorId: Long? = null,
    val conductorNombre: String? = null,
    val fecha: String,
    val hora: String,
    val origenDireccion: String,
    val origenLat: Double,
    val origenLng: Double,
    val destinoDireccion: String,
    val destinoLat: Double,
    val destinoLng: Double,
    val numeroVuelo: String? = null,
    val Estado: String = EstadoViaje.PENDIENTE.name,
    val codigoQr: String = ""
)

@Entity(tableName = "historial")
data class HistorialLocal (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val viajeId: Long,
    val estado: String,
    val fechaHora: String
)

fun ViajeLocal.estadoViaje(): EstadoViaje = EstadoViaje.valueOf(Estado)

/* fun para la contraseña */
fun hashClave( clave: String) : String =
    MessageDigest.getInstance("SHA-256")
        .digest(clave.toByteArray())
        .joinToString ("") {"%02x".format(it) }