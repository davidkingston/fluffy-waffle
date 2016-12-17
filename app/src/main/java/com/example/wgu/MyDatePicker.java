package com.example.wgu;

import android.app.DatePickerDialog;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.view.MotionEvent.ACTION_DOWN;

public class MyDatePicker {
    private static Calendar calendar = Calendar.getInstance();
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

    public static void initialize(final EditText editText) {

        editText.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getAction() == ACTION_DOWN) {
                            editText.setInputType(InputType.TYPE_NULL);
                            editText.onTouchEvent(motionEvent);

                            if (editText.getText().length() != 0) {
                                try {
                                    calendar.setTime(dateFormat.parse(editText.getText().toString()));
                                } catch (ParseException e) {
                                    calendar = Calendar.getInstance();
                                }
                            } else {
                                calendar = Calendar.getInstance();
                            }

                            new DatePickerDialog(
                                    editText.getContext(),
                                    new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                            calendar.set(year, monthOfYear, dayOfMonth);
                                            editText.setText(dateFormat.format(calendar.getTime()));
                                        }
                                    },
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                            ).show();
                        }
                        return true;
                    }
                }
        );
    }
}
