package com.virutsa.code.weatherapp.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.virutsa.code.weatherapp.repository.WeatherRepository
import com.virutsa.code.weatherapp.viewmodel.WeatherViewModel

class WeatherViewModelFactory(private val weatherRepository: WeatherRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WeatherViewModel(weatherRepository) as T
    }


}