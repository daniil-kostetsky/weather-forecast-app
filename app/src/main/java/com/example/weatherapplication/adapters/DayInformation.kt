package com.example.weatherapplication.adapters

data class DayInformation(
    val cityName: String,
    val fullDate: String,
    val condition: String,
    val imageUrl: String,
    val currentTemperature: String,
    val maxTemperature: String,
    val minTemperature: String,
    val hourlyWeather: String
)
