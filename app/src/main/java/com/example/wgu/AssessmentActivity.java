package com.example.wgu;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class AssessmentActivity extends AppCompatActivity {

    private static final int ASSESSMENTLIST_EDITOR_REQUEST_CODE = 1001;
    private String action;
    private EditText titleEditText;
    private EditText dueDateEditText;
    private EditText goalDateEditText;
    private RadioButton objectiveRadioButton;
    private RadioButton performanceRadioButton;
    private String itemFilter;
    private int currentRecordId;
    private int courseId;
    private AssessmentModel oldAssessment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.assessment_activity);

        initializeUIObjects();

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(AssessmentProvider.CONTENT_ITEM_TYPE);
        courseId = intent.getIntExtra(getString(R.string.parent_id), 0);

        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_assessment));
        } else {
            action = Intent.ACTION_EDIT;
            setTitle(getString(R.string.edit_assessment));

            currentRecordId = Integer.parseInt(uri.getLastPathSegment());
            itemFilter = DBHelper.COLUMN_ASSESSMENT_ID + "=" + currentRecordId;

            Cursor cursor = getContentResolver().query(uri, DBHelper.ALL_ASSESSMENT_COLUMNS,
                    itemFilter, null, null);
            cursor.moveToFirst();

            oldAssessment = new AssessmentModel(cursor);

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
        titleEditText = (EditText) findViewById(R.id.assessmentTitleEditText);
        dueDateEditText = (EditText) findViewById(R.id.assessmentDueEditText);
        goalDateEditText = (EditText) findViewById(R.id.assessmentGoalEditText);
        objectiveRadioButton = (RadioButton) findViewById(R.id.assessmentObjectiveRadioButton);
        performanceRadioButton = (RadioButton) findViewById(R.id.assessmentPerformanceRadioButton);

        MyDatePicker.initialize(dueDateEditText);
        MyDatePicker.initialize(goalDateEditText);
    }

    private void populateUIObjects() {
        titleEditText.setText(oldAssessment.getTitle());
        dueDateEditText.setText(oldAssessment.getDueDate());
        goalDateEditText.setText(oldAssessment.getGoalDate());

        objectiveRadioButton.setChecked(oldAssessment.getType().equals("o"));
        performanceRadioButton.setChecked(oldAssessment.getType().equals("p"));
    }

    private void finishEditing() {
        AssessmentModel newAssessment = new AssessmentModel(courseId,
                titleEditText.getText().toString().trim(),
                dueDateEditText.getText().toString().trim(),
                goalDateEditText.getText().toString().trim(),
                objectiveRadioButton.isChecked() ? "o" : performanceRadioButton.isChecked() ? "p" : ""
        );

        switch (action) {
            case Intent.ACTION_INSERT:
                if (newAssessment.getTitle().length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertItem(newAssessment);
                }
                break;
            case Intent.ACTION_EDIT:
                if (newAssessment.getTitle().length() == 0) {
                    titleEditText.setError("Title is required.");
                    return;
                } else if (oldAssessment.equals(newAssessment)) {
                    setResult(RESULT_CANCELED);
                } else {
                    updateItem(newAssessment);
                }
                break;
        }
        finish();
    }

    private void deleteItem() {
        getContentResolver().delete(AssessmentProvider.ASSESSMENT_CONTENT_URI, itemFilter, null);
        Toast.makeText(this, R.string.assessment_deleted, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void updateItem(AssessmentModel assessment) {
        ContentValues values = getContentValuesFromModel(assessment);
        getContentResolver().update(AssessmentProvider.ASSESSMENT_CONTENT_URI, values, itemFilter, null);
        Toast.makeText(this, R.string.assessment_updated, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertItem(AssessmentModel assessment) {
        ContentValues values = getContentValuesFromModel(assessment);
        getContentResolver().insert(AssessmentProvider.ASSESSMENT_CONTENT_URI, values);
        Toast.makeText(this, R.string.assessment_inserted, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private ContentValues getContentValuesFromModel(AssessmentModel assessment) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_ASSESSMENT_COURSE_ID, assessment.getCourseId());
        values.put(DBHelper.COLUMN_ASSESSMENT_TITLE, assessment.getTitle());
        values.put(DBHelper.COLUMN_ASSESSMENT_DUE, assessment.getDueDate());
        values.put(DBHelper.COLUMN_ASSESSMENT_GOAL, assessment.getGoalDate());
        values.put(DBHelper.COLUMN_ASSESSMENT_TYPE, assessment.getType());
        return values;
    }
}
