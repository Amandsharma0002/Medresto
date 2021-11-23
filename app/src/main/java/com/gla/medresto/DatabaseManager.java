package com.gla.medresto;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DatabaseManager extends SQLiteOpenHelper {
    private static String dbname = "reminder";                                                      //Table  name to store reminders in sqllite
    private static String tableName = "reminder_table";
    Context context;

    public DatabaseManager(@Nullable Context context) {
        super(context, dbname, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {                                           //sql query to insert data in sqllite
        String query = "create table if not exists " + tableName + "(id integer primary key autoincrement,title text,date text,time text)";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String query = "DROP TABLE IF EXISTS " + tableName;                                         //sql query to check table with the same name or not
        sqLiteDatabase.execSQL(query);                                                              //executes the sql command
        onCreate(sqLiteDatabase);
    }

    public String addreminder(String title, String date, String time) {
        SQLiteDatabase database = this.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);                                                          //Inserts  data into sqllite database
        contentValues.put("date", date);
        contentValues.put("time", time);

        float result = database.insert(tableName, null, contentValues);    //returns -1 if data successfully inserts into database

        if (result == -1) {
            return "Failed";
        } else {
            return "Successfully Added Medicine";
        }
    }

    public Cursor readAllReminders() {
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "select * from " + tableName + " order by id desc";                               //Sql query to  retrieve  data from the database
        Cursor cursor = database.rawQuery(query, null);
        return cursor;
    }

    public void flushDatabase() {
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "Delete from " + tableName;
        database.execSQL(query);
    }

    public void deleteTask(String title, String date, String time) {
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "DELETE FROM " + tableName + " WHERE title='" + title + "' AND date='" + date + "' AND time='" + time + "'";
        database.execSQL(query);
        Toast.makeText(context, "Medicine Removed Successfully", Toast.LENGTH_SHORT).show();
    }
}
