package com.mobilitybelmonteapp.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobilitybelmonteapp.data.local.ViajeLocal
import com.mobilitybelmonteapp.ui.componentes.BarraSuperior
import com.mobilitybelmonteapp.ui.componentes.ViajeCard

@Composable
fun PendientesScreen(
    onCerrarSesion: () -> Unit,
    vm: AdminViewModel = viewModel()
) {
    val pendientes by vm.pendientes.collectAsState()
    val conductores by vm.conductores.collectAsState()
    var viajeAAsignar by remember { mutableStateOf<ViajeLocal?>(null) }
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(vm.mensaje) {
        vm.mensaje?.let {
            snackbar.showSnackbar(it)
            vm.mensajeMostrado()
        }
    }

    Scaffold(
        topBar = { BarraSuperior("Solicitudes pendientes", onCerrarSesion = onCerrarSesion) },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        if (pendientes.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No hay solicitudes pendientes", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pendientes, key = { it.id }) { viaje ->
                    ViajeCard(
                        viaje = viaje,
                        onClick = { viajeAAsignar = viaje },
                        mostrarPasajero = true
                    ) {
                        Button(
                            onClick = { viajeAAsignar = viaje },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Asignar conductor")
                        }
                    }
                }
            }
        }
    }

    viajeAAsignar?.let { viaje ->
        AlertDialog(
            onDismissRequest = { viajeAAsignar = null },
            title = { Text("Asignar conductor") },
            text = {
                androidx.compose.foundation.layout.Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Viaje de ${viaje.pasajeroNombre} a las ${viaje.hora}")
                    conductores.forEach { conductor ->
                        OutlinedButton(
                            onClick = {
                                vm.asignar(viaje, conductor)
                                viajeAAsignar = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("${conductor.nombre} · ${conductor.patenteVehiculo ?: "-"}")
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { viajeAAsignar = null }) { Text("Cancelar") } }
        )
    }
}