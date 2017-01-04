package com.example.wgu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class AssessmentActivity extends AppCompatActivity {

    private static final int ASSESSMENTLIST_EDITOR_REQUEST_CODE = 1001;
    private static final int CAMERA_REQUEST_CODE = 1003;
    private String action;
    private EditText titleEditText;
    private EditText dueDateEditText;
    private EditText goalDateEditText;
    private RadioButton objectiveRadioButton;
    private RadioButton performanceRadioButton;
    private EditText noteEditText;
    private String itemFilter;
    private int currentRecordId;
    private int courseId;
    private AssessmentModel oldAssessment;
    private int noteWidth;
    private byte[] imageBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.assessment_activity);

        initializeUIObjects();

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(AssessmentProvider.CONTENT_ITEM_TYPE);

        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_assessment));
            courseId = intent.getIntExtra(getString(R.string.parent_id), 0);
        } else {
            action = Intent.ACTION_EDIT;
            setTitle(getString(R.string.edit_assessment));

            currentRecordId = Integer.parseInt(uri.getLastPathSegment());
            itemFilter = DBHelper.COLUMN_ASSESSMENT_ID + "=" + currentRecordId;

            Cursor cursor = getContentResolver().query(uri, DBHelper.ALL_ASSESSMENT_COLUMNS,
                    itemFilter, null, null);
            cursor.moveToFirst();

            oldAssessment = new AssessmentModel(cursor);
            courseId = oldAssessment.getCourseId();

            populateUIObjects();

            populateTheNoteImage();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // get the Bitmap from the camera
            Bitmap photoBitmap = (Bitmap) data.getExtras().get("data");
            // add the Bitmap to the Note control
            insertImageIntoEditText(photoBitmap);
            // store the Bitmap
            imageBytes = BitmapHelper.getBytesFromBitmap(photoBitmap);
        }
    }

    public void assessmentCameraButton_onClick(View view) {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    private void initializeUIObjects() {
        titleEditText = (EditText) findViewById(R.id.assessmentTitleEditText);
        dueDateEditText = (EditText) findViewById(R.id.assessmentDueEditText);
        goalDateEditText = (EditText) findViewById(R.id.assessmentGoalEditText);
        objectiveRadioButton = (RadioButton) findViewById(R.id.assessmentObjectiveRadioButton);
        performanceRadioButton = (RadioButton) findViewById(R.id.assessmentPerformanceRadioButton);
        noteEditText = (EditText) findViewById(R.id.assessmentNoteEditText);

        MyDatePicker.initialize(dueDateEditText);
        MyDatePicker.initialize(goalDateEditText);
    }

    private void populateUIObjects() {
        titleEditText.setText(oldAssessment.getTitle());
        dueDateEditText.setText(oldAssessment.getDueDate());
        goalDateEditText.setText(oldAssessment.getGoalDate());

        objectiveRadioButton.setChecked(oldAssessment.getType().equals("o"));
        performanceRadioButton.setChecked(oldAssessment.getType().equals("p"));

        noteEditText.setText(oldAssessment.getNote());

        if (oldAssessment.getImage() != null) {
            imageBytes = oldAssessment.getImage();
        }
    }

    private void populateTheNoteImage() {
        // when the screen is initially rendering, the width of the EditText isn't available
        // so set a listener on the preDraw event.
        ViewTreeObserver noteViewTree = noteEditText.getViewTreeObserver();
        noteViewTree.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                noteWidth = noteEditText.getMeasuredWidth();
                if (oldAssessment.getImage() != null) {
                    insertImageIntoEditText(BitmapHelper.getBitMapFromBytes(oldAssessment.getImage()));
                }
                return true;
            }
        });
    }

    private void finishEditing() {
        AssessmentModel newAssessment = getAssessmentModel();

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

    private AssessmentModel getAssessmentModel() {
        return new AssessmentModel(courseId,
                titleEditText.getText().toString().trim(),
                dueDateEditText.getText().toString().trim(),
                goalDateEditText.getText().toString().trim(),
                objectiveRadioButton.isChecked() ? "o" : performanceRadioButton.isChecked() ? "p" : "",
                noteEditText.getText().toString().trim(),
                imageBytes
        );
    }

    private void deleteItem() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            getContentResolver().delete(AssessmentProvider.CONTENT_URI, itemFilter, null);
                            Toast.makeText(AssessmentActivity.this, R.string.assessment_deleted, Toast.LENGTH_SHORT).show();
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

    private void updateItem(AssessmentModel assessment) {
        ContentValues values = getContentValuesFromModel(assessment);
        getContentResolver().update(AssessmentProvider.CONTENT_URI, values, itemFilter, null);
        Toast.makeText(this, R.string.assessment_updated, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertItem(AssessmentModel assessment) {
        ContentValues values = getContentValuesFromModel(assessment);
        getContentResolver().insert(AssessmentProvider.CONTENT_URI, values);
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
        values.put(DBHelper.COLUMN_ASSESSMENT_NOTE, assessment.getNote());
        values.put(DBHelper.COLUMN_ASSESSMENT_IMAGE, assessment.getImage());
        return values;
    }

    private void insertImageIntoEditText(Bitmap photoBitmap) {
        // convert the Bitmap to a Drawable
        Drawable photoDrawable = new BitmapDrawable(getResources(), photoBitmap);
        // find the Note control
        EditText et = (EditText) findViewById(R.id.assessmentNoteEditText);
        // calculate the proper scale to make the image half the width of the note control
        float scale = ((float) noteWidth / 2 / photoDrawable.getIntrinsicWidth());
        // set the bounds of the Drawable
        photoDrawable.setBounds(0, 0, (int) (scale * photoDrawable.getIntrinsicWidth()), (int) (scale * photoDrawable.getIntrinsicHeight()));
        // add the image the the Note control
        et.setCompoundDrawables(null, photoDrawable, null, null);
    }
}
