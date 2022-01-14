package com.example.college_students_communication_app.contracts;

import android.provider.BaseColumns;

public class ChatDataReaderContract {

    private ChatDataReaderContract() {}

    public static class ChatDataEntry implements BaseColumns {
        public static final String TABLE_NAME = "chat";
        public static final String COLUMN_NAME_MESSAGE = "message";
        public static final String COLUMN_NAME_SENDER = "senderId";
        public static final String COLUMN_NAME_TIME = "time";
    }

    public static final String SQL_CREATE_CHAT =
            "CREATE TABLE " + ChatDataEntry.TABLE_NAME + " (" +
                    ChatDataEntry._ID + " INTEGER PRIMARY KEY," +
                    ChatDataEntry.COLUMN_NAME_MESSAGE + " TEXT," +
                    ChatDataEntry.COLUMN_NAME_SENDER + " TEXT,"+
                    ChatDataEntry.COLUMN_NAME_TIME+ " INTEGER)";

    public static final String SQL_DELETE_CHAT =
            "DROP TABLE IF EXISTS " + ChatDataEntry.TABLE_NAME;
}
