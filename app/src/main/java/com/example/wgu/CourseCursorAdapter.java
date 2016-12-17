package com.example.wgu;

import android.content.Context;
import android.database.Cursor;

public class CourseCursorAdapter extends BaseCursorAdapter {
    public CourseCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags,
                R.layout.course_list_item, R.id.courseListTitleTextView, DBHelper.COLUMN_COURSE_TITLE);
    }
}
