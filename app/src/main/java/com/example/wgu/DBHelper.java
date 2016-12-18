package com.example.wgu;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "wgu.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_TERM = "Term";
    public static final String COLUMN_TERM_ID = "_id";
    public static final String COLUMN_TERM_TITLE = "Title";
    public static final String COLUMN_TERM_START = "StartDate";
    public static final String COLUMN_TERM_END = "EndDate";
    public static final String[] ALL_TERM_COLUMNS = {COLUMN_TERM_ID, COLUMN_TERM_TITLE, COLUMN_TERM_START, COLUMN_TERM_END};
    private static final String CREATE_TERM_TABLE =
            "CREATE TABLE " + TABLE_TERM + " (" +
                    COLUMN_TERM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TERM_TITLE + " TEXT, " +
                    COLUMN_TERM_START + " TEXT, " +
                    COLUMN_TERM_END + " TEXT" +
                    ")";

    public static final String TABLE_COURSE = "Course";
    public static final String COLUMN_COURSE_ID = "_id";
    public static final String COLUMN_COURSE_TERM_ID = "TermId";
    public static final String COLUMN_COURSE_TITLE = "Title";
    public static final String COLUMN_COURSE_START = "StartDate";
    public static final String COLUMN_COURSE_END = "EndDate";
    public static final String COLUMN_COURSE_STATUS = "Status";
    public static final String[] ALL_COURSE_COLUMNS = {COLUMN_COURSE_ID, COLUMN_COURSE_TERM_ID, COLUMN_COURSE_TITLE, COLUMN_COURSE_START, COLUMN_COURSE_END, COLUMN_COURSE_STATUS};
    private static final String CREATE_COURSE_TABLE =
            "CREATE TABLE " + TABLE_COURSE + " (" +
                    COLUMN_COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_COURSE_TERM_ID + " INTEGER, " +
                    COLUMN_COURSE_TITLE + " TEXT, " +
                    COLUMN_COURSE_START + " TEXT, " +
                    COLUMN_COURSE_END + " TEXT," +
                    COLUMN_COURSE_STATUS + " TEXT" +
                    ")";

    public static final String TABLE_ASSESSMENT = "Assessment";
    public static final String COLUMN_ASSESSMENT_ID = "_id";
    public static final String COLUMN_ASSESSMENT_COURSE_ID = "CourseId";
    public static final String COLUMN_ASSESSMENT_TITLE = "Title";
    public static final String COLUMN_ASSESSMENT_DUE = "DueDate";
    public static final String COLUMN_ASSESSMENT_GOAL = "GoalDate";
    public static final String COLUMN_ASSESSMENT_TYPE = "Type";
    public static final String[] ALL_ASSESSMENT_COLUMNS = {COLUMN_ASSESSMENT_ID, COLUMN_ASSESSMENT_COURSE_ID, COLUMN_ASSESSMENT_TITLE, COLUMN_ASSESSMENT_DUE, COLUMN_ASSESSMENT_GOAL, COLUMN_ASSESSMENT_TYPE};
    private static final String CREATE_ASSESSMENT_TABLE =
            "CREATE TABLE " + TABLE_ASSESSMENT + " (" +
                    COLUMN_ASSESSMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ASSESSMENT_COURSE_ID + " INTEGER, " +
                    COLUMN_ASSESSMENT_TITLE + " TEXT, " +
                    COLUMN_ASSESSMENT_DUE + " TEXT, " +
                    COLUMN_ASSESSMENT_GOAL + " TEXT," +
                    COLUMN_ASSESSMENT_TYPE + " TEXT" +
                    ")";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TERM_TABLE);
        sqLiteDatabase.execSQL(CREATE_COURSE_TABLE);
        sqLiteDatabase.execSQL(CREATE_ASSESSMENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TERM);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSESSMENT);
        onCreate(sqLiteDatabase);
    }
}
