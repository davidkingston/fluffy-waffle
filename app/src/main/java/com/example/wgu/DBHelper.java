package com.example.wgu;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "wgu.db";
    private static final int DATABASE_VERSION = 4;

    static final String TABLE_TERM = "Term";
    static final String COLUMN_TERM_ID = "_id";
    static final String COLUMN_TERM_TITLE = "Title";
    static final String COLUMN_TERM_START = "StartDate";
    static final String COLUMN_TERM_END = "EndDate";
    static final String[] ALL_TERM_COLUMNS = {COLUMN_TERM_ID, COLUMN_TERM_TITLE, COLUMN_TERM_START, COLUMN_TERM_END};
    private static final String CREATE_TERM_TABLE =
            "CREATE TABLE " + TABLE_TERM + " (" +
                    COLUMN_TERM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TERM_TITLE + " TEXT, " +
                    COLUMN_TERM_START + " TEXT, " +
                    COLUMN_TERM_END + " TEXT" +
                    ")";

    static final String TABLE_COURSE = "Course";
    static final String COLUMN_COURSE_ID = "_id";
    static final String COLUMN_COURSE_TERM_ID = "TermId";
    static final String COLUMN_COURSE_TITLE = "Title";
    static final String COLUMN_COURSE_START = "StartDate";
    static final String COLUMN_COURSE_END = "EndDate";
    static final String COLUMN_COURSE_STATUS = "Status";
    static final String COLUMN_COURSE_NOTE = "Note";
    static final String COLUMN_COURSE_IMAGE = "Image";
    static final String[] ALL_COURSE_COLUMNS = {COLUMN_COURSE_ID, COLUMN_COURSE_TERM_ID, COLUMN_COURSE_TITLE, COLUMN_COURSE_START, COLUMN_COURSE_END, COLUMN_COURSE_STATUS, COLUMN_COURSE_NOTE, COLUMN_COURSE_IMAGE};
    private static final String CREATE_COURSE_TABLE =
            "CREATE TABLE " + TABLE_COURSE + " (" +
                    COLUMN_COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_COURSE_TERM_ID + " INTEGER, " +
                    COLUMN_COURSE_TITLE + " TEXT, " +
                    COLUMN_COURSE_START + " TEXT, " +
                    COLUMN_COURSE_END + " TEXT," +
                    COLUMN_COURSE_STATUS + " TEXT," +
                    COLUMN_COURSE_NOTE + " TEXT," +
                    COLUMN_COURSE_IMAGE + " BLOB" +
                    ")";

    static final String TABLE_ASSESSMENT = "Assessment";
    static final String COLUMN_ASSESSMENT_ID = "_id";
    static final String COLUMN_ASSESSMENT_COURSE_ID = "CourseId";
    static final String COLUMN_ASSESSMENT_TITLE = "Title";
    static final String COLUMN_ASSESSMENT_DUE = "DueDate";
    static final String COLUMN_ASSESSMENT_GOAL = "GoalDate";
    static final String COLUMN_ASSESSMENT_TYPE = "Type";
    static final String COLUMN_ASSESSMENT_NOTE = "Note";
    static final String COLUMN_ASSESSMENT_IMAGE = "Image";
    static final String[] ALL_ASSESSMENT_COLUMNS = {COLUMN_ASSESSMENT_ID, COLUMN_ASSESSMENT_COURSE_ID, COLUMN_ASSESSMENT_TITLE, COLUMN_ASSESSMENT_DUE, COLUMN_ASSESSMENT_GOAL, COLUMN_ASSESSMENT_TYPE, COLUMN_ASSESSMENT_NOTE, COLUMN_ASSESSMENT_IMAGE};
    private static final String CREATE_ASSESSMENT_TABLE =
            "CREATE TABLE " + TABLE_ASSESSMENT + " (" +
                    COLUMN_ASSESSMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ASSESSMENT_COURSE_ID + " INTEGER, " +
                    COLUMN_ASSESSMENT_TITLE + " TEXT, " +
                    COLUMN_ASSESSMENT_DUE + " TEXT, " +
                    COLUMN_ASSESSMENT_GOAL + " TEXT," +
                    COLUMN_ASSESSMENT_TYPE + " TEXT," +
                    COLUMN_ASSESSMENT_NOTE + " TEXT," +
                    COLUMN_ASSESSMENT_IMAGE + " BLOB" +
                    ")";

    static final String TABLE_MENTOR = "Mentor";
    static final String COLUMN_MENTOR_ID = "_id";
    static final String COLUMN_MENTOR_COURSE_ID = "CourseId";
    static final String COLUMN_MENTOR_NAME = "Name";
    static final String COLUMN_MENTOR_EMAIL = "Email";
    static final String COLUMN_MENTOR_PHONE = "Phone";
    static final String[] ALL_MENTOR_COLUMNS = {COLUMN_MENTOR_ID, COLUMN_MENTOR_COURSE_ID, COLUMN_MENTOR_NAME, COLUMN_MENTOR_EMAIL, COLUMN_MENTOR_PHONE};
    private static final String CREATE_MENTOR_TABLE =
            "CREATE TABLE " + TABLE_MENTOR + " (" +
                    COLUMN_MENTOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_MENTOR_COURSE_ID + " INTEGER, " +
                    COLUMN_MENTOR_NAME + " TEXT, " +
                    COLUMN_MENTOR_EMAIL + " TEXT, " +
                    COLUMN_MENTOR_PHONE + " TEXT" +
                    ")";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TERM_TABLE);
        sqLiteDatabase.execSQL(CREATE_COURSE_TABLE);
        sqLiteDatabase.execSQL(CREATE_ASSESSMENT_TABLE);
        sqLiteDatabase.execSQL(CREATE_MENTOR_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_MENTOR);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSESSMENT);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TERM);
        onCreate(sqLiteDatabase);
    }
}
