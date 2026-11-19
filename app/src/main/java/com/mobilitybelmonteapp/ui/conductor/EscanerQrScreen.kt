package com.mobilitybelmonteapp.ui.conductor

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.mlkit.vision.barcode.common.Barcode
import com.mobilitybelmonteapp.ui.componentes.BarraSuperior
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.mobilitybelmonteapp.R

@Composable
fun EscanerQrScreen(
    onVolver: () -> Unit,
    qrviewModel: QrviewModel = viewModel()
) {
    val context = LocalContext.current
    val contenidoQR by qrviewModel.contenidoQR.collectAsStateWithLifecycle()
    val resultado by qrviewModel.resultado.collectAsStateWithLifecycle()

    val opciones = remember {
        GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
    }

    val scanner = remember(context, opciones) {
        GmsBarcodeScanning.getClient(context, opciones)
    }

    fun escanear() {
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                qrviewModel.guardarQr(barcode.rawValue.orEmpty())
            }
            .addOnCanceledListener {
                // Escaneo cancelado por el usuario
            }
            .addOnFailureListener { error ->
                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // El escáner se abre solo al entrar a la pantalla
    LaunchedEffect(Unit) { escanear() }

    Scaffold(topBar = { BarraSuperior("Escanear QR", onVolver = onVolver) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Lector de Código QR",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = { escanear() }) {
                Text("Escanear QR")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Contenido del QR:",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = contenidoQR.ifEmpty { "Todavía no se ha escaneado ningún código" },
                textAlign = TextAlign.Center
            )
        }
    }

    when (val r = resultado) {
        is ResultadoQr.Ok -> AlertDialog(
            onDismissRequest = {},
            title = { Text("Abordaje confirmado") },
            text = { Text("Viaje #${r.viaje.id} · ${r.viaje.pasajeroNombre}\nEstado: En curso") },
            confirmButton = {
                TextButton(onClick = {
                    qrviewModel.limpiarResultado()
                    onVolver()
                }) { Text("Aceptar") }
            }
        )
        is ResultadoQr.Error -> AlertDialog(
            onDismissRequest = {},
            title = { Text("No se pudo confirmar") },
            text = { Text(r.mensaje) },
            confirmButton = {
                TextButton(onClick = {
                    qrviewModel.limpiarResultado()
                    escanear()
                }) { Text("Reintentar") }
            },
            dismissButton = {
                TextButton(onClick = {
                    qrviewModel.limpiarResultado()
                    onVolver()
                }) { Text("Cancelar") }
            }
        )
        null -> Unit
    }
}

