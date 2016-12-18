package com.example.wgu;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class AssessmentProvider extends ContentProvider {
    public static final String CONTENT_ITEM_TYPE = "Assessment";
    private static final String AUTHORITY = "com.example.wgu.assessmentprovider";
    private static final String BASE_PATH = "asessment";
    public static final Uri ASSESSMENT_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
    private static final int ASSESSMENTS_BY_COURSE_ID = 1;
    private static final int ASSESSMENT_BY_ID = 2;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/c/#", ASSESSMENTS_BY_COURSE_ID);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", ASSESSMENT_BY_ID);
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
        if (uriMatcher.match(uri) == ASSESSMENT_BY_ID) {
            selection = DBHelper.COLUMN_COURSE_ID + "=" + uri.getLastPathSegment();
        } else {
            selection = DBHelper.COLUMN_COURSE_TERM_ID + "=" + uri.getLastPathSegment();
        }

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
