package com.mobilitybelmonteapp.data

import android.content.Context
import com.mobilitybelmonteapp.data.local.AppDatabase
import com.mobilitybelmonteapp.data.local.HistorialLocal
import com.mobilitybelmonteapp.data.local.UsuarioLocal
import com.mobilitybelmonteapp.data.local.ViajeLocal
import com.mobilitybelmonteapp.data.local.hashClave
import com.mobilitybelmonteapp.data.remote.EstadoViaje
import com.mobilitybelmonteapp.data.remote.Rol
import com.mobilitybelmonteapp.util.Formato
import com.mobilitybelmonteapp.util.Lugar
import com.mobilitybelmonteapp.util.Lugares
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class BelmonteRepository private constructor(db: AppDatabase) {
    private val usuarioDao = db.usuarioDao()
    private val viajeDao = db.viajeDao()
    private val historialDao = db.historialDao()
    private val mutex = Mutex()

    suspend fun asegurarDatos() {
        mutex.withLock {
            if (usuarioDao.contar() == 0) sembrar()
        }
    }

    suspend fun login(correo: String, password: String): Result<UsuarioLocal> {
        asegurarDatos()
        val usuario = usuarioDao.buscarPorCorreo(correo.trim().lowercase())
        return if (usuario != null && usuario.passwordHash == hashClave(password)) {
            Result.success(usuario)
        } else {
            Result.failure(Exception("Correo o contraseña incorrectos"))
        }
    }

    fun conductores(): Flow<List<UsuarioLocal>> = usuarioDao.conductores()
    fun viajesDePasajero(id: Long): Flow<List<ViajeLocal>> = viajeDao.delPasajero(id)
    fun viajesDeConductor(id: Long): Flow<List<ViajeLocal>> = viajeDao.delConductor(id)
    fun pendientes(): Flow<List<ViajeLocal>> = viajeDao.pendientes()
    fun observarViaje(id: Long): Flow<ViajeLocal?> = viajeDao.observarPorId(id)
    fun historial(viajeId: Long): Flow<List<HistorialLocal>> = historialDao.delViaje(viajeId)

    suspend fun crearViaje(
        pasajeroId: Long,
        pasajeroNombre: String,
        origen: Lugar,
        destino: Lugar,
        fecha: String,
        hora: String,
        vuelo: String?
    ): Long = insertarViaje(
        nuevoViaje(
            pasajeroId, pasajeroNombre, null, null,
            EstadoViaje.PENDIENTE, fecha, hora, origen, destino, vuelo
        )
    )

    suspend fun asignar(viajeId: Long, conductor: UsuarioLocal):
            Result<Unit> {
        val viaje = viajeDao.porId(viajeId)
            ?: return Result.failure(Exception("Viaje no encontrado"))
        if (viaje.Estado != EstadoViaje.PENDIENTE.name) {
            return Result.failure(Exception("Solo se puede asignar un viaje cone stado pendiente"))
        }
        viajeDao.actualizar(
            viaje.copy(
                conductorId = conductor.id,
                conductorNombre = conductor.nombre,
                Estado = EstadoViaje.ASIGNADO.name
            )
        )
        registrarHistorial(viajeId, EstadoViaje.ASIGNADO)
        return Result.success(Unit)
    }

    suspend fun cambiarEstado(viajeId: Long, nuevo: EstadoViaje): Result<Unit> {
        val viaje = viajeDao.porId(viajeId)
            ?: return Result.failure(Exception("Viaje no encontrado"))
        val actual = EstadoViaje.valueOf(viaje.Estado)
        if (nuevo == EstadoViaje.ASIGNADO || nuevo.ordinal != actual.ordinal + 1) {
            return Result.failure(Exception("Transición no permitida $actual -> $nuevo"))
        }
        viajeDao.actualizar(viaje.copy(Estado = nuevo.name))
        registrarHistorial(viajeId, nuevo)
        return Result.success(Unit)
    }

    suspend fun cancelarSolicitus(viajeId: Long): Result<Unit> {
        val viaje = viajeDao.porId(viajeId)
            ?: return Result.failure(Exception("Viaje no encontrado"))
        if (viaje.Estado != EstadoViaje.PENDIENTE.name) {
            return Result.failure(Exception("Solo se puede cancelar una solicitud pendiente"))
        }
        historialDao.eliminarDelViaje(viajeId)
        viajeDao.eliminar(viaje)
        return Result.success(Unit)
    }
    suspend fun confirmarAbordajePorQr(codigo: String, conductorId: Long): Result<ViajeLocal> {
        val viaje = viajeDao.porCodigoQr(codigo.trim())
            ?: return Result.failure(Exception("Código QR no reconocido"))
        if (viaje.conductorId != conductorId) {
            return Result.failure(Exception("Este viaje esra ASIGNADO a otro conductor"))
        }
        if (viaje.Estado != EstadoViaje.ASIGNADO.name) {
            return Result.failure(Exception("El viaje no esta listo para iniciar (estado: ${viaje.Estado})"))
        }
        val actualizado = viaje.copy(Estado = EstadoViaje.EN_CURSO.name)
        viajeDao.actualizar(actualizado)
        registrarHistorial(viaje.id, EstadoViaje.EN_CURSO)
        return Result.success(actualizado)
    }

    private fun nuevoViaje(
        pasajeroId: Long, pasajeroNombre: String,
        conductorId: Long?, conductorNombre: String?,
        estado: EstadoViaje, fecha: String, hora: String,
        origen: Lugar, destino: Lugar, vuelo: String?
    ) = ViajeLocal(
        pasajeroId = pasajeroId,
        pasajeroNombre = pasajeroNombre,
        conductorId = conductorId,
        conductorNombre = conductorNombre,
        fecha = fecha,
        hora = hora,
        origenDireccion = origen.nombre,
        origenLat = origen.lat,
        origenLng = origen.Ing,
        destinoDireccion = destino.nombre,
        destinoLat = destino.lat,
        destinoLng = destino.Ing,
        numeroVuelo = vuelo,
        Estado = estado.name
    )

    private suspend fun insertarViaje(viaje: ViajeLocal): Long {
        val id = viajeDao.insertar(viaje)
        viajeDao.actualizarQr (id, "BELMONTE-V$id")
        historialDao.insertar(
            HistorialLocal(
                viajeId = id,
                estado = viaje.Estado,
                fechaHora = Formato.ahoraIso()
            )
        )
        return id
    }

    private suspend fun registrarHistorial(viajeId: Long, estado: EstadoViaje) {
        historialDao.insertar(
            HistorialLocal(
                viajeId = viajeId,
                estado = estado.name,
                fechaHora = Formato.ahoraIso()
            )
        )
    }

    private suspend fun sembrar() {
        val clave = hashClave("Demo1234")
        usuarioDao.insertar (UsuarioLocal(
            nombre = "Admin Demo",
            correo = "admin@belmonte.test",
            passwordHash = clave,
            rol = Rol.ADMIN.name
        ))
        val p1 = usuarioDao.insertar(
            UsuarioLocal(
                nombre = "Pasajero Uno",
                correo = "pasajero1@belmonte.test",
                passwordHash = clave,
                rol = Rol.PASAJERO.name
            )
        )
        val p2 = usuarioDao.insertar(
            UsuarioLocal(
                nombre = "Pasajero Dos",
                correo = "pasajero2@belmonte,test",
                passwordHash = clave,
                rol = Rol.PASAJERO.name
            )
        )
        val c1 = usuarioDao.insertar(
            UsuarioLocal(
                nombre = "Conducto Uno",
                correo = "conductor1@belmonte.test",
                passwordHash = clave,
                rol = Rol.CONDUCTOR.name,
                patenteVehiculo = "AAAA-11"
            )
        )
        val c2 = usuarioDao.insertar(
            UsuarioLocal(
                nombre = "Conductor Dos",
                correo = "conductor2@belmonte.test",
                passwordHash = clave,
                rol = Rol.CONDUCTOR.name,
                patenteVehiculo = "BBBB-22"
            )
        )

        val hoy = Formato.hoyIso()
        insertarViaje(
            nuevoViaje(
                p1,
                "Pasajero uno",
                c1,
                "Conductor Uno",
                EstadoViaje.ASIGNADO,
                hoy,
                "08:30",
                Lugares.aeropuerto,
                Lugares.lasCondes,
                "LA502"
            )
        )
        insertarViaje(
            nuevoViaje(
                p2,
                "Pasajero Dos",
                null,
                null,
                EstadoViaje.PENDIENTE,
                hoy,
                "11:00",
                Lugares.providencia,
                Lugares.vitacura,
                null
            )
        )
        insertarViaje(
            nuevoViaje(
                p1,
                "Pasajero Uno",
                c1,
                "Conductor Uno",
                EstadoViaje.EN_CURSO,
                hoy,
                "16:15",
                Lugares.centro,
                Lugares.aeropuerto,
                "LA801"
            )
        )
        insertarViaje(
            nuevoViaje(
                p2,
                "Pasajero Dos",
                c2,
                "Conductor Dos",
                EstadoViaje.FINALIZADO,
                hoy,
                "07:00",
                Lugares.nunoa,
                Lugares.costanera,
                null
            )
        )
        insertarViaje(
            nuevoViaje(
                p1,
                "Pasajero Uno",
                null,
                null,
                EstadoViaje.PENDIENTE,
                hoy,
                "19:30",
                Lugares.huechuraba,
                Lugares.aeropuerto,
                "LA603"
            )
        )
        insertarViaje(
            nuevoViaje(
                p2,
                "Pasajero Dos",
                c1,
                "Conductor 1",
                EstadoViaje.ASIGNADO,
                hoy,
                "18:00",
                Lugares.elGolf,
                Lugares.estacionCentral,
                null
            )
        )
    }

    companion object {
        @Volatile
        private var instancia: BelmonteRepository? = null

        fun get(context: Context): BelmonteRepository = instancia ?: synchronized(this) {
            instancia ?: BelmonteRepository(AppDatabase.get(context)).also { instancia = it }
        }
    }
}