package com.sasha.weatherdiary.instruments

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeConverter {
    @TypeConverter
    fun fromLocalDateTime(source: LocalDateTime): String {
        return DateTimeFormatter.ISO_DATE_TIME.format(source)
    }

    @TypeConverter
    fun toLocalDateTime(source: String): LocalDateTime {
        return LocalDateTime.parse(source, DateTimeFormatter.ISO_DATE_TIME)
    }
}