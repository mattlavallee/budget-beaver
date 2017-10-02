package io.github.mattlavallee.budgetbeaver.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class ReminderSetupReceiver extends BroadcastReceiver {
    //run the service intent once an hour
    private static final int FREQUENCY = 60 * 60 * 1000;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent eventReceiverIntent = new Intent(context, ReminderEventReceiver.class);

        PendingIntent intentExecuted = PendingIntent.getBroadcast(context, 0, eventReceiverIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar now = Calendar.getInstance();
        now.add(Calendar.SECOND, 60);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, now.getTimeInMillis(), FREQUENCY, intentExecuted);
    }
}
