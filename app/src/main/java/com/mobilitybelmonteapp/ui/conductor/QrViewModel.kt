package com.mobilitybelmonteapp.ui.conductor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mobilitybelmonteapp.data.BelmonteRepository
import com.mobilitybelmonteapp.data.SessionManager
import com.mobilitybelmonteapp.data.local.ViajeLocal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ResultadoQr {
    data class Ok (val viaje: ViajeLocal) : ResultadoQr
    data class Error (val mensaje: String) : ResultadoQr
}

class QrviewModel (app: Application) : AndroidViewModel(app) {
    private val repo = BelmonteRepository.get(app)
    private val conductorId = SessionManager.sesion?.usuarioId ?: -1L

    private val _contenidoQR = MutableStateFlow("")
    val contenidoQR: StateFlow<String> = _contenidoQR.asStateFlow()

    private val _resultado = MutableStateFlow<ResultadoQr?>(null)
    val resultado: StateFlow<ResultadoQr?> = _resultado.asStateFlow()

    fun guardarQr(contenido: String) {
        _contenidoQR.value = contenido
        viewModelScope.launch {
            repo.confirmarAbordajePorQr(contenido, conductorId).fold(
                onSuccess = { _resultado.value = ResultadoQr.Ok(it) },
                onFailure = { _resultado.value = ResultadoQr.Error(it.message ?: "No se pudo leer el QR") }
            )
        }
    }

    fun limpiarResultado() {
        _resultado.value = null
    }
}