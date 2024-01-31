package com.example.babylife.helpers;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;

import com.example.babylife.contracts.FeedingLogContract;
import com.example.babylife.contracts.PumpingSessionContract;

public class PumpingSessionHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "pumping_log.db";
    private static final int DATABASE_VERSION = 2;

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
    public void insertPumpingLogWithTotal(String logType, String childName, String date, Integer amount, String duration, String side, boolean save) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            // Get the last total amount
            int lastTotal = getLastTotalAmount(db);

            // Calculate the new total amount
            int newTotal = save ? lastTotal + amount : lastTotal;

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(PumpingSessionContract.PumpingLogEntry.COLUMN_LOG_TYPE, logType);
            values.put(PumpingSessionContract.PumpingLogEntry.COLUMN_NAME, childName);
            values.put(PumpingSessionContract.PumpingLogEntry.COLUMN_DATE, date);
            values.put(PumpingSessionContract.PumpingLogEntry.COLUMN_AMOUNT, amount);
            values.put(PumpingSessionContract.PumpingLogEntry.COLUMN_DURATION, duration);
            values.put(PumpingSessionContract.PumpingLogEntry.COLUMN_SIDE, side);
            values.put(PumpingSessionContract.PumpingLogEntry.COLUMN_SAVE, save ? 1 : 0);
            values.put(PumpingSessionContract.PumpingLogEntry.COLUMN_TOTAL_AMOUNT, newTotal);

            // Insert the new row
            db.insertOrThrow(PumpingSessionContract.PumpingLogEntry.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    private int getLastTotalAmount(SQLiteDatabase db) {
        int lastTotal = 0;
        Cursor cursor = db.query(
                PumpingSessionContract.PumpingLogEntry.TABLE_NAME,
                new String[]{"MAX(" + PumpingSessionContract.PumpingLogEntry.COLUMN_TOTAL_AMOUNT + ")"},
                null, null, null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            lastTotal = cursor.getInt(0);
            cursor.close();
        }

        return lastTotal;
    }

    public long insertPumpingLog(String logType, String childName, String date, Integer total_amount, Integer save, String duration, Integer amount, String side) {
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

        long newRowId = db.insert(PumpingSessionContract.PumpingLogEntry.TABLE_NAME, null, values); // Ensure TABLE_NAME is "pumping_logs"

        db.close();
        return newRowId;
    }

    public Pair<Integer,Integer> getLastEntryDetails(){
        SQLiteDatabase db = this.getReadableDatabase();
        int lastEntryId = -1;
        int currentTotalAmount = 0;

        Cursor cursor = db.rawQuery("SELECT " + PumpingSessionContract.PumpingLogEntry._ID + ", " +
                PumpingSessionContract.PumpingLogEntry.COLUMN_TOTAL_AMOUNT +
                " FROM " + PumpingSessionContract.PumpingLogEntry.TABLE_NAME +
                " ORDER BY " + PumpingSessionContract.PumpingLogEntry._ID + " DESC LIMIT 1", null);

        if (cursor != null && cursor.moveToFirst()) {
            lastEntryId = cursor.getInt(0); // Get the last entry ID
            currentTotalAmount = cursor.getInt(1); // Get the current total amount
            cursor.close();
        }

        db.close();
        return new Pair<>(lastEntryId, currentTotalAmount);
    }
    public void deleteAllEntries(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + tableName);
        db.close();
    }

    //Call each time entry is made.
    public void updateTotalAmountIfNeeded(int entryId, int additionalAmount) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if the entry should be saved
        Cursor saveCursor = db.query(PumpingSessionContract.PumpingLogEntry.TABLE_NAME,
                new String[]{PumpingSessionContract.PumpingLogEntry.COLUMN_SAVE},
                PumpingSessionContract.PumpingLogEntry._ID + "=?",
                new String[]{String.valueOf(entryId)},
                null, null, null);

        if (saveCursor != null && saveCursor.moveToFirst()) {
            @SuppressLint("Range") int save = saveCursor.getInt(saveCursor.getColumnIndex(PumpingSessionContract.PumpingLogEntry.COLUMN_SAVE));
            saveCursor.close();

            if (save == 1) {
                // Retrieve the last total amount

                Cursor totalAmountCursor = db.rawQuery("SELECT " + PumpingSessionContract.PumpingLogEntry.COLUMN_TOTAL_AMOUNT +
                        " FROM " + PumpingSessionContract.PumpingLogEntry.TABLE_NAME +
                        " ORDER BY " + PumpingSessionContract.PumpingLogEntry._ID + " DESC LIMIT 1", null);

                int lastTotalAmount = 0;
                if (totalAmountCursor != null && totalAmountCursor.moveToFirst()) {
                    lastTotalAmount = totalAmountCursor.getInt(0); // Get the last total amount
                    totalAmountCursor.close();
                }
                Log.d("PumpingSessionHelper", "Updating total amount. Last total: " + lastTotalAmount + ", Additional amount: " + additionalAmount);

                // Update the total amount for the entry
                int newTotalAmount = lastTotalAmount + additionalAmount;
                ContentValues values = new ContentValues();
                values.put(PumpingSessionContract.PumpingLogEntry.COLUMN_TOTAL_AMOUNT, newTotalAmount);

                db.update(PumpingSessionContract.PumpingLogEntry.TABLE_NAME, values,
                        PumpingSessionContract.PumpingLogEntry._ID + "=?",
                        new String[]{String.valueOf(entryId)});
                Log.d("PumpingSessionHelper", "Updated total amount for entry ID " + entryId + " to " + newTotalAmount);
            }

        }

        db.close();
    }
    public Cursor getAllPumpingSessions() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                PumpingSessionContract.PumpingLogEntry.TABLE_NAME, // Table name
                null, // Columns to return (null for all)
                null, // Selection criteria
                null, // Selection args
                null, // Group by
                null, // Having
                PumpingSessionContract.PumpingLogEntry.COLUMN_DATE + " DESC" // Order by date descending
        );
    }
}
