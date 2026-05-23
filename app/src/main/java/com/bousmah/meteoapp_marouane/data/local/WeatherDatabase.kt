package com.bousmah.meteoapp_marouane.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bousmah.meteoapp_marouane.data.local.dao.WeatherDao
import com.bousmah.meteoapp_marouane.data.local.entity.ForecastEntity
import com.bousmah.meteoapp_marouane.data.local.entity.WeatherEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [WeatherEntity::class, ForecastEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao

    companion object {
        private const val DB_NAME = "weather_db"

        fun create(context: Context): WeatherDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                WeatherDatabase::class.java,
                DB_NAME
            )
                .addCallback(SeedCallback())
                .fallbackToDestructiveMigration()
                .build()
        }

        private class SeedCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    seedDatabase(db)
                }
            }

            private suspend fun seedDatabase(db: SupportSQLiteDatabase) {
                val now = System.currentTimeMillis()

                db.execSQL(
                    """INSERT OR REPLACE INTO weather 
                    (cityName, temperature, description, icon, humidity, windSpeed, uvIndex, feelsLike, latitude, longitude, lastUpdated)
                    VALUES ('Casablanca', 22.0, 'partiellement nuageux', '02d', 65, 4.5, 5.0, 20.0, 33.5731, -7.5898, $now)"""
                )

                val forecasts = listOf(
                    arrayOf("Casablanca", "2026-05-22", 18.0, 24.0, "02d", "partiellement nuageux", now),
                    arrayOf("Casablanca", "2026-05-23", 17.0, 23.0, "03d", "nuageux", now),
                    arrayOf("Casablanca", "2026-05-24", 19.0, 25.0, "01d", "ciel dégagé", now),
                    arrayOf("Casablanca", "2026-05-25", 18.0, 22.0, "10d", "pluie légère", now),
                    arrayOf("Casablanca", "2026-05-26", 17.0, 21.0, "04d", "couvert", now),
                    arrayOf("Casablanca", "2026-05-27", 18.0, 24.0, "02d", "partiellement nuageux", now),
                    arrayOf("Casablanca", "2026-05-28", 19.0, 26.0, "01d", "ciel dégagé", now)
                )

                for (f in forecasts) {
                    db.execSQL(
                        """INSERT OR REPLACE INTO forecast 
                        (cityName, date, minTemp, maxTemp, icon, description, lastUpdated)
                        VALUES (?, ?, ?, ?, ?, ?, ?)""",
                        f
                    )
                }
            }
        }
    }
}
