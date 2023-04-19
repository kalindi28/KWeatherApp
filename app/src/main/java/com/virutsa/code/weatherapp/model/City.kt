package com.weatherapidemo.model

data class City(
    val country: String = "",
    val id: Int = 0,
    val name: String = "",
    val population: Int = 0,
    val lat:Double=0.0,
    val lon:Double=0.0,
    val sunrise: Long = 0,
    val sunset: Long = 0,
    val state:String="",
    val timezone: Int = 0
)