package io.github.mattlavallee.budgetbeaver.data.tables;


import io.github.mattlavallee.budgetbeaver.models.Account;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class AccountDispatcher {
    private SQLiteOpenHelper dbHelper;
    private static final String TABLE_NAME = "accounts";

    public AccountDispatcher( SQLiteOpenHelper helper ){
        dbHelper = helper;
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
        "id integer primary key, name text, active integer)");
    }

    public ArrayList<Account> getAccounts(){
        ArrayList<Account> accounts = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE active = 1", null);
        result.moveToFirst();

        while(result.isAfterLast() == false){
            int id = result.getInt(result.getColumnIndex("id"));
            String name= result.getString(result.getColumnIndex("name"));
            int active = result.getInt(result.getColumnIndex("active"));
            accounts.add(new Account(id, name, active));

            result.moveToNext();
        }
        return accounts;
    }
}
