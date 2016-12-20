package com.example.wgu;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class CourseActivity extends AppCompatActivity {

    private static final int ASSESSMENTLIST_EDITOR_REQUEST_CODE = 1001;
    private String action;
    private EditText titleEditText;
    private EditText startDateEditText;
    private EditText endDateEditText;
    private Spinner statusSpinner;
    private ArrayAdapter<CharSequence> statusSpinnerAdapter;
    private String itemFilter;
    private int currentRecordId;
    private int termId;
    private CourseModel oldCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_course);

        initializeUIObjects();

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(CourseProvider.CONTENT_ITEM_TYPE);
        termId = intent.getIntExtra(getString(R.string.parent_id), 0);

        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_course));
        } else {
            action = Intent.ACTION_EDIT;
            setTitle(getString(R.string.edit_course));

            currentRecordId = Integer.parseInt(uri.getLastPathSegment());
            itemFilter = DBHelper.COLUMN_COURSE_ID + "=" + currentRecordId;

            Cursor cursor = getContentResolver().query(uri, DBHelper.ALL_COURSE_COLUMNS,
                    itemFilter, null, null);
            cursor.moveToFirst();

            oldCourse = new CourseModel(cursor);

            populateUIObjects();

            titleEditText.requestFocus();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (action.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.menu_term, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finishEditing();
                break;
            case R.id.action_delete_term:
                deleteItem();
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        finishEditing();
    }

    private void initializeUIObjects() {
        titleEditText = (EditText) findViewById(R.id.courseTitleEditText);
        startDateEditText = (EditText) findViewById(R.id.courseStartEditText);
        endDateEditText = (EditText) findViewById(R.id.courseEndEditText);
        statusSpinner = (Spinner) findViewById(R.id.courseStatusSpinner);

        statusSpinnerAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.course_status,
                android.R.layout.simple_spinner_item
        );
        statusSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        statusSpinner.setAdapter(statusSpinnerAdapter);

        MyDatePicker.initialize(startDateEditText);
        MyDatePicker.initialize(endDateEditText);
    }

    private void populateUIObjects() {
        titleEditText.setText(oldCourse.getTitle());
        startDateEditText.setText(oldCourse.getStartDate());
        endDateEditText.setText(oldCourse.getEndDate());

        if (oldCourse.getStatus().length() != 0) {
            statusSpinner.setSelection(statusSpinnerAdapter.getPosition(oldCourse.getStatus()));
        }
    }

    private void finishEditing() {
        CourseModel newCourse = new CourseModel(termId,
                titleEditText.getText().toString().trim(),
                startDateEditText.getText().toString().trim(),
                endDateEditText.getText().toString().trim(),
                statusSpinner.getItemAtPosition(statusSpinner.getSelectedItemPosition()).toString()
        );

        switch (action) {
            case Intent.ACTION_INSERT:
                if (newCourse.getTitle().length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertItem(newCourse);
                }
                break;
            case Intent.ACTION_EDIT:
                if (newCourse.getTitle().length() == 0) {
                    titleEditText.setError("Title is required.");
                    return;
                } else if (oldCourse.equals(newCourse)) {
                    setResult(RESULT_CANCELED);
                } else {
                    updateItem(newCourse);
                }
                break;
        }
        finish();
    }

    private void deleteItem() {
        getContentResolver().delete(CourseProvider.COURSE_CONTENT_URI, itemFilter, null);
        Toast.makeText(this, R.string.course_deleted, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void updateItem(CourseModel course) {
        ContentValues values = getContentValuesFromModel(course);
        getContentResolver().update(CourseProvider.COURSE_CONTENT_URI, values, itemFilter, null);
        Toast.makeText(this, R.string.course_updated, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertItem(CourseModel course) {
        ContentValues values = getContentValuesFromModel(course);
        getContentResolver().insert(CourseProvider.COURSE_CONTENT_URI, values);
        Toast.makeText(this, R.string.course_inserted, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private ContentValues getContentValuesFromModel(CourseModel course) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_COURSE_TERM_ID, course.getTermId());
        values.put(DBHelper.COLUMN_COURSE_TITLE, course.getTitle());
        values.put(DBHelper.COLUMN_COURSE_START, course.getStartDate());
        values.put(DBHelper.COLUMN_COURSE_END, course.getEndDate());
        values.put(DBHelper.COLUMN_COURSE_STATUS, course.getStatus());
        return values;
    }


    public void openAssessmentList(View view) {
        Intent intent = new Intent(CourseActivity.this, AssessmentListActivity.class);
        Uri uri = Uri.parse(AssessmentProvider.ASSESSMENT_CONTENT_URI + "/c/" + currentRecordId);
        intent.putExtra(AssessmentProvider.CONTENT_ITEM_TYPE, uri);
        startActivityForResult(intent, ASSESSMENTLIST_EDITOR_REQUEST_CODE);
    }
}
