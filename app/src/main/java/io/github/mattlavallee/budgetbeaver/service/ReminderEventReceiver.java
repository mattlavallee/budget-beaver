package io.github.mattlavallee.budgetbeaver.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReminderEventReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        Intent eventService = new Intent(context, ReminderService.class);
        context.startService(eventService);
    }
}
