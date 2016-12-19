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
import android.widget.ListView;
import android.widget.Toast;

public class AssessmentListActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ASSESSMENT_EDITOR_REQUEST_CODE = 1001;
    String itemFilter;
    private TermCursorAdapter termAdapter;
    private int courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_assessment_list);

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(AssessmentProvider.CONTENT_ITEM_TYPE);
        courseId = Integer.parseInt(uri.getLastPathSegment());

        termAdapter = new TermCursorAdapter(this, null, 0);

        ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(termAdapter);

        list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        Intent intent = new Intent(AssessmentListActivity.this, AssessmentActivity.class);
//                        Uri uri = Uri.parse(AssessmentProvider.ASSESSMENT_CONTENT_URI + "/" + id);
//                        intent.putExtra(AssessmentProvider.CONTENT_ITEM_TYPE, uri);
//                        intent.putExtra(getString(R.string.parent_id), courseId);
//                        startActivityForResult(intent, ASSESSMENT_EDITOR_REQUEST_CODE);
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
                            getContentResolver().delete(AssessmentProvider.ASSESSMENT_CONTENT_URI, itemFilter, null);
                            restartLoader();

                            Toast.makeText(AssessmentListActivity.this,
                                    getString(R.string.all_assessments_deleted),
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
        insertRecord("Assessment 1");
        insertRecord("Assessment 2");
        insertRecord("Assessment 3");
        restartLoader();
    }

    private void insertRecord(String title) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_ASSESSMENT_TITLE, title);
        values.put(DBHelper.COLUMN_ASSESSMENT_COURSE_ID, courseId);
        Uri uri = getContentResolver().insert(AssessmentProvider.ASSESSMENT_CONTENT_URI, values);
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        itemFilter = DBHelper.COLUMN_ASSESSMENT_COURSE_ID + " = " + courseId;
        Uri uri = AssessmentProvider.ASSESSMENT_CONTENT_URI
                .buildUpon()
                .appendPath("t")
                .appendPath(Integer.toString(courseId))
                .build();
        return new CursorLoader(this, uri, null, itemFilter, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        termAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        termAdapter.swapCursor(null);
    }

    public void openEditorForNewRecord(View view) {
//        Intent intent = new Intent(this, AssessmentActivity.class);
//        intent.putExtra(getString(R.string.parent_id), courseId);
//        startActivityForResult(intent, ASSESSMENT_EDITOR_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ASSESSMENT_EDITOR_REQUEST_CODE && resultCode == RESULT_OK) {
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
