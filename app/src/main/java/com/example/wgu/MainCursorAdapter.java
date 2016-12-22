package com.example.wgu;

import android.content.Context;
import android.database.Cursor;

public class MainCursorAdapter extends BaseCursorAdapter {
    public MainCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags,
                R.layout.main_list_item, R.id.mainAssessmentListTitleTextView, DBHelper.COLUMN_ASSESSMENT_TITLE);
    }
}
