package com.example.wgu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final int TERMLIST_EDITOR_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.mainActivityToolbar);
        setSupportActionBar(toolbar);


    }

    public void openTermList(View view) {
        Intent intent = new Intent(MainActivity.this, TermListActivity.class);
        intent.putExtra(AssessmentProvider.CONTENT_ITEM_TYPE, TermProvider.CONTENT_URI);
        startActivityForResult(intent, TERMLIST_EDITOR_REQUEST_CODE);
    }
}
