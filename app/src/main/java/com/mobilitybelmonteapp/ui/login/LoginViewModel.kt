package com.mobilitybelmonteapp.ui.login

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobilitybelmonteapp.data.BelmonteRepository
import com.mobilitybelmonteapp.data.Sesion
import com.mobilitybelmonteapp.data.SessionManager
import com.mobilitybelmonteapp.data.remote.Rol
import kotlinx.coroutines.launch

class LoginViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = BelmonteRepository.get(app)

    var correo by mutableStateOf("")
    var password by mutableStateOf("")
    var cargando by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun rellena(correoDemo: String) {
        correo = correoDemo
        password = "Demo1234"
        error = null
    }

    fun iniciarSesion(onExito: (Rol) -> Unit) {
        if (correo.isBlank() || password.isBlank()) {
            error = "Ingresa tu correo y contraseña"
            return
        }
        viewModelScope.launch {
            cargando = true
            error = null
            repo.login(correo, password).fold(
                onSuccess =  { u ->
                    val rol = Rol.valueOf(u.rol)
                    SessionManager.iniciar(Sesion(u.id, u.nombre, u.correo, rol))
                    onExito(rol)
                },
                onFailure = { error = it.message }
            )
            cargando = false
        }
    }
}