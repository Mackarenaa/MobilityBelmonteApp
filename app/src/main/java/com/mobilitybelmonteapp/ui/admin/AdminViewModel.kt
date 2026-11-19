package com.mobilitybelmonteapp.ui.admin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mobilitybelmonteapp.data.BelmonteRepository
import com.mobilitybelmonteapp.data.local.UsuarioLocal
import com.mobilitybelmonteapp.data.local.ViajeLocal
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.collections.emptyList
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class AdminViewModel (app: Application) : AndroidViewModel (app) {
    private val repo  = BelmonteRepository.get(app)

    val pendientes : StateFlow<List<ViajeLocal>> = repo.pendientes().stateIn(viewModelScope,
        SharingStarted.WhileSubscribed(5_000), emptyList())
    val conductores: StateFlow<List<UsuarioLocal>> = repo.conductores().stateIn(viewModelScope,
        SharingStarted.WhileSubscribed(5_000), emptyList())
    var mensaje by mutableStateOf<String?>(null)
        private set

    fun mensajeMostrado() {
        mensaje = null
    }

    fun asignar(viaje: ViajeLocal, conductor: UsuarioLocal) {
        viewModelScope.launch {
            repo.asignar(viaje.id, conductor).fold(
                onSuccess = { mensaje = "Viaje#${viaje.id} asignado a $conductor.nombre"},
                onFailure = { mensaje = it.message }
            )
        }
    }
}