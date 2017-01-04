package com.example.wgu;

import android.database.Cursor;

import java.util.Arrays;

public class CourseModel {
    private int termId;
    private String title;
    private String startDate;
    private String endDate;
    private String status;
    private String note;
    private byte[] image;

    public CourseModel(int termId, String title, String startDate, String endDate, String status, String note, byte[] image) {
        this.termId = termId;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.note = note;
        this.image = image;
    }

    public CourseModel(Cursor cursor) {
        termId = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_COURSE_TERM_ID));
        title = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_COURSE_TITLE));
        startDate = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_COURSE_START));
        endDate = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_COURSE_END));
        status = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_COURSE_STATUS));
        note = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_COURSE_NOTE));
        image = cursor.getBlob(cursor.getColumnIndex(DBHelper.COLUMN_COURSE_IMAGE));
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
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
                && course.getStatus().equals(status)
                && course.getNote().equals(note)
                && (course.getImage() != null && Arrays.equals(course.getImage(), image));
    }

    public boolean isEmpty() {
        return title.length() == 0
                && startDate.length() == 0
                && endDate.length() == 0
                && status.length() == 0
                && note.length() == 0;
    }
}
