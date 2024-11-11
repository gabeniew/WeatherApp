package com.gabrielniewielski.weatherapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gabrielniewielski.weatherapp.databinding.ListItemForecastBinding
import com.gabrielniewielski.weatherapp.ui.fragments.home.Forecast

class ForecastAdapter(private val onItemClick: (Forecast) -> Unit) : ListAdapter<Forecast, ForecastAdapter.ForecastViewHolder>(ForecastDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val binding = ListItemForecastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ForecastViewHolder(binding,onItemClick)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val forecast = getItem(position)
        holder.bind(forecast)
    }

    class ForecastViewHolder(private val binding: ListItemForecastBinding,
                             private val onItemClick: (Forecast) -> Unit) : RecyclerView.ViewHolder(binding.root) {

        fun bind(forecast: Forecast) {
            binding.forecastDate.text = forecast.date
            binding.forecastTemperature.text = "${forecast.tempMin}°C / ${forecast.tempMax}°C"
            binding.forecastCondition.text = forecast.weatherCondition

            binding.root.setOnClickListener {
                onItemClick(forecast)
            }
        }
    }

    class ForecastDiffCallback : DiffUtil.ItemCallback<Forecast>() {
        override fun areItemsTheSame(oldItem: Forecast, newItem: Forecast): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: Forecast, newItem: Forecast): Boolean {
            return oldItem == newItem
        }
    }
}