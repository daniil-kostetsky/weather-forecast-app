package com.example.weatherapplication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapplication.adapters.DayInformation

class MainViewModel : ViewModel() {
    val liveDataCurrent = MutableLiveData<DayInformation>()
    val liveDataList = MutableLiveData<List<DayInformation>>()
}