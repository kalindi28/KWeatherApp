package com.virutsa.code.weatherapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.weatherapidemo.model.City;


public class WeatherSession {
    private static SharedPreferences mSharedPreference;
    private static SharedPreferences.Editor mShEditor;
    private static WeatherSession ourInstance = null;

    public static WeatherSession getInstance() {
        if (ourInstance == null) {
            ourInstance = new WeatherSession();
        }
        return ourInstance;
    }

    @SuppressLint("CommitPrefEdits")
    private void init(Context mContext) {
        mSharedPreference = mContext.getSharedPreferences(SessionConstants.KEY_PROJECT_NAME, Context.MODE_PRIVATE);
        mShEditor = mSharedPreference.edit();
    }

    public void setCityData(City city, Context mContext) {
        if (mSharedPreference == null) {
            init(mContext);
        }
        Gson gson = new Gson();
        String jsonStudents = gson.toJson(city);
        if (mSharedPreference == null) {
            init(mContext);
        }
        mShEditor.putString(SessionConstants.KEY_CITY_DATA,jsonStudents );
        mShEditor.commit();

    }

    public City getCityData(Context context)
    {
        if (mSharedPreference == null) {
            init(context);
        }
        Gson gson = new Gson();
        String json= mSharedPreference.getString(SessionConstants.KEY_CITY_DATA,"");
        return gson.fromJson(json, City.class);
    }
}
