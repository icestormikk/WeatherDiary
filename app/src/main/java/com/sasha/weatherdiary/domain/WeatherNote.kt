package com.sasha.weatherdiary.domain

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.sasha.weatherdiary.instruments.LocalDateTimeConverter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.time.LocalDateTime

@Parcelize
@TypeConverters(LocalDateTimeConverter::class)
@Entity(tableName = "weather_notes_table")
data class WeatherNote(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "timestamp")
    val timestamp: LocalDateTime,

    @ColumnInfo(name = "temperature")
    val temperature: Double,

    @ColumnInfo(name = "pressure")
    val pressure: Double,

    @ColumnInfo(name = "clouds_cover_type")
    val cloudsCoverType: CloudsCoverType,

    @ColumnInfo(name = "weather_event_type")
    val weatherEventType: WeatherEvent,

    @Embedded
    val windData: @RawValue WindData
) : Parcelable

