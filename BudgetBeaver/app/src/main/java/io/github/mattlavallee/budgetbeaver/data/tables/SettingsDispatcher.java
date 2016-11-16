package io.github.mattlavallee.budgetbeaver.data.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import io.github.mattlavallee.budgetbeaver.models.Settings;
import io.github.mattlavallee.budgetbeaver.models.enums.SortDirection;
import io.github.mattlavallee.budgetbeaver.models.enums.SortType;

public class SettingsDispatcher {
    private SQLiteOpenHelper dbHelper;
    private static final String TABLE_NAME = "settings";

    public SettingsDispatcher( SQLiteOpenHelper helper ){
        dbHelper = helper;
    }

    public void onCreate(SQLiteDatabase db){
        //create the table
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
        "id integer primary key, defaultSortType integer, defaultSortDirection integer, " +
        "positiveTransactionColor text, negativeTransactionColor text, " +
        "positiveAccountColor text, negativeAccountColor text, defaultCurrencyId integer);");

        //create the initial settings
        Settings initialSettings = new Settings();
        initialSettings.setDefaultSortDirection(SortDirection.Ascending);
        initialSettings.setDefaultSortType(SortType.Date);
        initialSettings.setPositiveTransactionColor("#000000");
        initialSettings.setNegativeTransactionColor("#ff0000");
        initialSettings.setPositiveAccountColor("#000000");
        initialSettings.setNegativeAccountColor("#ff0000");
        initialSettings.setDefaultCurrencyId(-1);

        //insert initial settings into the database
        ContentValues content = getSettingsAsContentValues(initialSettings);
        db.insert(TABLE_NAME, null, content);
    }

    public Settings getSettings(){
        Settings appSettings = new Settings();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME + " LIMIT 1;", null);
        result.moveToFirst();

        while(result.isAfterLast() == false){
            SortType sortType = SortType.fromInt(result.getInt(result.getColumnIndex("defaultSortType")));
            SortDirection sortDirection = SortDirection.fromInt(result.getInt(result.getColumnIndex("defaultSortDirection")));

            appSettings.setDefaultSortType(sortType);
            appSettings.setDefaultSortDirection(sortDirection);
            appSettings.setPositiveTransactionColor(result.getString(result.getColumnIndex("positiveTransactionColor")));
            appSettings.setNegativeTransactionColor(result.getString(result.getColumnIndex("negativeTransactionColor")));
            appSettings.setPositiveAccountColor(result.getString(result.getColumnIndex("positiveAccountColor")));
            appSettings.setNegativeAccountColor(result.getString(result.getColumnIndex("negativeAccountColor")));
            appSettings.setDefaultCurrencyId(result.getInt(result.getColumnIndex("defaultCurrencyId")));
            result.moveToNext();
        }
        result.close();
        db.close();

        return appSettings;
    }

    public long updateSettings(Settings appSettings){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues content = getSettingsAsContentValues(appSettings);
        long result = db.update(TABLE_NAME, content, "id >= 0", null);
        db.close();
        return result;
    }

    private ContentValues getSettingsAsContentValues(Settings appSettings){
        ContentValues content = new ContentValues();
        content.put("defaultSortType", appSettings.getDefaultSortType().getValue());
        content.put("defaultSortDirection", appSettings.getDefaultSortDirection().getValue());
        content.put("positiveTransactionColor", appSettings.getPositiveTransactionColor());
        content.put("negativeTransactionColor", appSettings.getNegativeTransactionColor());
        content.put("positiveAccountColor", appSettings.getPositiveAccountColor());
        content.put("negativeAccountColor", appSettings.getNegativeAccountColor());
        content.put("defaultCurrencyId", appSettings.getDefaultCurrencyId());
        return content;
    }
}
