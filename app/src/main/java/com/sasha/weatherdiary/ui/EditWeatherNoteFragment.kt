package com.sasha.weatherdiary.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.sasha.weatherdiary.R
import com.sasha.weatherdiary.database.viewModels.WeatherNoteViewModel
import com.sasha.weatherdiary.databinding.FragmentEditWeatherNoteBinding
import com.sasha.weatherdiary.domain.CloudsCoverType
import com.sasha.weatherdiary.domain.Direction
import com.sasha.weatherdiary.domain.WeatherEvent
import com.sasha.weatherdiary.domain.WeatherNote
import com.sasha.weatherdiary.domain.WindData
import com.sasha.weatherdiary.instruments.TextEditValidator
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.TimeZone

class EditWeatherNoteFragment : Fragment() {
    private var _binding: FragmentEditWeatherNoteBinding? = null
    private val binding get() = _binding!!
    private val arguments by navArgs<EditWeatherNoteFragmentArgs>()
    private lateinit var weatherNoteViewModel: WeatherNoteViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditWeatherNoteBinding.inflate(inflater, container, false)
        weatherNoteViewModel = ViewModelProvider(this)[WeatherNoteViewModel::class.java]
        val root = binding.root
        val editableNote = arguments.editableWeatherNote
        val selectedWeatherNoteDate = MutableLiveData((editableNote?.timestamp ?: LocalDateTime.now()).getTime())

        val cloudsCoverTypeAdapter = ArrayAdapter(
            root.context,
            androidx.transition.R.layout.support_simple_spinner_dropdown_item,
            enumValues<CloudsCoverType>().map { getString(it.labelId) }
        )
        val weatherEventAdapter = ArrayAdapter(
            root.context,
            androidx.transition.R.layout.support_simple_spinner_dropdown_item,
            enumValues<WeatherEvent>().map { getString(it.labelId) }
        )
        val windDirectionAdapter = ArrayAdapter(
            root.context,
            androidx.transition.R.layout.support_simple_spinner_dropdown_item,
            enumValues<Direction>().map { getString(it.labelId) }
        )

        val timestampCalendarView = binding.timestampCalendarView
        val temperatureEditText = binding.temperatureField
        val pressureEditText = binding.pressureField
        val cloudsCoverTypeSpinner = binding.cloudCoverTypesSpinner
        val weatherEventSpinner = binding.weatherEventSpinner
        val windDirectionSpinner = binding.windDirectionSpinner
        val windVelocityEditText = binding.windVelocityField
        val editWeatherNoteButton = binding.editWeatherNoteButton
        val deleteWeatherNoteButton = binding.deleteWeatherNoteButton
        val deleteWeatherNoteLayout = binding.deleteWeatherNoteLayout

        timestampCalendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedWeatherNoteDate.value = LocalDateTime.of(year, month + 1, dayOfMonth, 0, 0).getTime()
        }
        arrayListOf(pressureEditText, windVelocityEditText).forEach {
            it.addTextChangedListener(object : TextEditValidator(it) {
                override fun validate(textView: TextView?, text: String?) {
                    if (textView == null || text == null) {
                        return
                    }

                    if (text.isEmpty() || text.toDouble() < 0) {
                        textView.error = getString(R.string.fragment_edit_weather_note_non_negative_error)
                    }
                }
            })
        }
        cloudsCoverTypeSpinner.adapter = cloudsCoverTypeAdapter
        weatherEventSpinner.adapter = weatherEventAdapter
        windDirectionSpinner.adapter = windDirectionAdapter

        editWeatherNoteButton.text = if (editableNote == null) {
            getString(R.string.fragment_edit_weather_note_create_note)
        } else {
            getString(R.string.fragment_edit_weather_note_update_note)
        }
        editWeatherNoteButton.setOnClickListener {
            if (
                (temperatureEditText.error != null || temperatureEditText.text.isEmpty()) ||
                (pressureEditText.error != null || pressureEditText.text.isEmpty()) ||
                (windVelocityEditText.error != null || windVelocityEditText.text.isEmpty())
            ) {
                showToastMessage(root.context, getString(R.string.fragment_edit_weather_note_non_negative_error))
                return@setOnClickListener
            }

            val cloudsCoverType = enumValues<CloudsCoverType>().find {
                getString(it.labelId) == cloudsCoverTypeSpinner.selectedItem.toString()
            }
            val weatherEvent = enumValues<WeatherEvent>().find {
                getString(it.labelId) == weatherEventSpinner.selectedItem.toString()
            }
            val windDirection = enumValues<Direction>().find {
                getString(it.labelId) == windDirectionSpinner.selectedItem.toString()
            }

            if (cloudsCoverType == null || weatherEvent == null || windDirection == null) {
                showToastMessage(root.context, getString(R.string.fragment_edit_weather_note_unknown_type_error))
                return@setOnClickListener
            }

            try {
                lifecycleScope.launch {
                    val weatherNote = WeatherNote(
                        timestamp = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(selectedWeatherNoteDate.value!!), TimeZone.getDefault().toZoneId()
                        ),
                        temperature = temperatureEditText.text.toString().toDouble(),
                        pressure = pressureEditText.text.toString().toDouble(),
                        cloudsCoverType = cloudsCoverType,
                        weatherEventType = weatherEvent,
                        windData = WindData(windDirection, windVelocityEditText.text.toString().toDouble())
                    )

                    runBlocking {
                        if (editableNote == null) {
                            weatherNoteViewModel.insertWeatherNote(weatherNote)
                        } else {
                            weatherNoteViewModel.updateWeatherNote(weatherNote.copy(id = editableNote.id))
                        }
                    }
                    root.findNavController().navigate(R.id.home_fragment)
                }
            } catch (ex: Error) {
                showToastMessage(root.context, ex.message ?: "Oops, something went wrong..")
                return@setOnClickListener
            }
        }
        deleteWeatherNoteButton.setOnClickListener {
            lifecycleScope.launch {
                runBlocking {
                    if (editableNote != null) { weatherNoteViewModel.deleteWeatherNote(editableNote) }
                }
                root.findNavController().navigate(R.id.home_fragment)
            }
        }

        with (arguments.editableWeatherNote) {
            deleteWeatherNoteLayout.visibility = if (this == null) View.GONE else View.VISIBLE
            if (this != null) {
                temperatureEditText.text = temperature.toString().toEditable()
                pressureEditText.text = pressure.toString().toEditable()
                cloudsCoverTypeSpinner.setSelectedItem(cloudsCoverTypeAdapter, getString(cloudsCoverType.labelId))
                weatherEventSpinner.setSelectedItem(weatherEventAdapter, getString(weatherEventType.labelId))
                windDirectionSpinner.setSelectedItem(windDirectionAdapter, getString(windData.direction.labelId))
                windVelocityEditText.text = windData.velocity.toString().toEditable()
            }
        }

        return root
    }

    private fun showToastMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun Spinner.setSelectedItem(adapter: ArrayAdapter<String>, label: String) {
        val position = adapter.getPosition(label)
        this.setSelection(position)
    }

    private fun LocalDateTime.getTime(): Long {
        return this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    private fun String.toEditable(): Editable {
        return Editable.Factory.getInstance().newEditable(this)
    }
}