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

public class ReminderDispatcher {
    private SQLiteOpenHelper dbHelper;
    private static final String TABLE_NAME = "reminders";
    private DateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");

    public ReminderDispatcher( SQLiteOpenHelper helper ){
        dbHelper = helper;
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
        "id integer primary key, accountId integer not null, message text not null," +
        "dayOfMonth integer not null, daysUntilExpired integer not null, " +
        "lastDateActivated text, isActiveNotification integer, active integer)");
    }

    public ArrayList<Reminder> getReminders(){
        ArrayList<Reminder> reminders = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME +
            "WHERE active = 1", null);
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
                dateActivated = new Date();
            }
            int isActiveNotification = result.getInt(result.getColumnIndex("isActiveNotification"));
            int isActive = result.getInt(result.getColumnIndex("active"));
            reminders.add(new Reminder(id, accountId, message, dayOfMonth, daysUntilExpired,
                    dateActivated, isActiveNotification == 1 ? true : false, isActive == 1 ? true : false ));
        }
        result.close();
        db.close();
        return reminders;
    }

    public Reminder getReminderById( int reminderId ){
        Reminder reminder = new Reminder();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME +
                "WHERE active = 1 and id = " + reminderId, null);
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
                dateActivated = new Date();
            }
            int isActiveNotification = result.getInt(result.getColumnIndex("isActiveNotification"));
            int isActive = result.getInt(result.getColumnIndex("active"));
            reminder = new Reminder(id, accountId, message, dayOfMonth, daysUntilExpired,
                    dateActivated, isActiveNotification == 1 ? true : false, isActive == 1 ? true : false );
        }
        result.close();
        db.close();
        return reminder;
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