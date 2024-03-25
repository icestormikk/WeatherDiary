package com.sasha.weatherdiary.domain

import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import com.sasha.weatherdiary.R

enum class CloudsCoverType(val iconId: Int, val labelId: Int) {
    CLEAR(R.drawable.clouds_none, R.string.clouds_cover_type_clear),
    LOW_CLOUDS(R.drawable.clouds_low, R.string.clouds_cover_type_low),
    MEDIUM_CLOUDS(R.drawable.clouds_medium, R.string.clouds_cover_type_medium),
    MANY_CLOUDS(R.drawable.clouds_many, R.string.clouds_cover_type_many);

    fun getTypeIcon(resources: Resources): Drawable? {
        return ResourcesCompat.getDrawable(resources, iconId, null)
    }
}