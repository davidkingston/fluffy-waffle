package com.example.wgu;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class CourseListActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int COURSE_EDITOR_REQUEST_CODE = 1001;
    String itemFilter;
    private CursorAdapter cursorAdapter;
    private int termId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.course_list_activity);

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(CourseProvider.CONTENT_ITEM_TYPE);
        termId = Integer.parseInt(uri.getLastPathSegment());

        cursorAdapter = new CourseCursorAdapter(this, null, 0);

        ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);

        list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(CourseListActivity.this, CourseActivity.class);
                        Uri uri = Uri.parse(CourseProvider.CONTENT_URI + "/" + id);
                        intent.putExtra(CourseProvider.CONTENT_ITEM_TYPE, uri);
                        intent.putExtra(getString(R.string.parent_id), termId);
                        startActivityForResult(intent, COURSE_EDITOR_REQUEST_CODE);
                    }
                });

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finishEditing();
                break;
            case R.id.action_create_sample:
                insertSampleData();
                break;
            case R.id.action_delete_all:
                deleteAllRecords();
                break;
        }

        return true;
    }

    private void deleteAllRecords() {

        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            getContentResolver().delete(CourseProvider.CONTENT_URI, itemFilter, null);
                            restartLoader();

                            Toast.makeText(CourseListActivity.this,
                                    getString(R.string.all_courses_deleted),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.are_you_sure))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }

    private void insertSampleData() {
        insertRecord("Course 1");
        insertRecord("Course 2");
        insertRecord("Course 3");
        restartLoader();
    }

    private void insertRecord(String title) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_COURSE_TITLE, title);
        values.put(DBHelper.COLUMN_COURSE_TERM_ID, termId);
        getContentResolver().insert(CourseProvider.CONTENT_URI, values);
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        itemFilter = DBHelper.COLUMN_COURSE_TERM_ID + " = " + termId;
        return new CursorLoader(this, CourseProvider.CONTENT_URI, null, itemFilter, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    public void openEditorForNewRecord(View view) {
        Intent intent = new Intent(this, CourseActivity.class);
        intent.putExtra(getString(R.string.parent_id), termId);
        startActivityForResult(intent, COURSE_EDITOR_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == COURSE_EDITOR_REQUEST_CODE && resultCode == RESULT_OK) {
            restartLoader();
        }
    }

    private void finishEditing() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        finishEditing();
    }
}
