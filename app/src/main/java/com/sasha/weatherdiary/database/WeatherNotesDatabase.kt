package com.sasha.weatherdiary.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sasha.weatherdiary.database.daos.WeatherNoteDao
import com.sasha.weatherdiary.domain.WeatherNote

@Database(
    entities = [WeatherNote::class],
    version = 2,
    exportSchema = false
)
abstract class WeatherNotesDatabase: RoomDatabase() {
    abstract fun getWeatherNoteDao(): WeatherNoteDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherNotesDatabase? = null

        fun getInstance(context: Context): WeatherNotesDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room
                        .databaseBuilder(
                            context.applicationContext,
                            WeatherNotesDatabase::class.java,
                            "weather_notes.db"
                        )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}