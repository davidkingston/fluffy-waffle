package com.example.wgu;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
    private static final int MENTORLIST_EDITOR_REQUEST_CODE = 1002;
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

        setContentView(R.layout.course_activity);

        initializeUIObjects();

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(CourseProvider.CONTENT_ITEM_TYPE);
        termId = intent.getIntExtra(getString(R.string.parent_id), 0);

        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_course));
        } else {
            currentRecordId = Integer.parseInt(uri.getLastPathSegment());
            initializeActivityPropertiesForEdit();

            initializeModel(uri);

            populateUIObjects();

            titleEditText.requestFocus();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (action.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.menu_delete, menu);
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

    private void initializeModel(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, DBHelper.ALL_COURSE_COLUMNS,
                itemFilter, null, null);
        cursor.moveToFirst();

        oldCourse = new CourseModel(cursor);
    }

    private boolean hasChildren() {
        String assessmentFilter = DBHelper.COLUMN_ASSESSMENT_COURSE_ID + " = " + currentRecordId;
        Cursor assessmentCursor = getContentResolver().query(AssessmentProvider.CONTENT_URI, DBHelper.ALL_ASSESSMENT_COLUMNS,
                assessmentFilter, null, null);
        if (assessmentCursor.getCount() > 0) {
            return true;
        }

        String mentorFilter = DBHelper.COLUMN_MENTOR_COURSE_ID + " = " + currentRecordId;
        Cursor mentorCursor = getContentResolver().query(MentorProvider.CONTENT_URI, DBHelper.ALL_MENTOR_COLUMNS,
                mentorFilter, null, null);


        return (mentorCursor.getCount() > 0);
    }

    private void initializeActivityPropertiesForEdit() {
        action = Intent.ACTION_EDIT;
        setTitle(getString(R.string.edit_course));
        itemFilter = DBHelper.COLUMN_COURSE_ID + "=" + currentRecordId;
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
        CourseModel newCourse = getCourseModel();

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
                } else if (oldCourse != null && oldCourse.equals(newCourse)) {
                    setResult(RESULT_CANCELED);
                } else {
                    updateItem(newCourse);
                }
                break;
        }
        finish();
    }

    private CourseModel getCourseModel() {
        return new CourseModel(termId,
                titleEditText.getText().toString().trim(),
                startDateEditText.getText().toString().trim(),
                endDateEditText.getText().toString().trim(),
                statusSpinner.getItemAtPosition(statusSpinner.getSelectedItemPosition()).toString()
        );
    }

    private void deleteItem() {
        if (hasChildren()) {
            Snackbar.make(findViewById(R.id.courseActivity),
                    "This Course cannot be deleted while it has associated Assessments or Mentors.",
                    Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            getContentResolver().delete(CourseProvider.CONTENT_URI, itemFilter, null);
                            Toast.makeText(CourseActivity.this, R.string.course_deleted, Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                };

        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.are_you_sure))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }

    private void updateItem(CourseModel course) {
        ContentValues values = getContentValuesFromModel(course);
        getContentResolver().update(CourseProvider.CONTENT_URI, values, itemFilter, null);
        Toast.makeText(this, R.string.course_updated, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private int insertItem(CourseModel course) {
        ContentValues values = getContentValuesFromModel(course);
        Uri uri = getContentResolver().insert(CourseProvider.CONTENT_URI, values);
        int newId = Integer.parseInt(uri.getLastPathSegment());
        Toast.makeText(this, R.string.course_inserted, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        return newId;
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
        openChildList(CourseActivity.this,
                AssessmentListActivity.class,
                AssessmentProvider.CONTENT_URI,
                AssessmentProvider.CONTENT_ITEM_TYPE,
                ASSESSMENTLIST_EDITOR_REQUEST_CODE);
    }

    public void openMentorList(View view) {
        openChildList(CourseActivity.this,
                MentorListActivity.class,
                MentorProvider.CONTENT_URI,
                MentorProvider.CONTENT_ITEM_TYPE,
                MENTORLIST_EDITOR_REQUEST_CODE);
    }

    public void openChildList(Context context, Class targetClass, Uri targetUri, String targetItemType, int requestCode) {
        CourseModel newCourse = getCourseModel();

        if (newCourse.getTitle().length() == 0) {
            titleEditText.setError("Title is required.");
            return;
        }
        switch (action) {
            case Intent.ACTION_INSERT:
                currentRecordId = insertItem(newCourse);
                initializeActivityPropertiesForEdit();
                invalidateOptionsMenu();
                break;
            case Intent.ACTION_EDIT:
                if (oldCourse != null && !oldCourse.equals(newCourse)) {
                    updateItem(newCourse);
                }
                break;
        }

        Intent intent = new Intent(context, targetClass);
        Uri uri = Uri.parse(targetUri + "/c/" + currentRecordId);
        intent.putExtra(targetItemType, uri);
        startActivityForResult(intent, requestCode);
    }
}
