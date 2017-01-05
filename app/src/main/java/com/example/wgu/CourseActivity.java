package com.example.wgu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class CourseActivity extends AppCompatActivity {

    private static final int ASSESSMENTLIST_EDITOR_REQUEST_CODE = 1001;
    private static final int MENTORLIST_EDITOR_REQUEST_CODE = 1002;
    private static final int CAMERA_REQUEST_CODE = 1003;
    private String action;
    private EditText titleEditText;
    private EditText startDateEditText;
    private EditText endDateEditText;
    private Spinner statusSpinner;
    private EditText noteEditText;
    private ArrayAdapter<CharSequence> statusSpinnerAdapter;
    private String itemFilter;
    private int currentRecordId;
    private int termId;
    private CourseModel oldCourse;
    private int noteWidth;
    private byte[] imageBytes;

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

            termId = oldCourse.getTermId();

            populateUIObjects();

            populateTheNoteImage();

            titleEditText.requestFocus();
        }

        // restore any saved data.  most likely from an orientation change.
        if (savedInstanceState != null) {
            titleEditText.setText(savedInstanceState.getString(DBHelper.COLUMN_COURSE_TITLE));
            startDateEditText.setText(savedInstanceState.getString(DBHelper.COLUMN_COURSE_START));
            endDateEditText.setText(savedInstanceState.getString(DBHelper.COLUMN_COURSE_END));

            if (savedInstanceState.getString(DBHelper.COLUMN_COURSE_STATUS).length() != 0) {
                statusSpinner.setSelection(statusSpinnerAdapter.getPosition(savedInstanceState.getString(DBHelper.COLUMN_COURSE_STATUS)));
            }

            noteEditText.setText(savedInstanceState.getString(DBHelper.COLUMN_COURSE_NOTE));

            if (savedInstanceState.getByteArray(DBHelper.COLUMN_COURSE_IMAGE) != null) {
                imageBytes = savedInstanceState.getByteArray(DBHelper.COLUMN_COURSE_IMAGE);
            }

            populateTheNoteImage();
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // persist the current data across orientation changes.
        outState.putInt(DBHelper.COLUMN_COURSE_ID, currentRecordId);
        outState.putInt(DBHelper.COLUMN_COURSE_TERM_ID, termId);
        outState.putString(DBHelper.COLUMN_COURSE_TITLE, titleEditText.getText().toString().trim());
        outState.putString(DBHelper.COLUMN_COURSE_START, startDateEditText.getText().toString().trim());
        outState.putString(DBHelper.COLUMN_COURSE_END, endDateEditText.getText().toString().trim());
        outState.putString(DBHelper.COLUMN_COURSE_STATUS, statusSpinner.getItemAtPosition(statusSpinner.getSelectedItemPosition()).toString());
        outState.putString(DBHelper.COLUMN_COURSE_NOTE, noteEditText.getText().toString().trim());
        outState.putByteArray(DBHelper.COLUMN_COURSE_IMAGE, imageBytes);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // get the Bitmap from the camera
            Bitmap photoBitmap = (Bitmap) data.getExtras().get("data");
            // ensure the noteWidth is set
            if (noteWidth == 0) {
                noteWidth = noteEditText.getMeasuredWidth();
            }
            // add the Bitmap to the Note control
            insertImageIntoEditText(photoBitmap);
            // store the Bitmap
            imageBytes = BitmapHelper.getBytesFromBitmap(photoBitmap);
        }
    }

    public void courseCameraButton_onClick(View view) {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    public void assessmentListButton_onClick(View view) {
        openChildList(CourseActivity.this,
                AssessmentListActivity.class,
                AssessmentProvider.CONTENT_URI,
                AssessmentProvider.CONTENT_ITEM_TYPE,
                ASSESSMENTLIST_EDITOR_REQUEST_CODE);
    }

    public void mentorListButton_onClick(View view) {
        openChildList(CourseActivity.this,
                MentorListActivity.class,
                MentorProvider.CONTENT_URI,
                MentorProvider.CONTENT_ITEM_TYPE,
                MENTORLIST_EDITOR_REQUEST_CODE);
    }

    public void courseShareButton_onClick(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String subject = titleEditText.getText().toString().trim();
        String body = titleEditText.getText().toString().trim() + ":\n" + noteEditText.getText().toString().trim();
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(android.content.Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(intent, "Share notes:"));
    }

    private void openChildList(Context context, Class targetClass, Uri targetUri, String targetItemType, int requestCode) {
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

    private void populateTheNoteImage() {
        // when the screen is initially rendering, the width of the EditText isn't available
        // so set a listener on the preDraw event.
        ViewTreeObserver noteViewTree = noteEditText.getViewTreeObserver();
        noteViewTree.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                if (noteWidth == 0) {
                    noteWidth = noteEditText.getMeasuredWidth();
                    if (imageBytes != null) {
                        insertImageIntoEditText(BitmapHelper.getBitMapFromBytes(imageBytes));
                    }
                }
                return true;
            }
        });
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
        noteEditText = (EditText) findViewById(R.id.courseNoteEditText);

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

        noteEditText.setText(oldCourse.getNote());

        if (oldCourse.getImage() != null) {
            imageBytes = oldCourse.getImage();
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
                statusSpinner.getItemAtPosition(statusSpinner.getSelectedItemPosition()).toString(),
                noteEditText.getText().toString().trim(),
                imageBytes
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
        values.put(DBHelper.COLUMN_COURSE_NOTE, course.getNote());
        values.put(DBHelper.COLUMN_COURSE_IMAGE, course.getImage());
        return values;
    }

    private void insertImageIntoEditText(Bitmap photoBitmap) {
        // convert the Bitmap to a Drawable
        Drawable photoDrawable = new BitmapDrawable(getResources(), photoBitmap);
        // find the Note control
        EditText et = (EditText) findViewById(R.id.courseNoteEditText);
        // calculate the proper scale to make the image half the width of the note control
        float scale = ((float) noteWidth / 2 / photoDrawable.getIntrinsicWidth());
        // set the bounds of the Drawable
        photoDrawable.setBounds(0, 0, (int) (scale * photoDrawable.getIntrinsicWidth()), (int) (scale * photoDrawable.getIntrinsicHeight()));
        // add the image the the Note control
        et.setCompoundDrawables(null, photoDrawable, null, null);
    }
}
