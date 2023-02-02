package com.example.weatherapplication.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapplication.MainViewModel
import com.example.weatherapplication.adapters.DayInformation
import com.example.weatherapplication.adapters.WeatherAdapter
import com.example.weatherapplication.databinding.FragmentHoursBinding
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.roundToInt


class HoursFragment : Fragment() {
    private lateinit var bindingHoursFragment: FragmentHoursBinding
    private lateinit var hoursFragmentAdapter: WeatherAdapter
    private val viewModel: MainViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingHoursFragment = FragmentHoursBinding.inflate(inflater, container, false)
        return bindingHoursFragment.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        viewModel.liveDataCurrent.observe(viewLifecycleOwner) {
            hoursFragmentAdapter.submitList(getHoursList(it))
        }
    }

    private fun initRcView() = with(bindingHoursFragment) {
        rcViewHours.layoutManager = LinearLayoutManager(activity)
        hoursFragmentAdapter = WeatherAdapter()
        rcViewHours.adapter = hoursFragmentAdapter

    }

    private fun getHoursList(currentDay: DayInformation): List<DayInformation> {
        val hoursArray = JSONArray(currentDay.hourlyWeather)
        val resultList = ArrayList<DayInformation>()
        for (i in 0 until hoursArray.length()) {
            val nextHour = DayInformation(
                "",
                (hoursArray[i] as JSONObject).getString("time").substringAfter(' '),
                (hoursArray[i] as JSONObject).getJSONObject("condition").getString("text"),
                (hoursArray[i] as JSONObject).getJSONObject("condition").getString("icon"),
                (hoursArray[i] as JSONObject).getString("temp_c").toDouble().roundToInt().toString() + "°C",
                "",
                "",
                ""
            )
            resultList += nextHour
        }
        return resultList
    }


    companion object {
        @JvmStatic
        fun newInstance() = HoursFragment()
    }
}