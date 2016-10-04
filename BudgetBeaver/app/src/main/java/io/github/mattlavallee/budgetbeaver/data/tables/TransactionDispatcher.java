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

import io.github.mattlavallee.budgetbeaver.models.Transaction;

public class TransactionDispatcher {
    private SQLiteOpenHelper dbHelper;
    private static final String TABLE_NAME = "transactions";
    private DateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");

    public TransactionDispatcher( SQLiteOpenHelper helper ){
        dbHelper = helper;
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
        "id integer primary key, accountId integer not null, location text, amount double, " +
        "description text, dateModified text, active integer, currencyId integer)");
    }

    public ArrayList<Transaction> getTransactionsForAccount(int accountId){
        ArrayList<Transaction> transactions = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE active = 1 " +
            " AND accountId = " + accountId, null);
        result.moveToFirst();

        while(result.isAfterLast() == false) {
            int id = result.getInt(result.getColumnIndex("id"));
            String location = result.getString(result.getColumnIndex("location"));
            double amount = result.getDouble(result.getColumnIndex("amount"));
            String description = result.getString(result.getColumnIndex("description"));
            String editDate = result.getString(result.getColumnIndex("dateModified"));
            Date dateModified;
            try{
                dateModified = dateFormatter.parse(editDate);
            } catch(ParseException ex){
                dateModified = new Date();
            }
            int active = result.getInt(result.getColumnIndex("active"));

            Transaction currentTrans = new Transaction(id, accountId, location, description, amount, dateModified, active != 0);
            transactions.add(currentTrans);

            result.moveToNext();
        }
        return transactions;
    }

    public Transaction getTransactionById(int transactionId){
        Transaction currTrans = new Transaction();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME
                + " WHERE active = 1 AND id = " + transactionId, null);
        result.moveToFirst();
        while(result.isAfterLast() == false){
            int id = result.getInt(result.getColumnIndex("id"));
            int accountId = result.getInt(result.getColumnIndex("accountId"));
            String location = result.getString(result.getColumnIndex("location"));
            double amount = result.getDouble(result.getColumnIndex("amount"));
            String description = result.getString(result.getColumnIndex("description"));
            String editDate = result.getString(result.getColumnIndex("dateModified"));
            Date dateModified;
            try{
                dateModified = dateFormatter.parse(editDate);
            } catch(ParseException ex){
                dateModified = new Date();
            }
            int active = result.getInt(result.getColumnIndex("active"));

            currTrans = new Transaction(id, accountId, location, description, amount, dateModified, active != 0);

            result.moveToNext();
        }
        return currTrans;
    }

    public long insertTransaction( Transaction newTransaction ){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put("accountId", newTransaction.getAccountId());
        content.put("location", newTransaction.getLocation());
        content.put("amount", newTransaction.getAmount());
        content.put("description", newTransaction.getDescription());
        content.put("dateModified", dateFormatter.format(new Date()));
        content.put("active", newTransaction.isActive() ? 1 : 0);
        content.put("currencyId", -1);

        return db.insert(TABLE_NAME, null, content);
    }

    public long updateTransaction( Transaction transaction ){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put("location", transaction.getLocation());
        content.put("amount", transaction.getAmount());
        content.put("description", transaction.getDescription());
        Date date = transaction.getTransactionDate();
        content.put("dateModified", dateFormatter.format(date));
        content.put("active", transaction.isActive() ? 1 : 0);
        content.put("currencyId", -1);
        return db.update(TABLE_NAME, content, "id = " + transaction.getId(), null);
    }
}

