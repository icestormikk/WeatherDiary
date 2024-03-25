package com.sasha.weatherdiary.database.repositories

import androidx.lifecycle.LiveData
import com.sasha.weatherdiary.database.daos.WeatherNoteDao
import com.sasha.weatherdiary.domain.WeatherNote

class WeatherNoteRepository(
    private val weatherNoteDao: WeatherNoteDao
) {
    fun getAllWeatherNotes(): LiveData<List<WeatherNote>> {
        return weatherNoteDao.getAll()
    }

    suspend fun addWeatherNote(weatherNote: WeatherNote) {
        return weatherNoteDao.insert(weatherNote)
    }

    suspend fun updateWeatherNote(weatherNote: WeatherNote) {
        return weatherNoteDao.update(weatherNote)
    }

    suspend fun deleteWeatherNote(weatherNote: WeatherNote) {
        return weatherNoteDao.delete(weatherNote)
    }
}