package com.example.weatherapplication.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapplication.DialogManager
import com.example.weatherapplication.MainViewModel
import com.example.weatherapplication.adapters.DayInformation
import com.example.weatherapplication.adapters.VpAdapter
import com.example.weatherapplication.databinding.FragmentMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import org.json.JSONObject
import kotlin.math.roundToInt

const val API_KEY = "0d6c6b4ff2134a3faa8123228232501"

class MainFragment : Fragment() {
    private lateinit var fLocationClient: FusedLocationProviderClient
    private val fragList = listOf(
        HoursFragment.newInstance(),
        DaysFragment.newInstance()
    )
    private lateinit var bindingMainFragment: FragmentMainBinding
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private val tList = listOf(
        "Hours",
        "Days"
    )
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingMainFragment = FragmentMainBinding.inflate(inflater, container, false)
        return bindingMainFragment.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
        initialization()
        updateCurrentCard()

    }

    override fun onResume() {
        super.onResume()
        checkLocation()
    }

    private fun initialization() = with(bindingMainFragment){
        fLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val myAdapter = VpAdapter(activity as FragmentActivity, fragList)
        viewPager.adapter = myAdapter
        TabLayoutMediator(tabLayout, viewPager) {
            tab, pos -> tab.text = tList[pos]
        }.attach()
        imButtonWeatherSync.setOnClickListener {
            tabLayout.selectTab(tabLayout.getTabAt(0))
            checkLocation()
        }

        imButtonCitySearch.setOnClickListener {
            DialogManager.searchByCityName(requireContext(), object : DialogManager.Listener {
                override fun onClick(city: String?) {
                    if (city != null) {
                        requestWeatherData(city)
                    }
                }
            })
        }
    }

    private fun getLocation() {
        val cToken = CancellationTokenSource()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cToken.token)
            .addOnCompleteListener {
                requestWeatherData("${it.result.latitude},${it.result.longitude}")
            }
    }

    private fun isLocationEnabled(): Boolean {
        val locManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun checkLocation() {
        if (isLocationEnabled()) {
            getLocation()
        } else {
            DialogManager.locationSettingsDialog(requireContext(), object : DialogManager.Listener {
                override fun onClick(city: String?) {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            })
        }
    }

    private fun permissionListener() {
        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()) {
                Toast.makeText(activity, "Permission is $it", Toast.LENGTH_LONG).show()
            }
    }

    private fun checkPermission() {
        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionListener()
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun requestWeatherData(cityName: String) {
        val url = "https://api.weatherapi.com/v1/forecast.json?" +
                "key=$API_KEY" +
                "&q=$cityName" +
                "&days=7" +
                "&aqi=no" +
                "&alerts=no"
        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(
            Request.Method.GET,
            url,
            {
                result -> parseWeatherData(result)
            },
            {
                error -> Log.d("MyLog", "This is error: $error")
            })
        queue.add(request)

    }

    private fun parseWeatherData(result: String) {
        val weatherJSONObject = JSONObject(result)
        val forecastData = parseForecastData(weatherJSONObject)
        parseCurrentData(weatherJSONObject, forecastData[0]) // current day in list[0]
    }

    private fun parseForecastData(weatherJSONObject: JSONObject): List<DayInformation> {
        val listOfForecast = ArrayList<DayInformation>()
        val forecastDaysArray = weatherJSONObject.getJSONObject("forecast").getJSONArray("forecastday")
        val cityName = weatherJSONObject.getJSONObject("location").getString("name")
        for (i in 0 until forecastDaysArray.length()) {
            val oneDay = forecastDaysArray[i] as JSONObject
            val dayData = DayInformation(
                cityName,
                oneDay.getString("date"),
                oneDay.getJSONObject("day").getJSONObject("condition").getString("text"),
                oneDay.getJSONObject("day").getJSONObject("condition").getString("icon"),
                "",
                oneDay.getJSONObject("day").getString("maxtemp_c"),
                oneDay.getJSONObject("day").getString("mintemp_c"),
                oneDay.getJSONArray("hour").toString()
            )
            listOfForecast.add(dayData)
        }
        viewModel.liveDataList.value = listOfForecast
        return listOfForecast
    }

    private fun parseCurrentData(weatherJSONObject: JSONObject, weatherItem: DayInformation) {
        val currentWeatherInfo = DayInformation(
            weatherJSONObject.getJSONObject("location").getString("name"),
            weatherJSONObject.getJSONObject("current").getString("last_updated"),
            weatherJSONObject.getJSONObject("current").getJSONObject("condition").getString("text"),
            weatherJSONObject.getJSONObject("current").getJSONObject("condition").getString("icon"),
            weatherJSONObject.getJSONObject("current").getString("temp_c"),
            weatherItem.maxTemperature,
            weatherItem.minTemperature,
            weatherItem.hourlyWeather
        )

        viewModel.liveDataCurrent.value = currentWeatherInfo
    }

    private fun updateCurrentCard() = with(bindingMainFragment){
        viewModel.liveDataCurrent.observe(viewLifecycleOwner, Observer {newValue ->
            tvCityName.text = newValue.cityName
            tvDate.text = newValue.fullDate
            val currentTemp = "${newValue.currentTemperature.toDouble().roundToInt()}°C"
            tvCurrentTemperature.text = currentTemp
            tvFeelingWeather.text = newValue.condition
            val maxMinTemperature = "${newValue.maxTemperature.toDouble().roundToInt()}°C / ${newValue.minTemperature.toDouble().roundToInt()}°C"
            tvMaxMinTemperature.text = maxMinTemperature
            Picasso.get().load("https:" + newValue.imageUrl).into(imViewWeather)
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}