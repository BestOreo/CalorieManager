package com.example.nuc.caloriemanager.receivers;

/**
 * Created by NUC on 2017/12/1.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.example.nuc.caloriemanager.services.HardwareStepCounterService;

/**
 * @author Tobias Neidig
 * @version 20170811
 */
public class HardwareStepCountReceiver extends WakefulBroadcastReceiver {
    private static final String LOG_CLASS = HardwareStepCountReceiver.class.getName();

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.i(LOG_CLASS, "Received hardware step count alarm");
        Intent serviceIntent = new Intent(context, HardwareStepCounterService.class);
        context.startService(serviceIntent);
    }
}
