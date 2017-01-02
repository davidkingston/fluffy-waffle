package com.example.wgu;

import android.database.Cursor;

public class AssessmentModel {
    private int courseId;
    private String title;
    private String dueDate;
    private String goalDate;
    private String type;
    private String note;

    public AssessmentModel(int courseId, String title, String dueDate, String goalDate, String type, String note) {
        this.courseId = courseId;
        this.title = title;
        this.dueDate = dueDate;
        this.goalDate = goalDate;
        this.type = type;
        this.note = note;
    }

    public AssessmentModel(Cursor cursor) {
        courseId = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ASSESSMENT_COURSE_ID));
        title = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ASSESSMENT_TITLE));
        dueDate = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ASSESSMENT_DUE));
        goalDate = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ASSESSMENT_GOAL));
        type = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ASSESSMENT_TYPE));
        note = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ASSESSMENT_NOTE));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getGoalDate() {
        return goalDate;
    }

    public void setGoalDate(String goalDate) {
        this.goalDate = goalDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getType() {
        return (type == null) ? "" : type;
    }

    public void setType(String type) {
        this.type = (type == null) ? "" : type;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AssessmentModel assessment = (AssessmentModel) obj;
        return assessment.getTitle().equals(title)
                && assessment.getCourseId() == courseId
                && assessment.getDueDate().equals(dueDate)
                && assessment.getGoalDate().equals(goalDate)
                && assessment.getType().equals(type)
                && assessment.getNote().equals(note);
    }

    public boolean isEmpty() {
        return title.length() == 0
                && dueDate.length() == 0
                && goalDate.length() == 0
                && type.length() == 0
                && note.length() == 0;
    }
}
