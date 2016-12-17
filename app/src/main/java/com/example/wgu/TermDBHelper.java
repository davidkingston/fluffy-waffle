package com.example.wgu;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class TermDBHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "Term";
    public static final String COLUMN_NAME_ID = "_id";
    public static final String COLUMN_NAME_TITLE = "Title";
    public static final String COLUMN_NAME_START = "StartDate";
    public static final String COLUMN_NAME_END = "EndDate";
    public static final String[] ALL_COLUMNS = {COLUMN_NAME_ID, COLUMN_NAME_TITLE, COLUMN_NAME_START, COLUMN_NAME_END};
    private static final String DATABASE_NAME = "wgu.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME_TITLE + " TEXT, " +
                    COLUMN_NAME_START + " TEXT, " +
                    COLUMN_NAME_END + " TEXT" +
                    ")";

    public TermDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
