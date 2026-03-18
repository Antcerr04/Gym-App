package com.example.gym;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorkoutActivity extends AppCompatActivity {

    private List<Exercise> exerciseList = new ArrayList<>();
    private ExerciseAdapter adapter;
    private String muscleGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        muscleGroup = getIntent().getStringExtra("GROUP");
        TextView tvGroupName = findViewById(R.id.tvGroupName);
        tvGroupName.setText(muscleGroup);

        // Carica esercizi dal DB per il gruppo selezionato
        DatabaseHelper db = new DatabaseHelper(this);
        exerciseList = db.getExercisesByGroup(muscleGroup);

        RecyclerView recycler = findViewById(R.id.recyclerExercises);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        Button btnComplete = findViewById(R.id.btnCompleteWorkout);


        adapter = new ExerciseAdapter(
                exerciseList,
                () -> btnComplete.setVisibility(View.VISIBLE),  // onAllCompleted
                (exercise, newWeight) -> db.updateExercise(exercise)  // onWeightUpdated
        );
        recycler.setAdapter(adapter);
        btnComplete.setOnClickListener(v -> saveWorkoutToCalendar());
    }

    private void saveWorkoutToCalendar() {
        SharedPreferences prefs = getSharedPreferences("workout_log", MODE_PRIVATE);
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());
        prefs.edit().putString(today, muscleGroup).apply();

        Toast.makeText(this, "Allenamento salvato!", Toast.LENGTH_SHORT).show();
        finish();
    }
}