package com.example.gym;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class EditSchemaActivity extends AppCompatActivity {

    private final String[] GROUPS = {
            "Gambe", "Bicipiti e Petto", "Tricipiti e Petto", "Schiena"
    };

    private DatabaseHelper db;
    private SchemaAdapter  schemaAdapter;
    private String         currentGroup;

    // Campi form
    private TextInputEditText etName, etSeries, etReps, etWeight, etRecovery;

    // Riferimenti UI per gestire la modalità modifica
    private com.google.android.material.button.MaterialButton btnSave;
    private TextView tvFormTitle;

    // Esercizio in fase di modifica (null = modalità aggiungi)
    private Exercise exerciseInEdit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_schema);

        db           = new DatabaseHelper(this);
        currentGroup = GROUPS[0];

        etName     = findViewById(R.id.etExerciseName);
        etSeries   = findViewById(R.id.etSeries);
        etReps     = findViewById(R.id.etReps);
        etWeight   = findViewById(R.id.etWeight);
        etRecovery = findViewById(R.id.etRecovery);
        btnSave    = findViewById(R.id.btnSaveExercise);
        tvFormTitle= findViewById(R.id.tvFormTitle);

        setupTabs();
        setupRecycler();
        loadExercises();

        btnSave.setOnClickListener(v -> {
            if (exerciseInEdit == null) {
                addExercise();
            } else {
                updateExercise();
            }
        });
    }

    // ── Modalità modifica: popola il form ────────────────────────────────────

    private void enterEditMode(Exercise exercise) {
        exerciseInEdit = exercise;

        etName.setText(exercise.getName());
        etSeries.setText(String.valueOf(exercise.getSeries()));
        etReps.setText(exercise.getReps());
        etWeight.setText(exercise.getWeight());
        etRecovery.setText(String.valueOf(exercise.getRecoverySeconds()));

        tvFormTitle.setText("Modifica esercizio");
        btnSave.setText("💾  Salva modifiche");

        // Scroll verso il form (opzionale ma comodo)
        findViewById(R.id.btnSaveExercise).requestFocus();
    }

    private void exitEditMode() {
        exerciseInEdit = null;

        etName.setText("");
        etSeries.setText("");
        etReps.setText("");
        etWeight.setText("");
        etRecovery.setText("");

        tvFormTitle.setText("Aggiungi esercizio");
        btnSave.setText("➕  Aggiungi");
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    private void addExercise() {
        String name      = getText(etName);
        String seriesStr = getText(etSeries);
        String reps      = getText(etReps);
        String weight    = getText(etWeight);
        String recStr    = getText(etRecovery);

        if (name.isEmpty() || seriesStr.isEmpty() || reps.isEmpty() || recStr.isEmpty()) {
            Toast.makeText(this, "Compila tutti i campi obbligatori", Toast.LENGTH_SHORT).show();
            return;
        }

        int series   = Integer.parseInt(seriesStr);
        int recovery = Integer.parseInt(recStr);
        if (weight.isEmpty()) weight = "—";

        db.insertExercise(name, series, reps, weight, recovery, currentGroup);
        loadExercises();
        exitEditMode();
        Toast.makeText(this, "Esercizio aggiunto!", Toast.LENGTH_SHORT).show();
    }

    private void updateExercise() {
        String name      = getText(etName);
        String seriesStr = getText(etSeries);
        String reps      = getText(etReps);
        String weight    = getText(etWeight);
        String recStr    = getText(etRecovery);

        if (name.isEmpty() || seriesStr.isEmpty() || reps.isEmpty() || recStr.isEmpty()) {
            Toast.makeText(this, "Compila tutti i campi obbligatori", Toast.LENGTH_SHORT).show();
            return;
        }

        int series   = Integer.parseInt(seriesStr);
        int recovery = Integer.parseInt(recStr);
        if (weight.isEmpty()) weight = "—";

        exerciseInEdit.setName(name);
        exerciseInEdit.setSeries(series);
        exerciseInEdit.setReps(reps);
        exerciseInEdit.setWeight(weight);
        exerciseInEdit.setRecoverySeconds(recovery);

        db.updateExercise(exerciseInEdit);
        loadExercises();
        exitEditMode();
        Toast.makeText(this, "Esercizio aggiornato!", Toast.LENGTH_SHORT).show();
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private String getText(TextInputEditText field) {
        return field.getText() != null ? field.getText().toString().trim() : "";
    }

    // ── Tab, Recycler, Load ──────────────────────────────────────────────────

    private void setupTabs() {
        TabLayout tabs = findViewById(R.id.tabLayout);
        for (String group : GROUPS) {
            tabs.addTab(tabs.newTab().setText(group));
        }
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                currentGroup = GROUPS[tab.getPosition()];
                exitEditMode(); // reset form quando cambi tab
                loadExercises();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupRecycler() {
        RecyclerView recycler = findViewById(R.id.recyclerSchema);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        schemaAdapter = new SchemaAdapter();
        recycler.setAdapter(schemaAdapter);
    }

    private void loadExercises() {
        List<Exercise> list = db.getExercisesByGroup(currentGroup);
        schemaAdapter.setData(list);
    }

    // ── Adapter interno ──────────────────────────────────────────────────────

    private class SchemaAdapter extends RecyclerView.Adapter<SchemaAdapter.VH> {

        private List<Exercise> data;

        void setData(List<Exercise> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_schema_exercise, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            Exercise e = data.get(position);

            holder.tvName.setText(e.getName());
            holder.tvInfo.setText(
                    e.getSeries() + " serie  ·  " + e.getReps() + " reps" +
                            "  ·  " + e.getWeight() + "  ·  " + e.getRecoverySeconds() + "s"
            );

            // ── Pulsante modifica → entra in edit mode ───────────────────────
            holder.btnUpdate.setOnClickListener(v -> enterEditMode(e));

            // ── Pulsante elimina ─────────────────────────────────────────────
            holder.btnDelete.setOnClickListener(v -> {
                // Se stavo modificando proprio questo, resetta il form
                if (exerciseInEdit != null && exerciseInEdit.getId() == e.getId()) {
                    exitEditMode();
                }
                db.deleteExercise(e.getId());
                loadExercises();
            });
        }

        @Override public int getItemCount() { return data == null ? 0 : data.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView    tvName, tvInfo;
            ImageButton btnUpdate, btnDelete;

            VH(@NonNull View v) {
                super(v);
                tvName    = v.findViewById(R.id.tvSchemaName);
                tvInfo    = v.findViewById(R.id.tvSchemaInfo);
                btnUpdate = v.findViewById(R.id.btnUpdateExercise);
                btnDelete = v.findViewById(R.id.btnDeleteExercise);
            }
        }
    }
}