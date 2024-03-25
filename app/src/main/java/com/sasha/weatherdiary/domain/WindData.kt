package com.sasha.weatherdiary.domain

import com.sasha.weatherdiary.R

data class WindData(
    val direction: Direction,
    val velocity: Double
)

enum class Direction(val labelId: Int) {
    NORTH(R.string.wind_direction_north),
    NORTH_EAST(R.string.wind_direction_northeast),
    EAST(R.string.wind_direction_east),
    SOUTH_EAST(R.string.wind_direction_southeast),
    SOUTH(R.string.wind_direction_south),
    SOUTH_WEST(R.string.wind_direction_southwest),
    WEST(R.string.wind_direction_west),
    NORTH_WEST(R.string.wind_direction_northwest)
}