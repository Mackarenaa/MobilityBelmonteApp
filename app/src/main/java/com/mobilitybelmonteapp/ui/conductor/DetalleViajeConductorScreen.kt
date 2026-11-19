package com.mobilitybelmonteapp.ui.conductor

import android.widget.Button
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.TripOrigin
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobilitybelmonteapp.data.local.estadoViaje
import com.mobilitybelmonteapp.data.remote.EstadoViaje
import com.mobilitybelmonteapp.ui.componentes.BarraSuperior
import com.mobilitybelmonteapp.ui.componentes.EstadoChip
import com.mobilitybelmonteapp.ui.componentes.LineaInfo
import com.mobilitybelmonteapp.ui.componentes.SeccionHistorial
import com.mobilitybelmonteapp.util.NavegationExterna


@Composable
fun DetalleViajeConductorScreen(
    viajeId: Long,
    onVolver: () -> Unit,
    onEscanear: () -> Unit,
    vm: ConductorViewModel = viewModel()
) {
    val context = LocalContext.current
    val viajeFlow = remember(viajeId) { vm.observarViaje(viajeId) }
    val viaje by viajeFlow.collectAsStateWithLifecycle(initialValue = null)
    val historialFlow = remember(viajeId) { vm.historial(viajeId) }
    val historial by historialFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(vm.mensaje) {
        vm.mensaje?.let {
            snackbar.showSnackbar(it)
            vm.mensajeMostrado()
        }
    }


    Scaffold(

        topBar = { BarraSuperior("Detalle del viaje", onVolver = onVolver) },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        val v = viaje
        if (v == null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val estado = v.estadoViaje()
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${v.hora} · ${v.fecha}",
                        style = MaterialTheme.typography.titleLarge
                    )
                    EstadoChip(estado)
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LineaInfo(Icons.Default.Person, v.pasajeroNombre)
                    LineaInfo(Icons.Default.TripOrigin, v.origenDireccion)
                    LineaInfo(Icons.Default.Place, v.destinoDireccion)
                    v.numeroVuelo?.let { LineaInfo(Icons.Default.Flight, "Vuelo $it") }
                }

                if (estado == EstadoViaje.ASIGNADO || estado == EstadoViaje.EN_CURSO) {
                    val haciaDestino = estado == EstadoViaje.EN_CURSO
                    val lat = if (haciaDestino) v.destinoLat else v.origenLat
                    val lng = if (haciaDestino) v.destinoLng else v.origenLng

                    Text(
                        if (haciaDestino) "Ir al destino" else "Ir a buscar al Pasajero",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = {
                            NavegationExterna.abrirGoogleMaps(
                                context,
                                lat,
                                lng
                            )
                        }) {
                            Icon(Icons.Default.Navigation, contentDescription = null)
                            Text(" Google Maps")
                        }
                        OutlinedButton(onClick = {
                            NavegationExterna.abrirWaze(
                                context,
                                lat,
                                lng
                            )
                        }) {
                            Icon(Icons.Default.Navigation, contentDescription = null)
                            Text(" Waze")
                        }
                    }
                }

                when (estado) {
                    EstadoViaje.ASIGNADO -> {
                        Button(onClick = onEscanear, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                            Text(" Escanear QR del pasajero")
                        }
                        TextButton(
                            onClick = { vm.iniciarSinQr(v.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Iniciar viaje sin QR")
                        }
                    }
                    EstadoViaje.EN_CURSO -> {
                        Button(onClick = { vm.finalizar(v.id) },
                            modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.Flag, contentDescription = null)
                            Text(" Finalizar Viaje")
                        }
                    }
                    EstadoViaje.FINALIZADO -> Text("Este Viaje ya Finalizó.")
                    EstadoViaje.PENDIENTE -> Text("Este viaje aún No está asignado")
                }
                SeccionHistorial(historial)
            }
        }
    }
}

