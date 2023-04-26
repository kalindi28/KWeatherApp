package com.virutsa.code.weatherapp.util;

import android.util.Log;

import com.virutsa.code.weatherapp.SessionConstants;

import java.util.prefs.Preferences;

public class Logger {

    public static void logException(Exception e){
        if(SessionConstants.DEBUG){
            Log.d("Exception", e.getMessage());
            return;
        }
    }

    public static void log(String tag, Exception e){
        Logger.log(Log.ERROR, tag, e.getMessage());
    }

    public static void log (String tag, String message){
        Logger.log(Log.INFO, tag, message);
    }

    public static void log(int loglevel, String tag, String message){
        if(SessionConstants.DEBUG){
            Log.println(loglevel,tag,message);
            return;
        }
    }

    public static void log(String message) {
        log(Log.INFO, "",message);
    }
}