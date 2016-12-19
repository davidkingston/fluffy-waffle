package com.example.wgu;

import android.database.Cursor;

public class MentorModel {
    private int courseId;
    private String name;
    private String email;
    private String phone;

    public MentorModel(int courseId, String name, String email, String phone) {
        this.courseId = courseId;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public MentorModel(Cursor cursor) {
        courseId = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_MENTOR_COURSE_ID));
        name = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_MENTOR_NAME));
        email = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_MENTOR_EMAIL));
        phone = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_MENTOR_PHONE));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MentorModel course = (MentorModel) obj;
        return course.getName().equals(name)
                && course.getCourseId() == courseId
                && course.getEmail().equals(email)
                && course.getPhone().equals(phone);
    }

    public boolean isEmpty() {
        return name.length() == 0
                && email.length() == 0
                && phone.length() == 0;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
}
