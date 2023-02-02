package com.example.weatherapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.R
import com.example.weatherapplication.databinding.ListItemBinding
import com.squareup.picasso.Picasso
import kotlin.math.roundToInt


class WeatherAdapter : ListAdapter<DayInformation, WeatherAdapter.Holder>(Comparator()) {
    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val bindingHolder = ListItemBinding.bind(view)

        fun bindim(item: DayInformation) = with(bindingHolder){
            tvDateAndTime.text = item.fullDate
            tvCondition.text = item.condition
            tvTemperature.text = item.currentTemperature.ifEmpty {
                "${(item.maxTemperature).toDouble().roundToInt()}°C / ${item.minTemperature.toDouble().roundToInt()}°C" }
            Picasso.get().load("https:" + item.imageUrl).into(imageWeather)
        }
    }

    class Comparator : DiffUtil.ItemCallback<DayInformation>() {
        override fun areItemsTheSame(oldItem: DayInformation, newItem: DayInformation): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: DayInformation, newItem: DayInformation): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val myView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return Holder(myView)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindim(getItem(position))
    }

}