package com.example.babylife;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

    //This creates the actual sqlite table.
    private static final String DATABASE_NAME = "diaper_log.db";
    private static final int DATABASE_VERSION = 1;

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_DIAPER_LOG_TABLE = "CREATE TABLE " +
                ChildLogContract.DiaperLogEntry.TABLE_NAME + " (" +
                ChildLogContract.DiaperLogEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ChildLogContract.DiaperLogEntry.COLUMN_LOG_TYPE + " TEXT NOT NULL," +
                ChildLogContract.DiaperLogEntry.COLUMN_NAME + " TEXT NOT NULL," +
                ChildLogContract.DiaperLogEntry.COLUMN_DATE + " TEXT NOT NULL," +
                ChildLogContract.DiaperLogEntry.COLUMN_TIME + " TEXT NOT NULL," +
                ChildLogContract.DiaperLogEntry.COLUMN_TYPE + " TEXT NOT NULL," +
                ChildLogContract.DiaperLogEntry.COLUMN_NOTES + " TEXT" +
                ");";

        db.execSQL(SQL_CREATE_DIAPER_LOG_TABLE);
    }
    public void insertDiaperChange(String logType, String childName, String date, String time, String diaperType, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ChildLogContract.DiaperLogEntry.COLUMN_LOG_TYPE, logType);
        values.put(ChildLogContract.DiaperLogEntry.COLUMN_NAME, childName);
        values.put(ChildLogContract.DiaperLogEntry.COLUMN_DATE, date);
        values.put(ChildLogContract.DiaperLogEntry.COLUMN_TIME, time);
        values.put(ChildLogContract.DiaperLogEntry.COLUMN_TYPE, diaperType);
        values.put(ChildLogContract.DiaperLogEntry.COLUMN_NOTES, notes);

        db.insert(ChildLogContract.DiaperLogEntry.TABLE_NAME, null, values);
        db.close();
    }
    public void deleteAllEntries(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + tableName);
        db.close();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ChildLogContract.DiaperLogEntry.TABLE_NAME);
        onCreate(db);
    }
}