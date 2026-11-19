package com.mobilitybelmonteapp.ui.pasajero

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mobilitybelmonteapp.data.BelmonteRepository
import com.mobilitybelmonteapp.data.SessionManager
import com.mobilitybelmonteapp.data.local.HistorialLocal
import com.mobilitybelmonteapp.data.local.ViajeLocal
import com.mobilitybelmonteapp.util.Lugar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PasajeroViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = BelmonteRepository.get(app)

    val viajes: StateFlow<List<ViajeLocal>> =
        repo.viajesDePasajero(SessionManager.sesion?.usuarioId ?: -1L)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun observarViaje (id: Long) : Flow<ViajeLocal?> = repo.observarViaje(id)
    fun historial (id: Long) : Flow<List<HistorialLocal>> = repo.historial(id)
    fun cancelar (viajeId: Long, onListo: () -> Unit) {
        viewModelScope.launch {
            repo.cancelarSolicitus(viajeId).onSuccess { onListo() }
        }
    }
    fun crearViaje(
        origen: Lugar,
        destino: Lugar,
        fecha: String,
        hora: String,
        vuelo: String?,
        onListo: () -> Unit
    ){
        val sesion = SessionManager.sesion ?: return
        viewModelScope.launch {
            repo.crearViaje(sesion.usuarioId, sesion.nombre, origen, destino, fecha, hora, vuelo)
            onListo()
        }
    }
}