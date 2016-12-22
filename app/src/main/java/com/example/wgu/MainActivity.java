package com.example.wgu;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int TERMLIST_EDITOR_REQUEST_CODE = 1001;
    private static final int ASSESSMENT_EDITOR_REQUEST_CODE = 1002;
    String itemFilter;
    private CursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.mainActivityToolbar);
        setSupportActionBar(toolbar);

        cursorAdapter = new MainCursorAdapter(this, null, 0);

        ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);

        list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(MainActivity.this, AssessmentActivity.class);
                        Uri uri = Uri.parse(AssessmentProvider.ASSESSMENT_CONTENT_URI + "/" + id);
                        intent.putExtra(AssessmentProvider.CONTENT_ITEM_TYPE, uri);
                        startActivityForResult(intent, ASSESSMENT_EDITOR_REQUEST_CODE);
                    }
                });

        Log.d("MainActivity", "onCreate1");
        Loader<Cursor> qqq = getLoaderManager().initLoader(0, null, this);
        Log.d("MainActivity", "onCreate2");
        Log.d("MainActivity", "count = " + Integer.toString(list.getAdapter().getCount()));

    }

    public void openTermList(View view) {
        Intent intent = new Intent(MainActivity.this, TermListActivity.class);
        intent.putExtra(AssessmentProvider.CONTENT_ITEM_TYPE, TermProvider.CONTENT_URI);
        startActivityForResult(intent, TERMLIST_EDITOR_REQUEST_CODE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String nowString = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
        itemFilter = DBHelper.COLUMN_ASSESSMENT_GOAL + " = '" + nowString + "'";
        Uri uri = AssessmentProvider.ASSESSMENT_CONTENT_URI
                .buildUpon()
                .appendPath("d")
                .appendPath(nowString)
                .build();
        return new CursorLoader(this, uri, null, itemFilter, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursorAdapter.swapCursor(cursor);
        setAssessmentMessage(cursor.getCount());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        restartLoader();
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    private void setAssessmentMessage(int count) {
        TextView v = (TextView) findViewById(R.id.mainAssessmentsDueTextView);
        switch (count) {
            case 0:
                v.setText("No assessments due today.");
                v.setGravity(Gravity.CENTER_HORIZONTAL);
                break;
            case 1:
                v.setText("1 assessment due today.");
                v.setGravity(Gravity.LEFT);
                break;

            default:
                v.setText(Integer.toString(count) + " assessments due today.");
                v.setGravity(Gravity.LEFT);
                break;
        }
    }
}
