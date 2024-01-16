package com.example.babylife.sqlitefiles;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class SQLiteBabyName extends SQLiteOpenHelper {
    private static final String DATABASE_NAME= "BabyNameDatabase.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "babies";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_BIRTHDAY = "birthday";
    public SQLiteBabyName(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_BABIES_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_BIRTHDAY + " TEXT" + ")";
        db.execSQL(CREATE_BABIES_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int

            oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);
    }

    public void addChild(String name, String birthday){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_BIRTHDAY, birthday);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<String> getAllChildNames() {
        List<String> childNames = new ArrayList<>();

        // Open the database for reading
        SQLiteDatabase db = this.getReadableDatabase();

        // Define the query
        String[] projection = { COLUMN_NAME };
        Cursor cursor = db.query(TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
            if (nameIndex != -1) {
                if (cursor.moveToFirst()) {
                    do {
                        String name = cursor.getString(nameIndex);
                        childNames.add(name);
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
        }

        db.close();

        return childNames;
    }
}
