package com.mobilitybelmonteapp.ui.componentes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.TripOrigin
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mobilitybelmonteapp.data.local.ViajeLocal
import com.mobilitybelmonteapp.data.local.estadoViaje


@Composable
fun ViajeCard(
    viaje: ViajeLocal,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    mostrarPasajero: Boolean = false,
    mostrarConductor: Boolean = false,
    acciones: @Composable ColumnScope.() -> Unit = {}
) {
    Card(modifier = modifier
        .fillMaxWidth()
        .clickable(onClick = onClick)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${viaje.hora} · ${viaje.fecha}",
                    style = MaterialTheme.typography.titleMedium
                )
                EstadoChip(viaje.estadoViaje())
            }
            LineaInfo(Icons.Default.TripOrigin, viaje.origenDireccion)
            LineaInfo(Icons.Default.Place, viaje.destinoDireccion)
            if (mostrarPasajero) LineaInfo(Icons.Default.Person, viaje.pasajeroNombre)
            if (mostrarConductor) {
                viaje.conductorNombre?.let { LineaInfo(Icons.Default.DirectionsCar, it) }
            }
            viaje.numeroVuelo?.let { LineaInfo(Icons.Default.Flight, "Vuelo $it") }
            acciones
        }
    }
}

@Composable
fun LineaInfo (icono: ImageVector, texto: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.primary
        )



        Text (text = texto, style = MaterialTheme.typography.bodyMedium)
    }
}