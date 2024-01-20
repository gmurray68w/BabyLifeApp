package com.example.babylife.contracts;

import android.provider.BaseColumns;

public class SleepSessionContract {

    static class SleepLogEntry implements BaseColumns{
        public static final String TABLE_NAME = "sleeping_logs";
        public static final String COLUMN_LOG_TYPE = "logtype";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_DURATION = "duration";
        public static final String COLUMN_NOTES = "notes";
    }
}
