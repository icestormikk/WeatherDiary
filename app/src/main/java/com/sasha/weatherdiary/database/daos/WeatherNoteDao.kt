package com.sasha.weatherdiary.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sasha.weatherdiary.domain.WeatherNote

@Dao
interface WeatherNoteDao {
    @Insert
    suspend fun insert(weatherNote: WeatherNote)

    @Update
    suspend fun update(weatherNote: WeatherNote)

    @Delete
    suspend fun delete(weatherNote: WeatherNote)

    @Query("SELECT * FROM weather_notes_table ORDER BY id")
    fun getAll(): LiveData<List<WeatherNote>>
}