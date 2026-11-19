package com.mobilitybelmonteapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database (
    entities = [UsuarioLocal::class, ViajeLocal::class, HistorialLocal::class],
    version = 1,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase (){
    abstract fun usuarioDao() : UsuarioDao
    abstract fun viajeDao() : ViajeDao
    abstract fun historialDao() : HistorialDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun get (context: Context) : AppDatabase = instancia ?: synchronized(this) {
            instancia ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java,"belmonte.db"
            ).build().also { instancia = it }
        }
    }
}