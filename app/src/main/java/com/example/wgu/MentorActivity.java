package com.example.wgu;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class MentorActivity extends AppCompatActivity {

    private String action;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private String itemFilter;
    private int currentRecordId;
    private int courseId;
    private MentorModel oldMentor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mentor_activity);

        initializeUIObjects();

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(MentorProvider.CONTENT_ITEM_TYPE);
        courseId = intent.getIntExtra(getString(R.string.parent_id), 0);

        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_mentor));
        } else {
            action = Intent.ACTION_EDIT;
            setTitle(getString(R.string.edit_mentor));

            currentRecordId = Integer.parseInt(uri.getLastPathSegment());
            itemFilter = DBHelper.COLUMN_MENTOR_ID + "=" + currentRecordId;

            Cursor cursor = getContentResolver().query(uri, DBHelper.ALL_MENTOR_COLUMNS,
                    itemFilter, null, null);
            cursor.moveToFirst();

            oldMentor = new MentorModel(cursor);

            populateUIObjects();

            nameEditText.requestFocus();
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

    private void initializeUIObjects() {
        nameEditText = (EditText) findViewById(R.id.mentorNameEditText);
        emailEditText = (EditText) findViewById(R.id.mentorEmailEditText);
        phoneEditText = (EditText) findViewById(R.id.mentorPhoneEditText);
    }

    private void populateUIObjects() {
        nameEditText.setText(oldMentor.getName());
        emailEditText.setText(oldMentor.getEmail());
        phoneEditText.setText(oldMentor.getPhone());
    }

    private void finishEditing() {
        MentorModel newMentor = new MentorModel(courseId,
                nameEditText.getText().toString().trim(),
                emailEditText.getText().toString().trim(),
                phoneEditText.getText().toString().trim()
        );

        switch (action) {
            case Intent.ACTION_INSERT:
                if (newMentor.getName().length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertItem(newMentor);
                }
                break;
            case Intent.ACTION_EDIT:
                if (newMentor.getName().length() == 0) {
                    nameEditText.setError("Name is required.");
                    return;
                } else if (oldMentor.equals(newMentor)) {
                    setResult(RESULT_CANCELED);
                } else {
                    updateItem(newMentor);
                }
                break;
        }
        finish();
    }

    private void deleteItem() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            getContentResolver().delete(MentorProvider.CONTENT_URI, itemFilter, null);
                            Toast.makeText(MentorActivity.this, R.string.mentor_deleted, Toast.LENGTH_SHORT).show();
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

    private void updateItem(MentorModel mentor) {
        ContentValues values = getContentValuesFromModel(mentor);
        getContentResolver().update(MentorProvider.CONTENT_URI, values, itemFilter, null);
        Toast.makeText(this, R.string.mentor_updated, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertItem(MentorModel mentor) {
        ContentValues values = getContentValuesFromModel(mentor);
        getContentResolver().insert(MentorProvider.CONTENT_URI, values);
        Toast.makeText(this, R.string.mentor_inserted, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private ContentValues getContentValuesFromModel(MentorModel mentor) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_MENTOR_COURSE_ID, mentor.getCourseId());
        values.put(DBHelper.COLUMN_MENTOR_NAME, mentor.getName());
        values.put(DBHelper.COLUMN_MENTOR_EMAIL, mentor.getEmail());
        values.put(DBHelper.COLUMN_MENTOR_PHONE, mentor.getPhone());
        return values;
    }
}
