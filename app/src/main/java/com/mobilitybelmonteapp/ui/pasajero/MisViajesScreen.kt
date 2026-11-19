package com.mobilitybelmonteapp.ui.pasajero

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobilitybelmonteapp.data.SessionManager
import com.mobilitybelmonteapp.ui.componentes.BarraSuperior
import com.mobilitybelmonteapp.ui.componentes.ViajeCard

@Composable
fun MisViajesScreen(
    onNuevaSolicitus: () -> Unit,
    onVerDetalle: (Long) -> Unit,
    onCerrarSesion: () -> Unit,
    vm: PasajeroViewModel = viewModel()
) {
    val viajes by vm.viajes.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { BarraSuperior("Mis Viajes", onCerrarSesion = onCerrarSesion) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNuevaSolicitus,
                icon = {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null
                    )
                },
                text = { Text("Nueva Solicitud") }
            )
        }
    ) { padding ->
        if (viajes.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Aun no hay Solicitudes",
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        "Hola, ${SessionManager.sesion?.nombre ?: ""}",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                items(viajes, key = { it.id }) {
                    viaje ->
                    ViajeCard(
                        viaje = viaje,
                        onClick = { onVerDetalle (viaje.id) },
                        mostrarConductor = true
                    )
                }
            }
        }
    }
}