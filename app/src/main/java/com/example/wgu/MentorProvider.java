package com.example.wgu;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MentorProvider extends ContentProvider {
    public static final String CONTENT_ITEM_TYPE = "Mentor";
    private static final String AUTHORITY = "com.example.wgu.mentorprovider";
    private static final String BASE_PATH = "asessment";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
    private static final int MENTORS_BY_COURSE_ID = 1;
    private static final int MENTOR_BY_ID = 2;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/c/#", MENTORS_BY_COURSE_ID);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", MENTOR_BY_ID);
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
                DBHelper.TABLE_MENTOR,
                DBHelper.ALL_MENTOR_COLUMNS,
                selection,
                null, null, null,
                DBHelper.COLUMN_MENTOR_NAME);
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long id = db.insert(DBHelper.TABLE_MENTOR, null, contentValues);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return db.update(DBHelper.TABLE_MENTOR, contentValues, selection, selectionArgs);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return db.delete(DBHelper.TABLE_MENTOR, selection, selectionArgs);
    }
}
