package com.mobilitybelmonteapp.navegation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mobilitybelmonteapp.data.remote.Rol
import com.mobilitybelmonteapp.ui.admin.PendientesScreen
import com.mobilitybelmonteapp.ui.conductor.ViajesDelDiaScreen
import com.mobilitybelmonteapp.ui.pasajero.DetalleViajePasajeroScreen
import com.mobilitybelmonteapp.ui.pasajero.MisViajesScreen
import com.mobilitybelmonteapp.ui.pasajero.NuevaSolicitudScreen
import androidx.navigation.compose.composable
import com.mobilitybelmonteapp.ui.conductor.DetalleViajeConductorScreen
import com.mobilitybelmonteapp.ui.conductor.EscanerQrScreen
import com.mobilitybelmonteapp.ui.login.LoginScreen
import androidx.navigation.compose.NavHost
import com.mobilitybelmonteapp.data.SessionManager


@Composable
fun AppNavegation () {
    val nav = rememberNavController()

    fun cerrarSesion() {
        SessionManager.cerrar()
        nav.navigate(Routes.LOGIN) {
            popUpTo(nav.graph.id) { inclusive = true }
        }
    }

    NavHost(navController = nav, startDestination = Routes.LOGIN) {

        composable(Routes.LOGIN) {
            LoginScreen  (onLoginExitoso = { rol ->
                val destino = when (rol) {
                Rol.PASAJERO -> Routes.PASAJERO_VIAJES
                Rol.CONDUCTOR -> Routes.CONDUCTOR_VIAJES
                Rol.ADMIN -> Routes.ADMIN_PENDIENTES
            }
                nav.navigate(destino){ popUpTo(Routes.LOGIN) { inclusive = true } }
            })
        }
        composable(Routes.PASAJERO_VIAJES) {
            MisViajesScreen(
                onNuevaSolicitus = { nav.navigate(Routes.PASAJERO_NUEVA) },
                onVerDetalle = {id -> nav.navigate(Routes.pasajeroDetalle(id)) },
                onCerrarSesion = { cerrarSesion() }
            )
        }
        composable(Routes.PASAJERO_NUEVA) {
            NuevaSolicitudScreen(
                onVolver = {nav.popBackStack() },
                onCreada = { nav.popBackStack() }
            )
        }
        composable( route = Routes.PASAJERO_DETALLE, arguments = listOf(navArgument("viajeId") {
            type = NavType.LongType})
        ){ entry -> DetalleViajePasajeroScreen(
            viajeId = entry.arguments?.getLong("viajeId") ?: 0L,
            onVolver = { nav.popBackStack() }
        )
        }

        composable(Routes.CONDUCTOR_VIAJES){
            ViajesDelDiaScreen (
                onVerDetalle = { id -> nav.navigate(Routes.conductorDetalle(id)) },
                onCerrarSesion = { cerrarSesion()}
            )
        }
        composable( route = Routes.CONDUCTOR_DETALLE, arguments = listOf(navArgument("viajeId") {
            type = NavType.LongType})
        ){ entry -> DetalleViajeConductorScreen (
            viajeId = entry.arguments?.getLong ("viajeId") ?: 0L,
            onVolver = { nav.popBackStack() },
            onEscanear = { nav.navigate(Routes.CONDUCTOR_ESCANER) }
        )}
        composable (Routes.CONDUCTOR_ESCANER) {
            EscanerQrScreen (onVolver = { nav.popBackStack() })
        }

        composable(Routes.ADMIN_PENDIENTES) {
            PendientesScreen (onCerrarSesion = { cerrarSesion() })
        }
    }
}