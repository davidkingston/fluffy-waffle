package com.example.wgu;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class BaseCursorAdapter extends CursorAdapter {
    private int listItemId;
    private int textViewId;
    private String columnName;

    public BaseCursorAdapter(Context context, Cursor c, int flags, int listItemId, int textViewId, String columnName) {
        super(context, c, flags);
        this.listItemId = listItemId;
        this.textViewId = textViewId;
        this.columnName = columnName;
    }

    public BaseCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(listItemId, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String title = cursor.getString(
                cursor.getColumnIndex(columnName));

        int pos = title.indexOf(10);
        if (pos != -1) {
            title = title.substring(0, pos) + context.getString(R.string.ellipses);
        }

        TextView tv = (TextView) view.findViewById(textViewId);
        tv.setText(title);
    }
}
