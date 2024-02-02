package com.example.babylife.helpers;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.babylife.contracts.FeedingLogContract;

public class SQLiteAddAFeedingHelper extends SQLiteOpenHelper {
    //This creates the actual sqlite table.
    private static final String DATABASE_NAME = "feeding_log.db";
    private static final int DATABASE_VERSION = 2;

    public SQLiteAddAFeedingHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FEEDING_LOG_TABLE = "CREATE TABLE " +
                FeedingLogContract.FeedingLogEntry.TABLE_NAME + " (" +
                FeedingLogContract.FeedingLogEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FeedingLogContract.FeedingLogEntry.COLUMN_LOG_TYPE + " TEXT NOT NULL," +
                FeedingLogContract.FeedingLogEntry.COLUMN_NAME + " TEXT NOT NULL," +
                FeedingLogContract.FeedingLogEntry.COLUMN_DATE + " TEXT NOT NULL," +
                FeedingLogContract.FeedingLogEntry.COLUMN_TIME + " TEXT NOT NULL," +
                FeedingLogContract.FeedingLogEntry.COLUMN_TYPE + " TEXT NOT NULL," +
                FeedingLogContract.FeedingLogEntry.COLUMN_DURATION + " TEXT NOT NULL," +
                FeedingLogContract.FeedingLogEntry.COLUMN_AMOUNT + " TEXT," +
                FeedingLogContract.FeedingLogEntry.COLUMN_NOTES + " TEXT" +
                ");";
        sqLiteDatabase.execSQL(SQL_CREATE_FEEDING_LOG_TABLE);
    }
    public void insertFeedingLog(String logType, String childName, String date, String time, String feedingType, String duration, String amount, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedingLogContract.FeedingLogEntry.COLUMN_LOG_TYPE, logType);
        values.put(FeedingLogContract.FeedingLogEntry.COLUMN_NAME, childName);
        values.put(FeedingLogContract.FeedingLogEntry.COLUMN_DATE, date);
        values.put(FeedingLogContract.FeedingLogEntry.COLUMN_TIME, time);
        values.put(FeedingLogContract.FeedingLogEntry.COLUMN_TYPE, feedingType);
        values.put(FeedingLogContract.FeedingLogEntry.COLUMN_DURATION, duration);
        values.put(FeedingLogContract.FeedingLogEntry.COLUMN_AMOUNT, amount);
        values.put(FeedingLogContract.FeedingLogEntry.COLUMN_NOTES, notes);

        db.insert(FeedingLogContract.FeedingLogEntry.TABLE_NAME, null, values);

        db.close();
    }
    public void deleteAllEntries(String tableName){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + tableName);
        db.close();
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +  FeedingLogContract.FeedingLogEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
    public void logFirstEntry() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(FeedingLogContract.FeedingLogEntry.TABLE_NAME, null, null, null, null, null, null, "1");

        if

        (cursor != null && cursor.moveToFirst()) {
// Assuming you have columns like name, date, time, etc. in your table
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(FeedingLogContract.FeedingLogEntry.COLUMN_NAME));
            @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(FeedingLogContract.FeedingLogEntry.COLUMN_DATE));
            @SuppressLint("Range") String time = cursor.getString(cursor.getColumnIndex(FeedingLogContract.FeedingLogEntry.COLUMN_TIME));
// ... fetch other fields as needed



            Log.d("DatabaseLog", "First Entry - Name: " + name + ", Date: " + date + ", Time: " + time);
            cursor.close();
        } else {
            Log.d("DatabaseLog", "No entries found in database");
        }

        db.close();

    }


    public Cursor fetchFeedingLogs() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                FeedingLogContract.FeedingLogEntry._ID,
                FeedingLogContract.FeedingLogEntry.COLUMN_NAME,
                FeedingLogContract.FeedingLogEntry.COLUMN_DATE,
                // ... other columns you need
        };

        // You can also use 'null' if you want to fetch all columns
        return db.query(
                FeedingLogContract.FeedingLogEntry.TABLE_NAME,  // The table to query
                projection,                                     // The array of columns to return (pass null to get all)
                null,                                           // The columns for the WHERE clause
                null,                                           // The values for the WHERE clause
                null,                                           // Don't group the rows
                null,                                           // Don't filter by row groups
                null                                            // The sort order
        );
    }
}
