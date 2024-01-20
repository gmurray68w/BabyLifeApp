package com.example.babylife.contracts;

import android.provider.BaseColumns;

public class PumpingSessionContract {

    public static class PumpingLogEntry implements BaseColumns{
        public static final String TABLE_NAME = "pumping_logs";
        public static final String COLUMN_LOG_TYPE = "logtype";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TIME = "time";

        public static final String COLUMN_DURATION = "duration";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_FEEZER_SAVE = "notes";
        public static final String COLUMN_NOTES = "notes";

    }
}

