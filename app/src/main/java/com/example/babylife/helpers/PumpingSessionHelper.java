package com.example.babylife.helpers;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.babylife.contracts.FeedingLogContract;
import com.example.babylife.contracts.PumpingSessionContract;

public class PumpingSessionHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "pumping_log.db";
    private static final int DATABASE_VERSION = 0;

    public PumpingSessionHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_PUMPING_LOG_TABLE = "CREATE TABLE " +
                PumpingSessionContract.PumpingLogEntry.TABLE_NAME + " (" +
                PumpingSessionContract.PumpingLogEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PumpingSessionContract.PumpingLogEntry.COLUMN_LOG_TYPE + " TEXT NOT NULL," +
                PumpingSessionContract.PumpingLogEntry.COLUMN_NAME + " TEXT NOT NULL," +
                PumpingSessionContract.PumpingLogEntry.COLUMN_DATE + " TEXT NOT NULL," +
                PumpingSessionContract.PumpingLogEntry.COLUMN_TOTAL_AMOUNT + " INTEGER NOT NULL," +
                PumpingSessionContract.PumpingLogEntry.COLUMN_SAVE + " INTEGER NOT NULL," +
                PumpingSessionContract.PumpingLogEntry.COLUMN_DURATION + " TEXT NOT NULL," +
                PumpingSessionContract.PumpingLogEntry.COLUMN_AMOUNT + " INTEGER," +
                PumpingSessionContract.PumpingLogEntry.COLUMN_SIDE + " TEXT" +
                ");";
        sqLiteDatabase.execSQL(SQL_CREATE_PUMPING_LOG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insertPumpingLog(String logType, String childName, String date, Integer total_amount, Integer save, String duration, Integer amount, String side) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PumpingSessionContract.PumpingLogEntry.COLUMN_LOG_TYPE, logType);
        values.put(PumpingSessionContract.PumpingLogEntry.COLUMN_NAME, childName);
        values.put(PumpingSessionContract.PumpingLogEntry.COLUMN_DATE, date);
        values.put(PumpingSessionContract.PumpingLogEntry.COLUMN_TOTAL_AMOUNT, total_amount);
        values.put(PumpingSessionContract.PumpingLogEntry.COLUMN_SAVE, save);
        values.put(PumpingSessionContract.PumpingLogEntry.COLUMN_DURATION, duration);
        values.put(PumpingSessionContract.PumpingLogEntry.COLUMN_AMOUNT, amount);
        values.put(PumpingSessionContract.PumpingLogEntry.COLUMN_SIDE, side);

        db.insert(FeedingLogContract.FeedingLogEntry.TABLE_NAME, null, values);

        db.close();
    }

    public void deleteAllEntries(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM" + tableName);
        db.close();
    }

    //Call each time entry is made.
    public void updateTotalAmountIfNeeded(int entryId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // First, check if the entry should be saved
        Cursor saveCursor = db.query(PumpingSessionContract.PumpingLogEntry.TABLE_NAME,
                new String[]{PumpingSessionContract.PumpingLogEntry.COLUMN_SAVE},
                PumpingSessionContract.PumpingLogEntry._ID + "=?",
                new String[]{String.valueOf(entryId)},
                null, null, null);

        if (saveCursor != null && saveCursor.moveToFirst()) {
            @SuppressLint("Range") int save = saveCursor.getInt(saveCursor.getColumnIndex(PumpingSessionContract.PumpingLogEntry.COLUMN_SAVE));
            saveCursor.close();

            if (save == 1) {
                // Calculate the new total amount
                Cursor sumCursor = db.rawQuery("SELECT SUM(" + PumpingSessionContract.PumpingLogEntry.COLUMN_AMOUNT + ") FROM " +
                        PumpingSessionContract.PumpingLogEntry.TABLE_NAME +
                        " WHERE " + PumpingSessionContract.PumpingLogEntry.COLUMN_SAVE + "=1", null);

                int newTotalAmount = 0;
                if (sumCursor != null && sumCursor.moveToFirst()) {
                    newTotalAmount = sumCursor.getInt(0); // Get the sum of the amounts
                    sumCursor.close();
                }

                // Update the total amount for the entry
                ContentValues values = new ContentValues();
                values.put(PumpingSessionContract.PumpingLogEntry.COLUMN_TOTAL_AMOUNT, newTotalAmount);

                db.update(PumpingSessionContract.PumpingLogEntry.TABLE_NAME, values,
                        PumpingSessionContract.PumpingLogEntry._ID + "=?",
                        new String[]{String.valueOf(entryId)});
            }
        }

        db.close();
    }
}
