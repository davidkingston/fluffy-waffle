package com.example.wgu;

import android.database.Cursor;

public class TermModel {
    private String title;
    private String startDate;
    private String endDate;

    public TermModel(String title, String startDate, String endDate) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public TermModel(Cursor cursor) {
        title = cursor.getString(cursor.getColumnIndex(TermDBHelper.COLUMN_NAME_TITLE));
        startDate = cursor.getString(cursor.getColumnIndex(TermDBHelper.COLUMN_NAME_START));
        endDate = cursor.getString(cursor.getColumnIndex(TermDBHelper.COLUMN_NAME_END));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TermModel term = (TermModel) obj;
        return term.getTitle().equals(title)
                && term.getStartDate().equals(startDate)
                && term.getEndDate().equals(endDate);
    }

    public boolean isEmpty() {
        return title.length() == 0
                && startDate.length() == 0
                && endDate.length() == 0;
    }
}
