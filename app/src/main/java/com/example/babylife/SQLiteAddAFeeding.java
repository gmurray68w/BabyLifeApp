package com.example.babylife;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteAddAFeeding extends SQLiteOpenHelper {
    //This creates the actual sqlite table.
    private static final String DATABASE_NAME = "feeding_log.db";
    private static final int DATABASE_VERSION = 1;

    public SQLiteAddAFeeding(Context context) {
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
                FeedingLogContract.FeedingLogEntry.COLUMN_AMOUNT + "TEXT" +
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
        db.execSQL("DELETE FROM" + tableName);
        db.close();
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +  FeedingLogContract.FeedingLogEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
