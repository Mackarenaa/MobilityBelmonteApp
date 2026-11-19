package com.mobilitybelmonteapp.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object NavegationExterna {

    fun abrirGoogleMaps(context: Context, lat: Double, lng: Double) {
        val navegation = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("google.navegation:q=$lat,$lng&mode=d")
        ).setPackage("com.google.android.apps.maps")

        val generico = Intent(Intent.ACTION_VIEW,
            Uri.parse("geo:$lat,$lng?q=$lat,$lng"))

        if (!lanzar(context, navegation) && !lanzar (context, generico)) {
            avisar(context, "No hay una app de mapas instalada")
        }
    }

    fun abrirWaze ( context: Context, lat: Double, lng: Double) {
        val waze = Intent(Intent.ACTION_VIEW, Uri.parse("waze://?ll=$lat,$lng&navigate=yes"))
        if (!lanzar(context, waze)) {
            avisar(context, "Waze no esta instalado en este dispositivo")
        }
    }

    private fun lanzar (context: Context, intent: Intent) : Boolean = try{
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        true
    } catch (e: ActivityNotFoundException) {
        false
    }

    private fun avisar (context: Context, mensaje: String) {
        Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
    }
}