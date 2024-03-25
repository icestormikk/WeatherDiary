package com.sasha.weatherdiary.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.sasha.weatherdiary.R
import com.sasha.weatherdiary.database.viewModels.WeatherNoteViewModel
import com.sasha.weatherdiary.databinding.FragmentHomeBinding
import com.sasha.weatherdiary.databinding.FragmentStatisticsBinding
import com.sasha.weatherdiary.domain.Direction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class StatisticsFragment : Fragment() {
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private lateinit var weatherNoteViewModel: WeatherNoteViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        weatherNoteViewModel = ViewModelProvider(this)[WeatherNoteViewModel::class.java]
        val root = binding.root
        val endDate = LocalDateTime.now()
        val startDate = endDate.minusMonths(1)

        val statisticsPeriodString = binding.statisticPeriodText
        val averageTemperature = binding.averageTemperatureText
        val averagePressure = binding.averagePressureText
        val windRoseRadarChart = binding.windRoseRadarChart

        statisticsPeriodString.apply {
            text = getString(
                R.string.fragment_statistics_period_title, startDate.toFormattedString(), endDate.toFormattedString()
            )
        }
        weatherNoteViewModel.getAllWeatherNotes().observe(viewLifecycleOwner) {
            val filteredNotes = it.filter { note -> startDate <= note.timestamp && endDate >= note.timestamp }

            averageTemperature.text =
                if (filteredNotes.isNotEmpty())
                    String.format("%f °C", filteredNotes.map { note -> note.temperature }.average())
                else getString(R.string.fragment_statistics_no_data)
            averagePressure.text =
                if (filteredNotes.isNotEmpty())
                    String.format("%f mps", filteredNotes.map { note -> note.pressure }.average())
                else getString(R.string.fragment_statistics_no_data)

            val windRoseEntries = enumValues<Direction>().map {direction ->
                val currDirCount = filteredNotes.count { note -> note.windData.direction.name == direction.name }
                RadarEntry(currDirCount.toFloat(), getString(direction.labelId))
            }
            val entriesSet = RadarDataSet(windRoseEntries, getString(R.string.fragment_statistics_wind_rose_legend))
            with(entriesSet) {
                setColor(Color.rgb(121, 162, 175))
                setFillColor(Color.rgb(121, 162, 175))
                setDrawFilled(true)
                setDrawHighlightIndicators(false)
                setLineWidth(4f)
                fillAlpha = 180
                valueTextSize = 14f
                isDrawHighlightCircleEnabled = true
            }
            with (windRoseRadarChart) {
                xAxis.valueFormatter = IndexAxisValueFormatter(enumValues<Direction>().map { dir -> getString(dir.labelId) })
                data = RadarData(entriesSet)

                val desc = Description()
                desc.text = ""
                description = desc

                invalidate()
            }
        }

        return root
    }

    private fun LocalDateTime.toFormattedString(): String {
        return DateTimeFormatter.ofPattern("dd.MM.uuuu").format(this)
    }
}