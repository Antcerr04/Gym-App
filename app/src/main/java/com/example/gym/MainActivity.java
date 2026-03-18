package com.example.gym;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Button btnGambe=findViewById(R.id.btnGambe);
        Button btnBicipiti=findViewById(R.id.btnBicipiti);
        Button btnTricipiti=findViewById(R.id.btnTricipiti);
        Button btnSchiena=findViewById(R.id.btnSchiena);

        View.OnClickListener listener = v -> {
            String group= ((Button) v).getText().toString();
            Intent intent = new Intent(this,WorkoutActivity.class);
            intent.putExtra("GROUP",group);
            startActivity(intent);
        };

        btnGambe.setOnClickListener(v -> openWorkout("Gambe"));
        btnBicipiti.setOnClickListener(v -> openWorkout("Bicipiti e Petto"));
        btnTricipiti.setOnClickListener(v -> openWorkout("Tricipiti e Petto"));
        btnSchiena.setOnClickListener(v -> openWorkout("Schiena"));

        setupBottomNav();

    }

    private void openWorkout(String group) {
        Intent intent = new Intent(this, WorkoutActivity.class);
        intent.putExtra("GROUP", group);
        startActivity(intent);
    }

    private void setupBottomNav() {
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_calendar) {
                startActivity(new Intent(this, CalendarActivity.class));
            } else if (item.getItemId() == R.id.nav_edit) {
                startActivity(new Intent(this, EditSchemaActivity.class));
            }
            return true;
        });
    }
}