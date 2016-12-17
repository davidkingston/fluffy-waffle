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
import android.widget.Toast;

public class TermActivity extends AppCompatActivity {

    private String action;
    private EditText titleEditText;
    private EditText startDateEditText;
    private EditText endDateEditText;

    private String itemFilter;

    private TermModel oldTerm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_term);

        initializeUIObjects();

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(TermProvider.CONTENT_ITEM_TYPE);

        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_term));
        } else {
            action = Intent.ACTION_EDIT;
            setTitle(getString(R.string.edit_term));

            itemFilter = TermDBHelper.COLUMN_NAME_ID + "=" + uri.getLastPathSegment();

            Cursor cursor = getContentResolver().query(uri, TermDBHelper.ALL_COLUMNS,
                    itemFilter, null, null);
            cursor.moveToFirst();

            oldTerm = new TermModel(cursor);

            populateUIObjects();

            titleEditText.requestFocus();

            MyDatePicker.initialize(startDateEditText);
            MyDatePicker.initialize(endDateEditText);
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
        titleEditText = (EditText) findViewById(R.id.termTitleEditText);
        startDateEditText = (EditText) findViewById(R.id.termStartEditText);
        endDateEditText = (EditText) findViewById(R.id.termEndEditText);
    }

    private void populateUIObjects() {
        titleEditText.setText(oldTerm.getTitle());
        startDateEditText.setText(oldTerm.getStartDate());
        endDateEditText.setText(oldTerm.getEndDate());
    }

    private void finishEditing() {
        TermModel newTerm = new TermModel(
                titleEditText.getText().toString().trim(),
                startDateEditText.getText().toString().trim(),
                endDateEditText.getText().toString().trim()
        );

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
                } else if (oldTerm.equals(newTerm)) {
                    setResult(RESULT_CANCELED);
                } else {
                    updateItem(newTerm);
                }
                break;
        }
        finish();
    }

    private void deleteItem() {
        getContentResolver().delete(TermProvider.CONTENT_URI, itemFilter, null);
        Toast.makeText(this, R.string.term_deleted, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void updateItem(TermModel term) {
        ContentValues values = getContentValuesFromModel(term);
        getContentResolver().update(TermProvider.CONTENT_URI, values, itemFilter, null);
        Toast.makeText(this, R.string.term_updated, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertItem(TermModel term) {
        ContentValues values = getContentValuesFromModel(term);
        getContentResolver().insert(TermProvider.CONTENT_URI, values);
        Toast.makeText(this, R.string.term_inserted, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private ContentValues getContentValuesFromModel(TermModel term) {
        ContentValues values = new ContentValues();
        values.put(TermDBHelper.COLUMN_NAME_TITLE, term.getTitle());
        values.put(TermDBHelper.COLUMN_NAME_START, term.getStartDate());
        values.put(TermDBHelper.COLUMN_NAME_END, term.getEndDate());
        return values;
    }


}
