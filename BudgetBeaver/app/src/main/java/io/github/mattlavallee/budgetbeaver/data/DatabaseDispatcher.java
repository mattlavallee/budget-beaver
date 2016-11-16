package io.github.mattlavallee.budgetbeaver.data;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import io.github.mattlavallee.budgetbeaver.data.tables.AccountDispatcher;
import io.github.mattlavallee.budgetbeaver.data.tables.SettingsDispatcher;
import io.github.mattlavallee.budgetbeaver.data.tables.TagDispatcher;
import io.github.mattlavallee.budgetbeaver.data.tables.TransactionDispatcher;

public class DatabaseDispatcher extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "BudgetBeaver.db";
    //This should be incremented when onUpdate needs to run (new table, update to table, etc)
    private static final int DB_VERSION = 4;

    public AccountDispatcher Accounts = new AccountDispatcher(this);
    public TransactionDispatcher Transactions = new TransactionDispatcher(this);
    public TagDispatcher Tags = new TagDispatcher(this);
    public SettingsDispatcher Settings = new SettingsDispatcher(this);

    public DatabaseDispatcher(Context context){
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Accounts.onCreate(db);
        Transactions.onCreate(db);
        Tags.onCreate(db);
        Settings.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}