package com.example.wgu;

import android.database.Cursor;

public class CourseModel {
    private int termId;
    private String title;
    private String startDate;
    private String endDate;
    private String status;

    public CourseModel(int termId, String title, String startDate, String endDate, String status) {
        this.termId = termId;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public CourseModel(Cursor cursor) {
        termId = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_COURSE_TERM_ID));
        title = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_COURSE_TITLE));
        startDate = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_COURSE_START));
        endDate = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_COURSE_END));
        status = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_COURSE_STATUS));
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
        CourseModel course = (CourseModel) obj;
        return course.getTitle().equals(title)
                && course.getTermId() == termId
                && course.getStartDate().equals(startDate)
                && course.getEndDate().equals(endDate)
                && course.getStatus().equals(status);
    }

    public boolean isEmpty() {
        return title.length() == 0
                && startDate.length() == 0
                && endDate.length() == 0;
    }

    public int getTermId() {
        return termId;
    }

    public void setTermId(int termId) {
        this.termId = termId;
    }

    public String getStatus() {
        return (status == null) ? "" : status;
    }

    public void setStatus(String status) {
        this.status = (status == null) ? "" : status;
    }
}
