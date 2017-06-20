package io.github.mattlavallee.budgetbeaver.data.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.github.mattlavallee.budgetbeaver.models.Reminder;

public class ReminderDispatcher extends DispatcherBase{
    private DateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    public ReminderDispatcher( SQLiteOpenHelper helper ){
        super(helper, "reminders");
        tableFields = "id integer primary key, accountId integer not null, message text not null," +
                "dayOfMonth integer not null, daysUntilExpired integer not null, " +
                "lastDateActivated text, isActiveNotification integer, active integer";
    }

    private ArrayList<Reminder> getRemindersFromQuery( String query ){
        ArrayList<Reminder> reminders = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor result = db.rawQuery( query, null );
        result.moveToFirst();

        while(result.isAfterLast() == false){
            int id = result.getInt(result.getColumnIndex("id"));
            int accountId = result.getInt(result.getColumnIndex("accountId"));
            String message = result.getString(result.getColumnIndex("message"));
            int dayOfMonth = result.getInt(result.getColumnIndex("dayOfMonth"));
            int daysUntilExpired = result.getInt(result.getColumnIndex("daysUntilExpired"));
            String lastDateActivatedStr = result.getString(result.getColumnIndex("lastDateActivated"));
            Date dateActivated;
            try{
                dateActivated= dateFormatter.parse(lastDateActivatedStr);
            } catch(ParseException ex){
                dateActivated = new Date(0);
            }
            int isActiveNotification = result.getInt(result.getColumnIndex("isActiveNotification"));
            int isActive = result.getInt(result.getColumnIndex("active"));
            reminders.add(new Reminder(id, accountId, message, dayOfMonth, daysUntilExpired,
                    dateActivated, isActiveNotification == 1 ? true : false, isActive == 1 ? true : false ));
            result.moveToNext();
        }
        result.close();
        db.close();
        return reminders;
    }

    public ArrayList<Reminder> getReminders(){
        return getRemindersFromQuery("SELECT * FROM " + TABLE_NAME + " WHERE active = 1");
    }

    public ArrayList<Reminder> getActiveNotifications(){
        return getRemindersFromQuery("SELECT * FROM " + TABLE_NAME +
                " WHERE active = 1 AND isActiveNotification = 1");
    }

    public Reminder getReminderById( int reminderId ){
        Reminder reminder = new Reminder();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME +
                " WHERE active = 1 and id = " + reminderId, null);
        result.moveToFirst();

        while(result.isAfterLast() == false){
            int id = result.getInt(result.getColumnIndex("id"));
            int accountId = result.getInt(result.getColumnIndex("accountId"));
            String message = result.getString(result.getColumnIndex("message"));
            int dayOfMonth = result.getInt(result.getColumnIndex("dayOfMonth"));
            int daysUntilExpired = result.getInt(result.getColumnIndex("daysUntilExpired"));
            String lastDateActivatedStr = result.getString(result.getColumnIndex("lastDateActivated"));
            Date dateActivated;
            try {
                dateActivated = dateFormatter.parse(lastDateActivatedStr);
            } catch (ParseException ex) {
                dateActivated = new Date(0);
            }
            int isActiveNotification = result.getInt(result.getColumnIndex("isActiveNotification"));
            int isActive = result.getInt(result.getColumnIndex("active"));
            reminder = new Reminder(id, accountId, message, dayOfMonth, daysUntilExpired,
                    dateActivated, isActiveNotification == 1 ? true : false, isActive == 1 ? true : false );
            result.moveToNext();
        }
        result.close();
        db.close();
        return reminder;
    }

    public ArrayList<Reminder> getRemindersForAccount(int accountId){
        ArrayList<Reminder> reminders = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME +
                " WHERE active = 1 AND accountId = " + accountId, null);
        result.moveToFirst();

        while(result.isAfterLast() == false){
            int id = result.getInt(result.getColumnIndex("id"));
            String message = result.getString(result.getColumnIndex("message"));
            int dayOfMonth = result.getInt(result.getColumnIndex("dayOfMonth"));
            int daysUntilExpired = result.getInt(result.getColumnIndex("daysUntilExpired"));
            String lastDateActivatedStr = result.getString(result.getColumnIndex("lastDateActivated"));
            Date dateActivated;
            try{
                dateActivated= dateFormatter.parse(lastDateActivatedStr);
            } catch(ParseException ex){
                dateActivated = new Date(0);
            }
            int isActiveNotification = result.getInt(result.getColumnIndex("isActiveNotification"));
            int isActive = result.getInt(result.getColumnIndex("active"));
            reminders.add(new Reminder(id, accountId, message, dayOfMonth, daysUntilExpired,
                    dateActivated, isActiveNotification == 1 ? true : false, isActive == 1 ? true : false ));
            result.moveToNext();
        }
        result.close();
        db.close();
        return reminders;
    }

    public long insertReminder(Reminder reminder){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put("accountId", reminder.getAccountId());
        content.put("message", reminder.getMessage());
        content.put("dayOfMonth", reminder.getDayOfMonth());
        content.put("daysUntilExpired", reminder.getDaysUntilExpiration());
        content.put("lastDateActivated", dateFormatter.format(reminder.getLastDateActivated()));
        content.put("isActiveNotification", reminder.isNotificationActive() ? 1 : 0);
        content.put("active", reminder.isActive() ? 1 : 0);

        long result = db.insert(TABLE_NAME, null, content);
        db.close();
        return result;
    }

    public long updateReminder(Reminder reminder){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put("message", reminder.getMessage());
        content.put("dayOfMonth", reminder.getDayOfMonth());
        content.put("daysUntilExpired", reminder.getDaysUntilExpiration());
        content.put("lastDateActivated", dateFormatter.format(reminder.getLastDateActivated()));
        content.put("isActiveNotification", reminder.isNotificationActive() ? 1 : 0);
        content.put("active", reminder.isActive() ? 1 : 0);

        long result = db.update(TABLE_NAME, content, "id = " + reminder.getId(), null);
        db.close();
        return result;
    }
}