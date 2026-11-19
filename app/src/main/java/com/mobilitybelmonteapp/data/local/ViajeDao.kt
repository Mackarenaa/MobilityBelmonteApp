package com.mobilitybelmonteapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface UsuarioDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(usuario: UsuarioLocal): Long

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun buscarPorCorreo(correo: String): UsuarioLocal?

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int

    @Query("SELECT * FROM usuarios WHERE rol = 'CONDUCTOR' ORDER BY nombre")
    fun conductores(): Flow<List<UsuarioLocal>>
}

@Dao
interface ViajeDao {
    @Insert
    suspend fun insertar(viaje: ViajeLocal): Long

    @Update
    suspend fun actualizar(viaje: ViajeLocal)

    @Delete
    suspend fun eliminar(viaje: ViajeLocal)

    @Query("UPDATE viajes SET codigoQr = :codigo WHERE id = :id")
    suspend fun actualizarQr(id: Long, codigo: String)

    @Query("SELECT * FROM viajes WHERE id = :id")
    suspend fun porId(id: Long): ViajeLocal?

    @Query("SELECT * FROM viajes WHERE id = :id")
    fun observarPorId(id: Long): Flow<ViajeLocal>

    @Query("SELECT * FROM viajes WHERE codigoQr = :codido LIMIT 1")
    suspend fun porCodigoQr(codido: String): ViajeLocal?

    @Query("SELECT *FROM viajes WHERE pasajeroId = :pasajeroId ORDER BY fecha DESC, hora Desc")
    fun delPasajero(pasajeroId: Long): Flow<List<ViajeLocal>>

    @Query("SELECT * FROM viajes WHERE estado = 'PENDIENTE' ORDER BY fecha,hora")
    fun pendientes(): Flow<List<ViajeLocal>>

    @Query("SELECT * FROM viajes WHERE conductorId = :conductorId ORDER BY fecha, hora")
    fun delConductor(conductorId: Long): Flow<List<ViajeLocal>>
}

@Dao
interface HistorialDao {
    @Insert
    suspend fun insertar(historial: HistorialLocal)

    @Query("SELECT * FROM historial WHERE viajeId = :viajeId ORDER BY id")
    fun delViaje(viajeId: Long): Flow<List<HistorialLocal>>

    @Query("DELETE FROM historial WHERE viajeId = :viajeId")
    suspend fun eliminarDelViaje(viajeId: Long  )
}