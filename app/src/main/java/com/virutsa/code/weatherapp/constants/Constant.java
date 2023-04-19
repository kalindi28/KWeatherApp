package com.virutsa.code.weatherapp.constants;

import android.Manifest;

public class Constant {
    public static final String BASE_URL_WEATHER_ICON="http://openweathermap.org/img/w/";
    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.
    public static final String[] permissions = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION

    };
}
