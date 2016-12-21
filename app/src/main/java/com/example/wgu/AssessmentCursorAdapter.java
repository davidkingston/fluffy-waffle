package com.example.wgu;

import android.content.Context;
import android.database.Cursor;

public class AssessmentCursorAdapter extends BaseCursorAdapter {
    public AssessmentCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags,
                R.layout.assessment_list_item, R.id.assessmentListTitleTextView, DBHelper.COLUMN_ASSESSMENT_TITLE);
    }
}
