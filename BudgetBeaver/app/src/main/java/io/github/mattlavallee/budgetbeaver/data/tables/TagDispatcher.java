package io.github.mattlavallee.budgetbeaver.data.tables;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

import io.github.mattlavallee.budgetbeaver.models.Tag;

public class TagDispatcher extends DispatcherBase{
    public TagDispatcher( SQLiteOpenHelper helper ){
        super(helper, "tags");
        tableFields = "id integer primary key, transactionId integer not null, name text";
    }

    public ArrayList<Tag> getTagsForTransaction( int transactionId ){
        ArrayList<Tag> tags = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME +
                " WHERE transactionId = " + transactionId, null);
        result.moveToFirst();

        while(result.isAfterLast() == false){
            int id = result.getInt(result.getColumnIndex("id"));
            String name = result.getString(result.getColumnIndex("name"));
            tags.add(new Tag(id, transactionId, name));
            result.moveToNext();
        }
        result.close();
        db.close();
        return tags;
    }

    public long insertTags( ArrayList<Tag> tags ){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long finalResult = 0;
        for(int i = 0; i < tags.size(); i++){
            ContentValues content = new ContentValues();
            content.put("transactionId", tags.get(i).getTransactionId());
            content.put("name", tags.get(i).getTagName());

            long result = db.insert(TABLE_NAME, null, content);
            //ensure that a failure is conveyed
            if(finalResult >= 0)
                finalResult = result;
        }
        db.close();
        return finalResult;
    }

    public long deleteTags( ArrayList<Tag> tags){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long finalResult = 0;
        for(int i = 0; i < tags.size(); i++){
            long result = db.delete(TABLE_NAME, "id=?", new String[]{ String.valueOf(tags.get(i).getId()) });
            //ensure that a failure is conveyed
            if(finalResult >= 0)
                finalResult = result;
        }
        db.close();
        return finalResult;
    }

    public ArrayList<Tag> getUniqueTags(){
        ArrayList<Tag> tags = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT MIN(id) as id, MIN(transactionId) as transactionId, name FROM "
                + TABLE_NAME + " GROUP BY name ORDER BY name ASC", null);
        result.moveToFirst();

        while(result.isAfterLast() == false){
            int id = result.getInt(result.getColumnIndex("id"));
            int transactionId = result.getInt(result.getColumnIndex("transactionId"));
            String name = result.getString(result.getColumnIndex("name"));
            tags.add(new Tag(id, transactionId, name));
            result.moveToNext();
        }
        result.close();
        db.close();
        return tags;
    }
}