package com.example.wgu;

import android.content.Context;
import android.database.Cursor;

public class MentorCursorAdapter extends BaseCursorAdapter {
    public MentorCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags,
                R.layout.mentor_list_item, R.id.mentorListNameTextView, DBHelper.COLUMN_MENTOR_NAME);
    }
}
