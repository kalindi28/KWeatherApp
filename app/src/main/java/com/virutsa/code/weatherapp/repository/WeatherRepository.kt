package com.virutsa.code.weatherapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.virutsa.code.weatherapp.constants.AppConstant.APP_ID
import com.virutsa.code.weatherapp.model.WeatherResponse
import com.weatherapidemo.model.City
import com.weatherapidemo.network.ApiService
import com.weatherapidemo.network.RetrofitInstance

class WeatherRepository {

    val retrofitInstance = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)

    private val cityData=MutableLiveData<ArrayList<City>>()
    private val weatherData=MutableLiveData<WeatherResponse>()

    val city:LiveData<ArrayList<City>>
    get() = cityData

    val weather:LiveData<WeatherResponse>
    get() = weatherData


    suspend fun getCityLatLong(q:String){
        val result=retrofitInstance.getCityLatLong(q,10,APP_ID)
        if(result?.body()!=null){
            cityData.postValue(result.body())

        }
    }

    suspend fun getWeatherData(lat:Double,lon:Double){
        val result=retrofitInstance.getWeatherData(lat,lon,APP_ID)
        if(result?.body()!=null){
            weatherData.postValue(result.body())
        }
    }


}