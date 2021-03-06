package io.github.mattlavallee.budgetbeaver.data.tables;

import io.github.mattlavallee.budgetbeaver.models.Account;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class AccountDispatcher extends DispatcherBase {
    public AccountDispatcher( SQLiteOpenHelper helper ){
        super(helper, "accounts");
        tableFields = "id integer primary key, name text, active integer";
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
            int active = result.getInt(result.getColumnIndex("active"));
            account = new Account(id, name, active);

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
        content.put("active", newAccount.isActive() ? 1 : 0 );
        long result = db.insert(TABLE_NAME, null, content);
        db.close();
        return result;
    }

    public long updateAccount( Account account ){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put("name", account.getName());
        content.put("active", account.isActive() ? 1 : 0);
        long result = db.update(TABLE_NAME, content, "id = " + account.getId(), null);
        db.close();
        return result;
    }
}
