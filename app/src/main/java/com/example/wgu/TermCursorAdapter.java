package com.example.wgu;

import android.content.Context;
import android.database.Cursor;

public class TermCursorAdapter extends BaseCursorAdapter {
    public TermCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags,
                R.layout.term_list_item, R.id.termListTitleTextView, DBHelper.COLUMN_TERM_TITLE);
    }
}
