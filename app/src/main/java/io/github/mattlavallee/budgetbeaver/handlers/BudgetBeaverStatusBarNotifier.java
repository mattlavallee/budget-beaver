package io.github.mattlavallee.budgetbeaver.handlers;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import androidx.core.app.NotificationCompat;

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
            Bitmap bmp = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_budget_beaver);
            Account notificationAccount = dbDispatcher.Accounts.getAccountById( newNotification.getAccountId() );

            setNotificationProperties(notifyBuilder, bmp, newNotification, notificationAccount);

            //we want to launch BudgetBeaver on the notifications fragment when a notification is tapped
            Intent resultIntent = new Intent(activity, BudgetBeaverActivity.class);
            resultIntent.putExtra("startOnNotifications", true);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(activity, 0, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            notifyBuilder.setContentIntent(resultPendingIntent);

            NotificationManager mNotificationManager = (NotificationManager)
                    activity.getSystemService(Context.NOTIFICATION_SERVICE);

            activateNotification(mNotificationManager, notifyBuilder, newNotification);
        }
    }

    public static void generateFromService(IntentService service, ArrayList<Reminder> notifications) {
        DatabaseDispatcher dbDispatcher = new DatabaseDispatcher(service.getApplicationContext());

        for(Reminder newNotification : notifications){
            NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(service);
            Bitmap bmp = BitmapFactory.decodeResource(service.getResources(), R.mipmap.ic_budget_beaver);
            Account notificationAccount = dbDispatcher.Accounts.getAccountById( newNotification.getAccountId() );

            setNotificationProperties(notifyBuilder, bmp, newNotification, notificationAccount);

            //we want to launch BudgetBeaver on the notifications fragment when a notification is tapped
            Intent resultIntent = new Intent(service, BudgetBeaverActivity.class);
            resultIntent.putExtra("startOnNotifications", true);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(service, 0, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            notifyBuilder.setContentIntent(resultPendingIntent);

            NotificationManager mNotificationManager = (NotificationManager)
                    service.getSystemService(Context.NOTIFICATION_SERVICE);

            activateNotification(mNotificationManager, notifyBuilder, newNotification);
        }
    }

    private static void setNotificationProperties(NotificationCompat.Builder builder, Bitmap bmp,
                                                  Reminder notification, Account account) {
        builder.setSmallIcon(R.mipmap.ic_stat_check);
        builder.setLargeIcon(bmp);
        builder.setColor(Color.WHITE);
        builder.setContentTitle("Budget Beaver");
        builder.setContentText( account.getName() + ": " + notification.getMessage() );

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("Budget Beaver");
        bigTextStyle.bigText(account.getName() + ": " + notification.getMessage());
        builder.setStyle(bigTextStyle);
    }

    private static void activateNotification(NotificationManager mgr, NotificationCompat.Builder builder,
                                             Reminder notification) {
        // notificationID allows you to update the notification later on.
        String uuid = "budget beaver_" + notification.getId();
        mgr.notify(uuid.hashCode(), builder.build());
    }
}
