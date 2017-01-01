package com.example.wgu;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int TERMLIST_EDITOR_REQUEST_CODE = 1001;
    private static final int ASSESSMENT_EDITOR_REQUEST_CODE = 1002;
    private static final int COURSESTART_EDITOR_REQUEST_CODE = 1003;
    private static final int COURSEEND_EDITOR_REQUEST_CODE = 1004;

    private CursorAdapter assessmentCursorAdapter;
    private CursorAdapter coursesStartingCursorAdapter;
    private CursorAdapter coursesEndingCursorAdapter;

    private int toDoWindowId = 0;
    private int temporaryToDoWindowId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.mainActivityToolbar);
        setSupportActionBar(toolbar);

        // retrieve the To Do configuration from SharedPreferences.
        SharedPreferences settings = getSharedPreferences("settings", 0);
        toDoWindowId = settings.getInt(getString(R.string.to_do_window_id), 0);


        initializeAssessmentList();
        initializeCourseStartingList();
        initializeCourseEndingList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_todo_config, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.configure_todo_window:
                promptUserForToDoWindow();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // set up the from and to dates
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        Date fromDate = new Date();
        String fromString = sdf.format(fromDate);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, convertIdToWindow(toDoWindowId));
        Date toDate = c.getTime();
        String toString = sdf.format(toDate);

        // create the SQL string and initialize the Loader
        String itemFilter;

        switch (i) {
            case 0:
                itemFilter = String.format("%1$s >= '%2$s' AND %1$s <= '%3$s'",
                        DBHelper.COLUMN_ASSESSMENT_GOAL, fromString, toString);

                return new CursorLoader(this, AssessmentProvider.CONTENT_URI, null, itemFilter, null, null);
            case 1:
                itemFilter = String.format("%1$s >= '%2$s' AND %1$s <= '%3$s'",
                        DBHelper.COLUMN_COURSE_START, fromString, toString);

                return new CursorLoader(this, CourseProvider.CONTENT_URI, null, itemFilter, null, null);
            case 2:
                itemFilter = String.format("%1$s >= '%2$s' AND %1$s <= '%3$s'",
                        DBHelper.COLUMN_COURSE_END, fromString, toString);

                return new CursorLoader(this, CourseProvider.CONTENT_URI, null, itemFilter, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // replace the cursor in the appropriate CursorAdapter
        // update the appropriate TextView
        switch (loader.getId()) {
            case 0:
                assessmentCursorAdapter.swapCursor(cursor);
                setAssessmentMessage(cursor.getCount());
                break;
            case 1:
                coursesStartingCursorAdapter.swapCursor(cursor);
                setCoursesStartingMessage(cursor.getCount());
                break;
            case 2:
                coursesEndingCursorAdapter.swapCursor(cursor);
                setCoursesEndingMessage(cursor.getCount());
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // clear the appropriate CursorAdapter
        switch (loader.getId()) {
            case 0:
                assessmentCursorAdapter.swapCursor(null);
                break;
            case 1:
                coursesStartingCursorAdapter.swapCursor(null);
                break;
            case 2:
                coursesEndingCursorAdapter.swapCursor(null);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        restartLoader();
    }

    public void openTermList(View view) {
        Intent intent = new Intent(MainActivity.this, TermListActivity.class);
        intent.putExtra(AssessmentProvider.CONTENT_ITEM_TYPE, TermProvider.CONTENT_URI);
        startActivityForResult(intent, TERMLIST_EDITOR_REQUEST_CODE);
    }

    private void initializeAssessmentList() {
        assessmentCursorAdapter = new AssessmentCursorAdapter(this, null, 0);

        ListView list = (ListView) findViewById(R.id.mainAssessmentsListView);
        list.setAdapter(assessmentCursorAdapter);

        list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(MainActivity.this, AssessmentActivity.class);
                        Uri uri = Uri.parse(AssessmentProvider.CONTENT_URI + "/" + id);
                        intent.putExtra(AssessmentProvider.CONTENT_ITEM_TYPE, uri);
                        startActivityForResult(intent, ASSESSMENT_EDITOR_REQUEST_CODE);
                    }
                });

        getLoaderManager().initLoader(0, null, this);
    }

    private void initializeCourseStartingList() {
        coursesStartingCursorAdapter = new CourseCursorAdapter(this, null, 0);

        ListView list = (ListView) findViewById(R.id.mainCoursesStartingListView);
        list.setAdapter(coursesStartingCursorAdapter);

        list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(MainActivity.this, CourseActivity.class);
                        Uri uri = Uri.parse(CourseProvider.CONTENT_URI + "/" + id);
                        intent.putExtra(CourseProvider.CONTENT_ITEM_TYPE, uri);
                        startActivityForResult(intent, COURSESTART_EDITOR_REQUEST_CODE);
                    }
                });

        getLoaderManager().initLoader(1, null, this);
    }

    private void initializeCourseEndingList() {
        coursesEndingCursorAdapter = new CourseCursorAdapter(this, null, 0);

        ListView list = (ListView) findViewById(R.id.mainCoursesEndingListView);
        list.setAdapter(coursesEndingCursorAdapter);

        list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(MainActivity.this, CourseActivity.class);
                        Uri uri = Uri.parse(CourseProvider.CONTENT_URI + "/" + id);
                        intent.putExtra(CourseProvider.CONTENT_ITEM_TYPE, uri);
                        startActivityForResult(intent, COURSEEND_EDITOR_REQUEST_CODE);
                    }
                });

        getLoaderManager().initLoader(2, null, this);
    }

    private void promptUserForToDoWindow() {
        // display a dialog with a list of radio buttons
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle("Select the window for the To Do list:")
                .setSingleChoiceItems(R.array.to_do_window, toDoWindowId,
                        new DialogInterface.OnClickListener() {
                            // store the selected option in a temporary location
                            public void onClick(DialogInterface dialog, int id) {
                                temporaryToDoWindowId = id;
                            }
                        })
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // store the selected option in SharedPreferences
                        toDoWindowId = temporaryToDoWindowId;
                        SharedPreferences settings = getSharedPreferences("settings", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putInt(getString(R.string.to_do_window_id), toDoWindowId);
                        editor.apply();

                        // update the list
                        restartLoader();
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // discard any selected option
                        dialog.cancel();
                    }
                })
                .create().show();
    }

    private void restartLoader() {
        // just reload all three lists
        getLoaderManager().restartLoader(0, null, this);
        getLoaderManager().restartLoader(1, null, this);
        getLoaderManager().restartLoader(2, null, this);
    }

    private void setAssessmentMessage(int count) {
        TextView v = (TextView) findViewById(R.id.mainAssessmentsDueTextView);
        v.setText(getResources().getQuantityString(R.plurals.numberOfAssessmentsDue, count, count));
    }

    private void setCoursesStartingMessage(int count) {
        TextView v = (TextView) findViewById(R.id.mainCoursesStartingTextView);
        v.setText(getResources().getQuantityString(R.plurals.numberOfCoursesStarting, count, count));
    }

    private void setCoursesEndingMessage(int count) {
        TextView v = (TextView) findViewById(R.id.mainCoursesEndingTextView);
        v.setText(getResources().getQuantityString(R.plurals.numberOfCoursesEnding, count, count));
    }

    private int convertIdToWindow(int id) {
        int window;
        switch (id) {
            default:
                window = 0;
                break;
            case 1:
                window = 3;
                break;
            case 2:
                window = 7;
                break;
            case 3:
                window = 30;
                break;
        }
        return window;
    }

}
