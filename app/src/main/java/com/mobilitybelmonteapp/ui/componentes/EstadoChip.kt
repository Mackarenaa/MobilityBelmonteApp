package com.mobilitybelmonteapp.ui.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mobilitybelmonteapp.data.local.HistorialLocal
import com.mobilitybelmonteapp.data.remote.EstadoViaje

fun EstadoViaje.etiqueta(): String = when (this) {
    EstadoViaje.PENDIENTE -> "Pendiente"
    EstadoViaje.ASIGNADO -> "Asignado"
    EstadoViaje.EN_CURSO -> "En curso"
    EstadoViaje.FINALIZADO -> "Finalizado"
}

@Composable
fun EstadoChip (estado: EstadoViaje) {
    val colores = MaterialTheme.colorScheme
    val (fondo, texto) = when (estado) {
        EstadoViaje.PENDIENTE -> colores.secondaryContainer to colores.onSecondaryContainer
        EstadoViaje.ASIGNADO -> colores.tertiaryContainer to colores.onTertiaryContainer
        EstadoViaje.EN_CURSO -> colores.primaryContainer to colores.onPrimaryContainer
        EstadoViaje.FINALIZADO -> colores.surfaceVariant to colores.onSurfaceVariant
    }
    Surface(shape = RoundedCornerShape(50), color = fondo) {
        Text(
            text =estado.etiqueta(),
            style = MaterialTheme.typography.labelMedium, color = texto,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun SeccionHistorial (items: List<HistorialLocal>) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("Seguimiento", style = MaterialTheme.typography.titleMedium)
        items.forEach { h ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text (
                    text = h.fechaHora,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text (
                    text = EstadoViaje.valueOf(h.estado).etiqueta(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}