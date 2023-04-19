package com.virutsa.code.weatherapp.model;

import com.google.gson.annotations.SerializedName;
import com.weatherapidemo.model.Temprature;

import java.io.Serializable;
import java.util.ArrayList;

public class WeatherResponse implements Serializable {


    public ArrayList<Weather> getWeathers() {
        return weathers;
    }

    public void setWeathers(ArrayList<Weather> weathers) {
        this.weathers = weathers;
    }

    public Temprature getTemprature() {
        return temprature;
    }

    public void setTemprature(Temprature temprature) {
        this.temprature = temprature;
    }

    @SerializedName("weather")
  private   ArrayList<Weather> weathers;

    @SerializedName("main")
  private   Temprature temprature;
}
