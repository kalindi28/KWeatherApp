package com.weatherapidemo.network

import com.virutsa.code.weatherapp.constants.ApiConstant
import com.virutsa.code.weatherapp.model.WeatherResponse
import com.weatherapidemo.model.City
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET(ApiConstant.WEATHER_API)
    suspend fun getWeatherData(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") appID: String,
    ): Response<WeatherResponse>

    @GET(ApiConstant.CITY_API)
    suspend fun getCityLatLong(
        @Query("q") query: String,
        @Query("limit") limit: Int,
        @Query("appid") appID: String,
    ): Response<ArrayList<City>>
}

