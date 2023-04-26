package com.virutsa.code.weatherapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virutsa.code.weatherapp.model.WeatherResponse
import com.virutsa.code.weatherapp.repository.WeatherRepository
import com.weatherapidemo.model.City
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(private val weatherRepository: WeatherRepository) : ViewModel() {
    //get latlong of city from search query
    fun getCityLatLongBySearch(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.getCityLatLong(query)
        }
    }
    //get weather of city from lat long
    fun getWeatherData(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.getWeatherData(lat, lon)
        }
    }

    val cities: LiveData<ArrayList<City>>
        get() = weatherRepository.city

    val weatherResponse: LiveData<WeatherResponse>
        get() = weatherRepository.weather

    fun getCityDataObserver(): LiveData<ArrayList<City>> {
        return cities
    }

    fun getWeatherDataObserver(): LiveData<WeatherResponse> {
        return weatherResponse
    }


}