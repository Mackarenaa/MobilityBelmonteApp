package com.mobilitybelmonteapp.ui.conductor

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mobilitybelmonteapp.data.BelmonteRepository
import com.mobilitybelmonteapp.data.SessionManager
import com.mobilitybelmonteapp.data.local.HistorialLocal
import com.mobilitybelmonteapp.data.local.ViajeLocal
import com.mobilitybelmonteapp.data.remote.EstadoViaje
import com.mobilitybelmonteapp.util.Formato
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ConductorViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = BelmonteRepository.get(app)
    private val conductorId = SessionManager.sesion?.usuarioId ?: -1L

    private val _soloHoy = MutableStateFlow(false)
    val soloHoy: StateFlow<Boolean> = _soloHoy.asStateFlow()

    val viajes: StateFlow<List<ViajeLocal>> =
        combine(repo.viajesDeConductor(conductorId), _soloHoy) { lista, hoy ->
            if (hoy) lista.filter {
                it.fecha == Formato.hoyIso()
            } else
                lista
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    var mensaje by androidx.compose.runtime.mutableStateOf<String?>(null)
        private set

    fun filtrarHoy(hoy: Boolean) {
        _soloHoy.value = hoy
    }

    fun historial(id: Long): Flow<List<HistorialLocal>> = repo.historial(id)
    fun observarViaje(id: Long): Flow<ViajeLocal?> = repo.observarViaje(id)
    fun mensajeMostrado() {
        mensaje = null
    }

    fun iniciarSinQr(viajeId: Long) = cambiar(viajeId, EstadoViaje.EN_CURSO, "Viaje Iniciado")
    fun finalizar(viajeId: Long) = cambiar(viajeId, EstadoViaje.FINALIZADO, "Viaje Finalizado")

    private fun cambiar(viajeId: Long, estado: EstadoViaje, exito: String) {
        viewModelScope.launch {
            repo.cambiarEstado(viajeId, estado).fold(
                onSuccess = { mensaje = exito },
                onFailure = { mensaje = it.message }
            )
        }
    }
}
