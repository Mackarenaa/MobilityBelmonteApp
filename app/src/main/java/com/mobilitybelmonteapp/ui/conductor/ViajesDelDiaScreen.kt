package com.mobilitybelmonteapp.ui.conductor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobilitybelmonteapp.ui.componentes.BarraSuperior
import com.mobilitybelmonteapp.ui.componentes.ViajeCard
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ViajesDelDiaScreen(
    onVerDetalle: (Long) -> Unit,
    onCerrarSesion: () -> Unit,
    vm: ConductorViewModel = viewModel()
) {
    val viajes by vm.viajes.collectAsStateWithLifecycle()
    val soloHoy by vm.soloHoy.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { BarraSuperior("Mis viajes", onCerrarSesion = onCerrarSesion) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(selected = !soloHoy, onClick = { vm.filtrarHoy(false) }, label = { Text("Todos") })
                FilterChip(selected = soloHoy, onClick = { vm.filtrarHoy(true) }, label = { Text("Hoy") })
            }

            if (viajes.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tienes viajes asignados", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn (
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(viajes, key = { it.id }) { viaje ->
                        ViajeCard(
                            viaje = viaje,
                            onClick = { onVerDetalle(viaje.id) },
                            mostrarPasajero = true
                        )
                    }
                }
            }
        }
    }
}