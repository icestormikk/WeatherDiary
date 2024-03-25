package com.sasha.weatherdiary.database.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.sasha.weatherdiary.database.WeatherNotesDatabase
import com.sasha.weatherdiary.database.repositories.WeatherNoteRepository
import com.sasha.weatherdiary.domain.WeatherNote
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async

class WeatherNoteViewModel(application: Application): AndroidViewModel(application) {
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val repository: WeatherNoteRepository

    init {
        val weatherNoteDao = WeatherNotesDatabase
            .getInstance(application)
            .getWeatherNoteDao()
        repository = WeatherNoteRepository(weatherNoteDao)
    }

    fun getAllWeatherNotes(): LiveData<List<WeatherNote>> {
        return repository.getAllWeatherNotes()
    }

    fun insertWeatherNote(weatherNote: WeatherNote): Deferred<Unit> {
        return uiScope.async {
            repository.addWeatherNote(weatherNote)
        }
    }

    fun updateWeatherNote(weatherNote: WeatherNote): Deferred<Unit> {
        return uiScope.async {
            repository.updateWeatherNote(weatherNote)
        }
    }

    fun deleteWeatherNote(weatherNote: WeatherNote): Deferred<Unit> {
        return uiScope.async {
            repository.deleteWeatherNote(weatherNote)
        }
    }
}