package io.github.mattlavallee.budgetbeaver.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

import io.github.mattlavallee.budgetbeaver.data.DatabaseDispatcher;
import io.github.mattlavallee.budgetbeaver.handlers.BudgetBeaverStatusBarNotifier;
import io.github.mattlavallee.budgetbeaver.models.Reminder;

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

    private ArrayList<Reminder> invalidateOldAndActivateNewNotifications(DatabaseDispatcher dbDispatcher){
        ArrayList<Reminder> allReminders = dbDispatcher.Reminders.getReminders();

        ArrayList<Reminder> expiredReminders = Reminder.invalidateExpiredNotifications( allReminders );
        ArrayList<Reminder> newActiveReminders = Reminder.activateNewNotifications( allReminders );

        for(Reminder expReminder : expiredReminders ){
            dbDispatcher.Reminders.updateReminder(expReminder);
        }
        for(Reminder actReminder : newActiveReminders ){
            dbDispatcher.Reminders.updateReminder(actReminder);
        }

        return newActiveReminders;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("TEST", "do all the things!");
        DatabaseDispatcher dbDispatcher = new DatabaseDispatcher(getApplicationContext());
        ArrayList<Reminder> newNotifications = invalidateOldAndActivateNewNotifications(dbDispatcher);
        BudgetBeaverStatusBarNotifier.generateFromService(this, newNotifications);

        this.stopSelf();
    }
}
