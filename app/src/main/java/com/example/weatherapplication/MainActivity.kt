package com.example.weatherapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapplication.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fetchweatherdata("delhi")
        SearchCity()
    }

    private fun SearchCity() {
        var searchView=binding.searchView
        searchView.setOnQueryTextListener(object :android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query!=null){
               fetchweatherdata(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    fun fetchweatherdata(cityName:String) {
        var retrofit= Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(APIinterface::class.java)
        var response=retrofit.getweatherdata(cityName,"74368f034013bae64b5552821bf8f10a","metric")
        response.enqueue(object: Callback<weatherdata>{
            override fun onResponse(call: Call<weatherdata>, response: Response<weatherdata>) {
                val responseBody=response.body()
                if(response.isSuccessful && responseBody!=null){
                    var temperature = responseBody.main.temp.toString()
                    binding.temprature.text="$temperature °C"
                    var humidity=responseBody.main.humidity.toString()
                    binding.humidity.text="$humidity %"
                    var wind=responseBody.wind.speed.toString()
                    binding.wind.text="$wind m/s"
                    var sunrise=responseBody.sys.sunrise.toString().toLong()
                    binding.sunrise.text="${time(sunrise)}"
                    var sunset=responseBody.sys.sunset.toString().toLong()
                    binding.sunset.text="${time(sunset)}"
                    var sea=responseBody.main.pressure.toString()
                    binding.sealevel.text="$sea hpa"
                    var condition=responseBody.weather.firstOrNull()?.main?:"unknown"
                    binding.weather.text=condition
                    var maxtemp=responseBody.main.temp_max.toString()
                    binding.maxtemp.text=" max temp: $maxtemp °C"
                    var mintemp=responseBody.main.temp_min.toString()
                    binding.mintemp.text="min temp: $mintemp °C"
                    binding.condition.text=condition
                    binding.day.text=dayname(System.currentTimeMillis())
                    binding.date.text=date()
                        binding.cityname.text="$cityName"
                    changeiamgeaccordingtoweather(condition)
                }
            }



            override fun onFailure(call: Call<weatherdata?>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })

    }
    private fun changeiamgeaccordingtoweather(conditions:String){
        when(conditions.lowercase()){
            "haze","clouds","overcast","mist","foggy"->{

              binding.root.setBackgroundResource(R.drawable.colud_background)
              binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "clear sky","sunny","clear"->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "light rain","drizzle","rain","moderate rain","showers","heavy rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "light snow","moderate snow","heavy snow","snow","blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }else->{
            binding.root.setBackgroundResource(R.drawable.sunny_background)
            binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

        }
        binding.lottieAnimationView.playAnimation()

    }

    private fun date(): String{
        var sdf= SimpleDateFormat("dd MMM yyyy",Locale.getDefault())
        return sdf.format((Date()))
    }
    fun dayname(timestamp:Long):String{
        var sdf= SimpleDateFormat("EEEE",Locale.getDefault())
        return sdf.format((Date()))
    }
     private fun time(timestamp:Long):String{
        var sdf= SimpleDateFormat("HH:mm",Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }
}