package io.github.mattlavallee.budgetbeaver.data.tables;


import io.github.mattlavallee.budgetbeaver.models.Account;
import io.github.mattlavallee.budgetbeaver.models.enums.SortDirection;
import io.github.mattlavallee.budgetbeaver.models.enums.SortType;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class AccountDispatcher {
    private SQLiteOpenHelper dbHelper;
    private static final String TABLE_NAME = "accounts";

    public AccountDispatcher( SQLiteOpenHelper helper ){
        dbHelper = helper;
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
        "id integer primary key, name text, sortType integer, sortDirection integer, active integer)");
    }

    public ArrayList<Account> getAccounts(){
        ArrayList<Account> accounts = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE active = 1", null);
        result.moveToFirst();

        while(result.isAfterLast() == false){
            int id = result.getInt(result.getColumnIndex("id"));
            String name= result.getString(result.getColumnIndex("name"));
            int sortType = result.getInt(result.getColumnIndex("sortType"));
            int sortDirection = result.getInt(result.getColumnIndex("sortDirection"));
            int active = result.getInt(result.getColumnIndex("active"));
            accounts.add(new Account(id, name, sortType, sortDirection, active));

            result.moveToNext();
        }
        result.close();
        db.close();
        return accounts;
    }

    public Account getAccountById(int accountId){
        Account account = new Account();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME
                + " WHERE active = 1 AND id = " + accountId, null);
        result.moveToFirst();
        while(result.isAfterLast() == false){
            int id = result.getInt(result.getColumnIndex("id"));
            String name = result.getString(result.getColumnIndex("name"));
            int sortType = result.getInt(result.getColumnIndex("sortType"));
            int sortDirection = result.getInt(result.getColumnIndex("sortDirection"));
            int active = result.getInt(result.getColumnIndex("active"));
            account = new Account(id, name, sortType, sortDirection, active);

            result.moveToNext();
        }
        result.close();
        db.close();
        return account;
    }

    public long insertAccount( Account newAccount ){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put("name", newAccount.getName());
        content.put("sortType", -1);
        content.put("sortDirection", -1);
        content.put("active", newAccount.isActive() ? 1 : 0 );
        long result = db.insert(TABLE_NAME, null, content);
        db.close();
        return result;
    }

    public long updateAccount( Account account ){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put("name", account.getName());
        SortType type = account.getSortType();
        SortDirection direction = account.getSortDirection();
        content.put("sortType", type == null ? -1 : type.getValue());
        content.put("sortDirection", direction == null ? -1 : direction.getValue());
        content.put("active", account.isActive() ? 1 : 0);
        long result = db.update(TABLE_NAME, content, "id = " + account.getId(), null);
        db.close();
        return result;
    }
}
