package com.example.wgu;

import android.database.Cursor;

public class AssessmentModel {
    private int courseId;
    private String title;
    private String dueDate;
    private String goalDate;
    private String type;

    public AssessmentModel(int courseId, String title, String dueDate, String goalDate, String type) {
        this.courseId = courseId;
        this.title = title;
        this.dueDate = dueDate;
        this.goalDate = goalDate;
        this.type = type;
    }

    public AssessmentModel(Cursor cursor) {
        courseId = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ASSESSMENT_COURSE_ID));
        title = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ASSESSMENT_TITLE));
        dueDate = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ASSESSMENT_DUE));
        goalDate = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ASSESSMENT_GOAL));
        type = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ASSESSMENT_TYPE));
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AssessmentModel course = (AssessmentModel) obj;
        return course.getTitle().equals(title)
                && course.getCourseId() == courseId
                && course.getDueDate().equals(dueDate)
                && course.getGoalDate().equals(goalDate)
                && course.getType().equals(type);
    }

    public boolean isEmpty() {
        return title.length() == 0
                && dueDate.length() == 0
                && goalDate.length() == 0;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
