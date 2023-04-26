package com.virutsa.code.weatherapp;

import android.app.Application;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class AppController extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
