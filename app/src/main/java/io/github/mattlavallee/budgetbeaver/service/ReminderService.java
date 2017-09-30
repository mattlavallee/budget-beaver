package io.github.mattlavallee.budgetbeaver.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ReminderService extends IntentService {
    public ReminderService() {
        super("ReminderService");
    }

    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        super.onStartCommand(intent, startId, startId);

        return Service.START_NOT_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("TEST", "do all the things!");
        this.stopSelf();
    }
}
