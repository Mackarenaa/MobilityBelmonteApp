package com.mobilitybelmonteapp.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object Formato {

    fun hoyIso(): String = SimpleDateFormat(
        "yyyy-MM-dd", Locale.US).format(Date())

    fun ahoraIso(): String = SimpleDateFormat(
        "yyyy-MM-dd:mm",Locale.US).format(Date())

    fun millisUtcDeHoy(): Long {
        val local = Calendar.getInstance()
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(
                local.get(Calendar.YEAR),
                local.get(Calendar.MONTH),
                local.get(Calendar.DAY_OF_MONTH)
            )
        }.timeInMillis
    }

    fun isoDesdeMillisUtc(millis: Long): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US)
            .apply { timeZone = TimeZone.getTimeZone("UTC") }
            .format(Date(millis))
}