package com.mobilitybelmonteapp.util

data class Lugar(
    val nombre: String,
    val lat: Double,
    val Ing: Double
)

object Lugares {
    val aeropuerto = Lugar("Aeropuerto SCL", -33.3930, -70.7858)
    val lasCondes = Lugar("Las Condes, Av. Apoquindo", -33.4089, -70.6110)
    val providencia = Lugar("Providencia, Av. Providencia", -33.4263, -70.6110)
    val vitacura = Lugar("Vitacura, Av. Vitacura", -33.3900, -70.5760)
    val centro = Lugar("Santiago Centro, Plaza de Armas", -33.4372, -70.6506)
    val nunoa = Lugar("Ñuñoa, Plaza Ñuñoa", -33.4569, -70.5978)
    val estacionCentral = Lugar("Estación Central", -33.4527, -70.6783)
    val huechuraba = Lugar("Huechuraba, Ciudad Empresarial", -33.3700, -70.6350)
    val elGolf = Lugar("Barrio El Golf, Las Condes", -33.4153, -70.5933)
    val costanera = Lugar("Costanera Center, Providencia", -33.4178, -70.6063)

    val todos = listOf(
        aeropuerto, lasCondes, providencia, vitacura, centro, nunoa,
        estacionCentral, huechuraba, elGolf, costanera
    )
}