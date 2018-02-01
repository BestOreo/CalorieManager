package com.example.nuc.caloriemanager.receivers;

/**
 * Created by NUC on 2017/12/2.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.nuc.caloriemanager.StepDetectionServiceHelper;

/**
 * Receives the on shutdown broadcast and saves the counted steps.
 */

public class OnShutdownBroadcastReceiver extends BroadcastReceiver {
    private static final String LOG_CLASS = OnShutdownBroadcastReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOG_CLASS, "onReceive");
        StepDetectionServiceHelper.startPersistenceService(context);
    }
}
