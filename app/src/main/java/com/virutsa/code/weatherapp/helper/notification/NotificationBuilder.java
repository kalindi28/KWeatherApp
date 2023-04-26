package com.virutsa.code.weatherapp.helper.notification;

import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.virutsa.code.weatherapp.R;
import com.virutsa.code.weatherapp.constants.Constant;

public class NotificationBuilder extends NotificationCompat.Builder {
    public NotificationBuilder(Context context){
        super(context);
       // setSmallIcon(R.drawable.ic_stat_no_complete_white_logo);
        setColor(ContextCompat.getColor(context, R.color.teal_700));
        setGroup(Constant.NOTIFICATION_KEY);
        setGroupSummary(true);
    }
}