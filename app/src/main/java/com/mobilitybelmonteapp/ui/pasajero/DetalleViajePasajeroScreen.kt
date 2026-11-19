package com.mobilitybelmonteapp.ui.pasajero

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirtyLens
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.TripOrigin
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobilitybelmonteapp.data.local.estadoViaje
import com.mobilitybelmonteapp.data.remote.EstadoViaje
import com.mobilitybelmonteapp.ui.componentes.BarraSuperior
import com.mobilitybelmonteapp.ui.componentes.EstadoChip
import com.mobilitybelmonteapp.ui.componentes.LineaInfo
import com.mobilitybelmonteapp.ui.componentes.SeccionHistorial
import com.mobilitybelmonteapp.util.generarQr


@Composable
fun DetalleViajePasajeroScreen (
    viajeId: Long,
    onVolver: () -> Unit,
    vm: PasajeroViewModel = viewModel()
){
    val viajeFlow = remember(viajeId) { vm.observarViaje(viajeId) }
    val viaje by viajeFlow.collectAsStateWithLifecycle( initialValue = null)
    val historialFlow = remember(viajeId) { vm.historial(viajeId) }
    val historial by historialFlow.collectAsStateWithLifecycle( initialValue = emptyList())

    Scaffold(topBar = { BarraSuperior("Detalle del Viaje", onVolver = onVolver) }) { padding ->
        val v = viaje
        if (v == null ) {
            Box(Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
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
                ){
                    Text("${v.hora} · ${v.fecha}", style = MaterialTheme.typography.titleLarge)
                    EstadoChip(v.estadoViaje())
                }

                Column( verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LineaInfo(Icons.Default.TripOrigin, v.origenDireccion)
                    LineaInfo(Icons.Default.Place, v.destinoDireccion)
                    v.numeroVuelo?.let { LineaInfo(Icons.Default.Flight, "Vuelo $it") }
                    LineaInfo(Icons.Default.DirectionsCar, v.conductorNombre ?: "Conductor por Asignar")
                }

                when (v.estadoViaje()) {
                    EstadoViaje.ASIGNADO -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val imagen = remember ( v.codigoQr) {
                                generarQr ( v.codigoQr).asImageBitmap() }
                            Image(
                                bitmap =  imagen,
                                contentDescription = "Código QR del viaje",
                                modifier = Modifier
                                    .size(220.dp)
                                    .background(Color.White, RoundedCornerShape(12.dp))
                                    .padding(8.dp)
                            )
                            Text(
                                "Muestra este Código al conductor al Abordar",
                                style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center
                            )
                        }
                    }

                    EstadoViaje.PENDIENTE -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text( "Tu Código QR aparecerá cuando se asigne un Conductor.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedButton(
                            onClick = { vm.cancelar(v.id, onVolver) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Cancelar solicitud")
                        }
                    }

                    EstadoViaje.EN_CURSO -> Text( "Vaije en curso.")
                    EstadoViaje.FINALIZADO -> Text("Este viaje ya Finalizó")
                }

                SeccionHistorial(historial)
            }
        }
    }
}