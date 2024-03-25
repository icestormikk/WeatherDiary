package com.sasha.weatherdiary.domain

import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import com.sasha.weatherdiary.R

enum class WeatherEvent(val iconId: Int, val labelId: Int) {
    RAIN(R.drawable.weather_type_rain, R.string.weather_event_rain),
    SNOW(R.drawable.weather_type_snow,  R.string.weather_event_snow),
    THUNDERSTORM(R.drawable.weather_type_thunderstorm, R.string.weather_event_thunderstorm);

    fun getEventIcon(resources: Resources): Drawable? {
        return ResourcesCompat.getDrawable(resources, iconId, null)
    }
}