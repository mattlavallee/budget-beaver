package io.github.mattlavallee.budgetbeaver.data.tables;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DispatcherBase {
    protected SQLiteOpenHelper dbHelper;
    protected String TABLE_NAME = "";
    protected String tableFields = "";

    public DispatcherBase(SQLiteOpenHelper helper, String tableName){
        dbHelper = helper;
        TABLE_NAME = tableName;
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + tableFields + ")");
    }
}
