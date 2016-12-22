package com.example.wgu;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class CourseProvider extends ContentProvider {
    public static final String CONTENT_ITEM_TYPE = "Course";
    private static final String AUTHORITY = "com.example.wgu.courseprovider";
    private static final String BASE_PATH = "course";
    public static final Uri COURSE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
    private static final int COURSES_BY_TERM_ID = 1;
    private static final int COURSE_BY_ID = 2;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/t/#", COURSES_BY_TERM_ID);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", COURSE_BY_ID);
    }

    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        DBHelper helper = new DBHelper(getContext());
        db = helper.getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return db.query(
                DBHelper.TABLE_COURSE,
                DBHelper.ALL_COURSE_COLUMNS,
                selection,
                null, null, null,
                DBHelper.COLUMN_COURSE_TITLE);
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long id = db.insert(DBHelper.TABLE_COURSE, null, contentValues);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return db.update(DBHelper.TABLE_COURSE, contentValues, selection, selectionArgs);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return db.delete(DBHelper.TABLE_COURSE, selection, selectionArgs);
    }
}
