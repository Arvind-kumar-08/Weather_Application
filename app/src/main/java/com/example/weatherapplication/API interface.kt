package com.example.weatherapplication

import android.R
import com.airbnb.lottie.model.content.TextRangeUnits
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface APIinterface {
    @GET("weather")
    fun getweatherdata(
        @Query("q")city: String,
        @Query("appid")appid:String,
        @Query("units")units: String
    ): Call<weatherdata>
}