package com.sasha.weatherdiary.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sasha.weatherdiary.R
import com.sasha.weatherdiary.database.viewModels.WeatherNoteViewModel
import com.sasha.weatherdiary.databinding.FragmentHomeBinding
import com.sasha.weatherdiary.domain.CloudsCoverType
import com.sasha.weatherdiary.domain.WeatherEvent
import com.sasha.weatherdiary.domain.WeatherNote
import com.sasha.weatherdiary.instruments.WeatherNotesAdapter
import java.time.LocalDateTime

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var weatherNoteViewModel: WeatherNoteViewModel

    private val leftBorderDateFilter = MutableLiveData(LocalDateTime.MIN)
    private val rightBorderDateFilter = MutableLiveData(LocalDateTime.MAX)
    private val filteredWeatherNotes = MutableLiveData(mutableListOf<WeatherNote>())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        weatherNoteViewModel = ViewModelProvider(this)[WeatherNoteViewModel::class.java]
        val root = binding.root
        val cloudsCoverTypeAdapter = ArrayAdapter(
            root.context,
            androidx.transition.R.layout.support_simple_spinner_dropdown_item,
            enumValues<CloudsCoverType>().map { getString(it.labelId) }.plus(getString(R.string.fragment_home_list_none_variant))
        )
        val weatherEventTypeAdapter = ArrayAdapter(
            root.context,
            androidx.transition.R.layout.support_simple_spinner_dropdown_item,
            enumValues<WeatherEvent>().map { getString(it.labelId) }.plus(getString(R.string.fragment_home_list_none_variant))
        )

        val filterLayout = binding.filterLayout
        val toggleFilterLayoutButton = binding.toggleFilterLayoutButton
        val lowerBorderDateCalendarView = binding.lowerDateBorder
        val highBorderDateCalendarView = binding.highDateBorder
        val cloudsCoverTypeSpinner = binding.cloudCoverTypeSpinner
        val weatherEventTypeSpinner = binding.weatherEventTypeSpinner
        val applyFilterButton = binding.applyFilterButton
        val clearFilterButton = binding.clearFilterButton
        val weatherNotesList = binding.weatherNotesList
        val collectStatisticsButton = binding.collectStatisticsButton

        filterLayout.visibility = View.GONE
        toggleFilterLayoutButton.setOnClickListener {
            with (filterLayout.visibility) {
                filterLayout.visibility = if (this == View.GONE) View.VISIBLE else View.GONE
            }
        }
        lowerBorderDateCalendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            leftBorderDateFilter.value = LocalDateTime.of(year, month + 1, dayOfMonth, 0, 0)
        }
        highBorderDateCalendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            rightBorderDateFilter.value = LocalDateTime.of(year, month + 1, dayOfMonth, 0, 0)
        }
        cloudsCoverTypeSpinner.adapter = cloudsCoverTypeAdapter
        weatherEventTypeSpinner.adapter = weatherEventTypeAdapter
        applyFilterButton.setOnClickListener {
            if (leftBorderDateFilter.value!! > rightBorderDateFilter.value!!) {
                showToastMessage(root.context, getString(R.string.fragment_home_date_interval_error))
                return@setOnClickListener
            }

            weatherNoteViewModel.getAllWeatherNotes().observe(viewLifecycleOwner) {
                var result = it.filter {
                    note -> leftBorderDateFilter.value!! < note.timestamp && rightBorderDateFilter.value!! > note.timestamp
                }

                with (cloudsCoverTypeSpinner.selectedItem.toString()) {
                    if (this != getString(R.string.fragment_home_list_none_variant)) {
                        result = result.filter { note -> getString(note.cloudsCoverType.labelId) == this }
                    }
                }
                with (weatherEventTypeSpinner.selectedItem.toString()) {
                    if (this != getString(R.string.fragment_home_list_none_variant)) {
                        result = result.filter { note -> getString(note.weatherEventType.labelId) == this }
                    }
                }
                filteredWeatherNotes.value = result.toMutableList()
            }
        }
        clearFilterButton.setOnClickListener {
            weatherNoteViewModel.getAllWeatherNotes().observe(viewLifecycleOwner) {
                filteredWeatherNotes.value = it.toMutableList()
            }
        }
        collectStatisticsButton.setOnClickListener {
            root.findNavController().navigate(R.id.action_home_fragment_to_statisticsFragment)
        }

        weatherNoteViewModel.getAllWeatherNotes().observe(viewLifecycleOwner) {
            weatherNotesList.adapter = WeatherNotesAdapter(it, root.context)
            weatherNotesList.layoutManager = LinearLayoutManager(root.context)
        }

        filteredWeatherNotes.observe(viewLifecycleOwner) {
            weatherNotesList.adapter = WeatherNotesAdapter(it, root.context)
        }

        return root
    }

    private fun showToastMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}