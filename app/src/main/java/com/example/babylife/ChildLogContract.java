package com.example.babylife;

import android.provider.BaseColumns;

public class ChildLogContract {
    private ChildLogContract(){}

    //Sets up the column names for my sqlite table
    public static class DiaperLogEntry implements BaseColumns{
        public static final String TABLE_NAME = "child_logs";
        public static final String COLUMN_LOG_TYPE = "logtype";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_NOTES = "notes";
    }
}
