package com.example.wgu;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

// used to populate a ListView
public class TermCursorAdapter extends CursorAdapter {
    public TermCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.term_list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String termTitle = cursor.getString(
                cursor.getColumnIndex(TermDBHelper.COLUMN_NAME_TITLE));

        int pos = termTitle.indexOf(10);
        if (pos != -1) {
            termTitle = termTitle.substring(0, pos) + context.getString(R.string.ellipses);
        }

        TextView tv = (TextView) view.findViewById(R.id.termListTitleTextView);
        tv.setText(termTitle);
    }
}
