package com.example.babylife.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.babylife.contracts.FeedingLogContract;
import com.example.babylife.contracts.SleepSessionContract;

public class SQLiteSleepHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "sleeping_log.db";
    private static final int DATABASE_VERSION = 1;

    public SQLiteSleepHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_SLEEPING_LOG_TABLE = "CREATE TABLE " +
                SleepSessionContract.SleepLogEntry.TABLE_NAME + " (" +
                SleepSessionContract.SleepLogEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SleepSessionContract.SleepLogEntry.COLUMN_LOG_TYPE + " TEXT NOT NULL," +
                SleepSessionContract.SleepLogEntry.COLUMN_NAME + " TEXT NOT NULL," +
                SleepSessionContract.SleepLogEntry.COLUMN_DATE + " TEXT NOT NULL," +
                SleepSessionContract.SleepLogEntry.COLUMN_START_TIME + " TEXT NOT NULL," +
                SleepSessionContract.SleepLogEntry.COLUMN_END_TIME + " TEXT NOT NULL," +
                SleepSessionContract.SleepLogEntry.COLUMN_DURATION + " TEXT NOT NULL," +
                SleepSessionContract.SleepLogEntry.COLUMN_NOTES + " TEXT" +
                ");";
        sqLiteDatabase.execSQL(SQL_CREATE_SLEEPING_LOG_TABLE);

    }

    public void insertSleepingLog(String logType, String childName, String date, String starttime, String endtime, String duration, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SleepSessionContract.SleepLogEntry.COLUMN_LOG_TYPE, logType);
        values.put(SleepSessionContract.SleepLogEntry.COLUMN_NAME, childName);
        values.put(SleepSessionContract.SleepLogEntry.COLUMN_DATE, date);
        values.put(SleepSessionContract.SleepLogEntry.COLUMN_START_TIME, starttime);
        values.put(SleepSessionContract.SleepLogEntry.COLUMN_END_TIME, endtime);
        values.put(SleepSessionContract.SleepLogEntry.COLUMN_DURATION, duration);
        values.put(SleepSessionContract.SleepLogEntry.COLUMN_NOTES, notes);

        db.insert(SleepSessionContract.SleepLogEntry.TABLE_NAME, null, values);

        db.close();
    }

    public void deleteAllEntries(String tableName){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM" + tableName);
        db.close();
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +  SleepSessionContract.SleepLogEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
