package io.github.mattlavallee.budgetbeaver.data;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import io.github.mattlavallee.budgetbeaver.data.tables.AccountDispatcher;

public class DatabaseDispatcher extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "BudgetBeaver.db";

    public AccountDispatcher Accounts = new AccountDispatcher(this);

    public DatabaseDispatcher(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Accounts.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}