package com.example.wgu;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class TermProvider extends ContentProvider {
    public static final String CONTENT_ITEM_TYPE = "Term";
    private static final String AUTHORITY = "com.example.wgu.termprovider";
    private static final String BASE_PATH = "term";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
    private static final int TERMS = 1;
    private static final int TERM_ID = 2;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, TERMS);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", TERM_ID);
    }

    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        TermDBHelper helper = new TermDBHelper(getContext());
        db = helper.getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (uriMatcher.match(uri) == TERM_ID) {
            selection = TermDBHelper.COLUMN_NAME_ID + "=" + uri.getLastPathSegment();
        }

        return db.query(
                TermDBHelper.TABLE_NAME,
                TermDBHelper.ALL_COLUMNS,
                selection,
                null, null, null,
                TermDBHelper.COLUMN_NAME_TITLE);
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long id = db.insert(TermDBHelper.TABLE_NAME, null, contentValues);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return db.update(TermDBHelper.TABLE_NAME, contentValues, selection, selectionArgs);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return db.delete(TermDBHelper.TABLE_NAME, selection, selectionArgs);
    }
}
