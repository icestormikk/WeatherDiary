package com.sasha.weatherdiary.instruments

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.sasha.weatherdiary.databinding.LayoutWeatherNoteCardBinding
import com.sasha.weatherdiary.domain.WeatherNote
import com.sasha.weatherdiary.ui.HomeFragmentDirections

class WeatherNotesAdapter(
    private var weatherNotes: List<WeatherNote>,
    private val context: Context
): RecyclerView.Adapter<WeatherNotesAdapter.ViewHolder>() {

    inner class ViewHolder(
        private val imageCardBinding: LayoutWeatherNoteCardBinding
    ) : RecyclerView.ViewHolder(imageCardBinding.root) {
        fun bind(note: WeatherNote, holder: WeatherNotesAdapter.ViewHolder) {
            with (imageCardBinding) {
                weatherTemperatureText.text = String.format("%f °C", note.temperature)
                weatherPressureText.text = String.format("%f mps", note.pressure)

                windDirectionText.text = context.getString(note.windData.direction.labelId)
                windVelocityText.text = String.format("%.2f mps", note.windData.velocity)

                weatherEventImage.setImageDrawable(note.weatherEventType.getEventIcon(context.resources))
                cloudsCoverTypeImage.setImageDrawable(note.cloudsCoverType.getTypeIcon(context.resources))

                weatherNoteCardLayout.setOnClickListener {
                    val action = HomeFragmentDirections.actionHomeFragmentToEditWeatherNoteFragment(note)
                    holder.itemView.findNavController().navigate(action)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutWeatherNoteCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = this.weatherNotes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = weatherNotes[position]
        holder.bind(note, holder)
    }
}