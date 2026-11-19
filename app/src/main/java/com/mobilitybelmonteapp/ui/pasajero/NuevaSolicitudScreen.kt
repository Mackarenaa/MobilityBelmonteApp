package com.mobilitybelmonteapp.ui.pasajero

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobilitybelmonteapp.ui.componentes.BarraSuperior
import com.mobilitybelmonteapp.util.Formato
import com.mobilitybelmonteapp.util.Lugar
import com.mobilitybelmonteapp.util.Lugares


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaSolicitudScreen(
    onVolver: () -> Unit,
    onCreada: () -> Unit,
    vm: PasajeroViewModel = viewModel()
) {
    var origen by remember { mutableStateOf<Lugar?>(null) }
    var destino by remember { mutableStateOf<Lugar?>(null) }
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var vuelo by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var verFecha by remember { mutableStateOf(false) }
    var verHora by remember { mutableStateOf(false) }

    Scaffold(topBar = { BarraSuperior("Nueva Solicitud", onVolver = onVolver) })
    { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SelectorLugar("Origen", origen) { origen = it }
            SelectorLugar("Destino", destino) { destino = it }

            CampoClicable("Fecha", fecha, Icons.Default.CalendarMonth) { verFecha = true }
            CampoClicable("Hora", hora, Icons.Default.Schedule) { verHora = true }

            OutlinedTextField(
                value = vuelo,
                onValueChange = { if (it.length <= 10) vuelo = it.uppercase() },
                label = { Text("N° de Vuelo (opcional)") },
                leadingIcon = { Icon(Icons.Default.Flight, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val o = origen
                    val d = destino
                    error = when {
                        o == null || d == null -> "Selecciona el origen y el destino"
                        o.nombre == d.nombre -> "El origen y el destino NO pueden ser iguales"
                        fecha.isEmpty() -> "Selecciona la fecha y la hora"
                        fecha < Formato.hoyIso() -> "La fecha NO puede ser anterior a hoy"
                        else -> null
                    }
                    if (error == null && o != null && d != null) {
                        vm.crearViaje(o, d, fecha, hora, vuelo.trim().ifEmpty { null }, onCreada)
                    }
                }
            ) {
                Text("Solicitud traslado")
            }
        }
    }

    if (verFecha) {
        val estado = rememberDatePickerState(initialSelectedDateMillis = Formato.millisUtcDeHoy())
        DatePickerDialog(
            onDismissRequest = { verFecha = false },
            confirmButton = {
                TextButton(onClick = {
                    estado.selectedDateMillis?.let { fecha = Formato.isoDesdeMillisUtc(it) }
                    verFecha = false
                }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { verFecha = false }) { Text("Cancelar") } }
        ) {
            DatePicker(state = estado)
        }
    }

    if (verHora) {
        val estado = rememberTimePickerState(initialHour = 9, initialMinute = 0, is24Hour = true)
        AlertDialog(
            onDismissRequest = { verHora = false },
            confirmButton = {
                TextButton(onClick = {
                    hora = "%02d:%02d".format(estado.hour, estado.minute)
                    verHora = false
                }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { verHora = false }) { Text("Cancelar") } },
            text = {
                TimePicker(state = estado)
            }

        )
    }
}

@Composable
private fun CampoClicable(label: String, valor: String, icono: ImageVector, onClick: () -> Unit) {
    Box {
        OutlinedTextField(
            value = valor,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            leadingIcon = { Icon(icono, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            Modifier
                .matchParentSize()
                .clickable(onClick = onClick)
        )
    }
}

@Composable
private fun SelectorLugar(label: String, seleccionado: Lugar?, onSeleccion: (Lugar) -> Unit) {
    var abierto by remember { mutableStateOf(false) }
    Box {
        CampoClicable(
            label,
            seleccionado?.nombre ?: "",
            Icons.Default.Place
        ) { abierto = true }
        DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            Lugares.todos.forEach { lugar ->
                DropdownMenuItem(
                    text = { Text(lugar.nombre) },
                    onClick = {
                        onSeleccion(lugar)
                        abierto = false
                    }
                )
            }
        }
    }
}
