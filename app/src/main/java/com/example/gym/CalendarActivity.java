package com.example.gym;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        CalendarView calendarView = findViewById(R.id.calendarView);
        TextView tvWorkoutForDay = findViewById(R.id.tvWorkoutForDay);
        SharedPreferences prefs = getSharedPreferences("workout_log", MODE_PRIVATE);

        calendarView.setOnDateChangeListener((view, year, month, day) -> {
            String key = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                    year, month + 1, day);
            String workout = prefs.getString(key, null);
            if (workout != null) {
                tvWorkoutForDay.setText("Allenamento: " + workout);
            } else {
                tvWorkoutForDay.setText("Nessun allenamento registrato");
            }
        });
    }
}