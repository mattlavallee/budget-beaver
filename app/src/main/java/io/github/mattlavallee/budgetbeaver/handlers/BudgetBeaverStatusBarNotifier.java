package io.github.mattlavallee.budgetbeaver.handlers;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;

import io.github.mattlavallee.budgetbeaver.BudgetBeaverActivity;
import io.github.mattlavallee.budgetbeaver.R;
import io.github.mattlavallee.budgetbeaver.data.DatabaseDispatcher;
import io.github.mattlavallee.budgetbeaver.models.Account;
import io.github.mattlavallee.budgetbeaver.models.Reminder;

public class BudgetBeaverStatusBarNotifier {
    public static void generate( Activity activity, ArrayList<Reminder> notifications ){
        DatabaseDispatcher dbDispatcher = new DatabaseDispatcher(activity.getApplicationContext());

        for(Reminder newNotification : notifications){
            NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(activity);
            notifyBuilder.setSmallIcon(R.mipmap.ic_stat_check);
            Bitmap bmp = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_budget_beaver);
            notifyBuilder.setLargeIcon(bmp);
            notifyBuilder.setColor(Color.WHITE);
            notifyBuilder.setContentTitle("Budget Beaver");
            Account notificationAccount = dbDispatcher.Accounts.getAccountById( newNotification.getAccountId() );
            notifyBuilder.setContentText( notificationAccount.getName() + ": " + newNotification.getMessage() );

            //we want to launch BudgetBeaver on the notifications fragment when a notification is tapped
            Intent resultIntent = new Intent(activity, BudgetBeaverActivity.class);
            resultIntent.putExtra("startOnNotifications", true);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(activity, 0, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            notifyBuilder.setContentIntent(resultPendingIntent);

            NotificationManager mNotificationManager = (NotificationManager)
                    activity.getSystemService(Context.NOTIFICATION_SERVICE);

            // notificationID allows you to update the notification later on.
            String uuid = "budget beaver_" + newNotification.getId();
            mNotificationManager.notify(uuid.hashCode(), notifyBuilder.build());
        }
    }
}
