package com.example.weatherapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapplication.databinding.ActivityMainBinding
import com.example.weatherapplication.fragments.MainFragment
import org.json.JSONObject




class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.constraintLayoutPlaceHolder, MainFragment.newInstance())
            .commit()
    }

//    private fun getResult(locationName: String) {
//        val queue = Volley.newRequestQueue(this)
//        val url = "https://api.weatherapi.com/v1/current.json" +
//                "?key=$API_KEY&q=$locationName&aqi=no"
//        val stringRequest = StringRequest(Request.Method.GET, url,
//            { response ->
//                val obj = JSONObject(response)
//                val temperature = obj.getJSONObject("current")
//                Log.d("MyLog", "Response: ${temperature.getString("temp_c")}")
//            },
//            {
//                Log.d("MyLog", "Volley error: $it")
//            })
//        queue.add(stringRequest)
//    }
}