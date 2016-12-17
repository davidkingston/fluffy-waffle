package com.example.wgu;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {
    public static final String TABLE_COURSE = "Course";
    public static final String COLUMN_COURSE_ID = "_id";
    public static final String COLUMN_COURSE_TERM_ID = "TermId";
    public static final String COLUMN_COURSE_TITLE = "Title";
    public static final String COLUMN_COURSE_START = "StartDate";
    public static final String COLUMN_COURSE_END = "EndDate";
    public static final String COLUMN_COURSE_STATUS = "Status";
    public static final String[] ALL_COURSE_COLUMNS = {COLUMN_COURSE_ID, COLUMN_COURSE_TERM_ID, COLUMN_COURSE_TITLE, COLUMN_COURSE_START, COLUMN_COURSE_END, COLUMN_COURSE_STATUS};
    public static final String TABLE_TERM = "Term";
    public static final String COLUMN_TERM_ID = "_id";
    public static final String COLUMN_TERM_TITLE = "Title";
    public static final String COLUMN_TERM_START = "StartDate";
    public static final String COLUMN_TERM_END = "EndDate";
    public static final String[] ALL_TERM_COLUMNS = {COLUMN_TERM_ID, COLUMN_TERM_TITLE, COLUMN_TERM_START, COLUMN_TERM_END};
    private static final String CREATE_COURSE_TABLE =
            "CREATE TABLE " + TABLE_COURSE + " (" +
                    COLUMN_COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_COURSE_TERM_ID + " INTEGER, " +
                    COLUMN_COURSE_TITLE + " TEXT, " +
                    COLUMN_COURSE_START + " TEXT, " +
                    COLUMN_COURSE_END + " TEXT," +
                    COLUMN_COURSE_STATUS + " TEXT" +
                    ")";
    private static final String DATABASE_NAME = "wgu.db";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_TERM_TABLE =
            "CREATE TABLE " + TABLE_TERM + " (" +
                    COLUMN_TERM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TERM_TITLE + " TEXT, " +
                    COLUMN_TERM_START + " TEXT, " +
                    COLUMN_TERM_END + " TEXT" +
                    ")";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TERM_TABLE);
        sqLiteDatabase.execSQL(CREATE_COURSE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TERM);
        onCreate(sqLiteDatabase);
    }
}
