package com.example.nuc.caloriemanager.receivers;

/**
 * Created by NUC on 2017/12/1.
 */

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.example.nuc.caloriemanager.Factory;
import com.example.nuc.caloriemanager.StepDetectionServiceHelper;
import com.example.nuc.caloriemanager.models.WalkingMode;
import com.example.nuc.caloriemanager.persistence.StepCountPersistenceHelper;
import com.example.nuc.caloriemanager.persistence.WalkingModePersistenceHelper;

/**
 * Stores the current step count in database.
 *
 * @author Tobias Neidig
 * @version 20160720
 */

public class StepCountPersistenceReceiver extends WakefulBroadcastReceiver {
    private static final String LOG_CLASS = StepCountPersistenceReceiver.class.getName();
    private WalkingMode oldWalkingMode;
    /**
     * The application context
     */
    private Context context;
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            StepCountPersistenceHelper.storeStepCounts(service, context, oldWalkingMode);
            context.getApplicationContext().unbindService(mServiceConnection);
            StepDetectionServiceHelper.stopAllIfNotRequired(false, context);
          //  WidgetReceiver.forceWidgetUpdate(context);
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOG_CLASS, "Storing the steps");
        this.context = context.getApplicationContext();
        if (intent.hasExtra(WalkingModePersistenceHelper.BROADCAST_EXTRA_OLD_WALKING_MODE)) {
            oldWalkingMode = WalkingModePersistenceHelper.getItem(intent.getLongExtra(WalkingModePersistenceHelper.BROADCAST_EXTRA_OLD_WALKING_MODE, (long) -1), context);
        }
        if(oldWalkingMode == null){
            oldWalkingMode = WalkingModePersistenceHelper.getActiveMode(context);
        }
        // bind to service
        Intent serviceIntent = new Intent(context, Factory.getStepDetectorServiceClass(context.getPackageManager()));
        context.getApplicationContext().bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

    }
}
