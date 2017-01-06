package com.example.wgu;

import android.app.AlertDialog;
import android.content.ContentValues;
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
import android.widget.EditText;
import android.widget.Toast;

public class TermActivity extends AppCompatActivity {

    private static final int COURSELIST_EDITOR_REQUEST_CODE = 1001;
    private String action;
    private EditText titleEditText;
    private EditText startDateEditText;
    private EditText endDateEditText;
    private String itemFilter;
    private int currentRecordId;
    private TermModel oldTerm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.term_activity);

        initializeUIObjects();

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(TermProvider.CONTENT_ITEM_TYPE);

        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_term));
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
            case R.id.action_delete_record:
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
        Cursor cursor = getContentResolver().query(uri, DBHelper.ALL_TERM_COLUMNS,
                itemFilter, null, null);
        cursor.moveToFirst();

        oldTerm = new TermModel(cursor);
    }

    private boolean hasChildren() {
        String childFilter = DBHelper.COLUMN_COURSE_TERM_ID + " = " + currentRecordId;
        Cursor cursor = getContentResolver().query(CourseProvider.CONTENT_URI, DBHelper.ALL_COURSE_COLUMNS,
                childFilter, null, null);
        return (cursor.getCount() > 0);
    }

    private void initializeActivityPropertiesForEdit() {
        action = Intent.ACTION_EDIT;
        setTitle(getString(R.string.edit_term));
        itemFilter = DBHelper.COLUMN_TERM_ID + "=" + currentRecordId;
    }

    private void initializeUIObjects() {
        titleEditText = (EditText) findViewById(R.id.termTitleEditText);
        startDateEditText = (EditText) findViewById(R.id.termStartEditText);
        endDateEditText = (EditText) findViewById(R.id.termEndEditText);

        MyDatePicker.initialize(startDateEditText);
        MyDatePicker.initialize(endDateEditText);
    }

    private void populateUIObjects() {
        titleEditText.setText(oldTerm.getTitle());
        startDateEditText.setText(oldTerm.getStartDate());
        endDateEditText.setText(oldTerm.getEndDate());
    }

    private void finishEditing() {
        TermModel newTerm = getTermModel();

        switch (action) {
            case Intent.ACTION_INSERT:
                if (newTerm.getTitle().length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertItem(newTerm);
                }
                break;
            case Intent.ACTION_EDIT:
                if (newTerm.isEmpty()) {
                    deleteItem();
                } else if (oldTerm != null && oldTerm.equals(newTerm)) {
                    setResult(RESULT_CANCELED);
                } else {
                    updateItem(newTerm);
                }
                break;
        }

        finish();
    }

    private TermModel getTermModel() {
        return new TermModel(
                titleEditText.getText().toString().trim(),
                startDateEditText.getText().toString().trim(),
                endDateEditText.getText().toString().trim()
        );
    }

    private void deleteItem() {
        if (hasChildren()) {
            Snackbar.make(findViewById(R.id.termActivity),
                    "This Term cannot be deleted while it has associated Courses.",
                    Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            getContentResolver().delete(TermProvider.CONTENT_URI, itemFilter, null);
                            Toast.makeText(TermActivity.this, R.string.term_deleted, Toast.LENGTH_SHORT).show();
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

    private void updateItem(TermModel term) {
        ContentValues values = getContentValuesFromModel(term);
        getContentResolver().update(TermProvider.CONTENT_URI, values, itemFilter, null);
        Toast.makeText(this, R.string.term_updated, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private int insertItem(TermModel term) {
        ContentValues values = getContentValuesFromModel(term);
        Uri uri = getContentResolver().insert(TermProvider.CONTENT_URI, values);
        int newId = Integer.parseInt(uri.getLastPathSegment());
        Toast.makeText(this, R.string.term_inserted, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        return newId;
    }

    private ContentValues getContentValuesFromModel(TermModel term) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_TERM_TITLE, term.getTitle());
        values.put(DBHelper.COLUMN_TERM_START, term.getStartDate());
        values.put(DBHelper.COLUMN_TERM_END, term.getEndDate());
        return values;
    }

    public void openCourseList(View view) {
        TermModel newTerm = getTermModel();

        if (newTerm.getTitle().length() == 0) {
            titleEditText.setError("Title is required.");
            return;
        }
        switch (action) {
            case Intent.ACTION_INSERT:
                currentRecordId = insertItem(newTerm);
                initializeActivityPropertiesForEdit();
                invalidateOptionsMenu();
                break;
            case Intent.ACTION_EDIT:
                if (oldTerm != null && !oldTerm.equals(newTerm)) {
                    updateItem(newTerm);
                }
                break;
        }

        Intent intent = new Intent(TermActivity.this, CourseListActivity.class);
        Uri uri = Uri.parse(CourseProvider.CONTENT_URI + "/t/" + currentRecordId);
        intent.putExtra(CourseProvider.CONTENT_ITEM_TYPE, uri);
        startActivityForResult(intent, COURSELIST_EDITOR_REQUEST_CODE);
    }

}
