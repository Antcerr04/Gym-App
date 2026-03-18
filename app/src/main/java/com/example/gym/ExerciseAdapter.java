package com.example.gym;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {

    public interface OnAllCompletedListener {
        void onAllCompleted();
    }

    private final List<Exercise> exercises;
    private final OnAllCompletedListener listener;

    public ExerciseAdapter(List<Exercise> exercises, OnAllCompletedListener listener) {
        this.exercises = exercises;
        this.listener  = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);

        // ── Testo ───────────────────────────────────────────────────────────
        holder.tvName.setText(exercise.getName());
        holder.tvDetails.setText(
                "Serie: " + exercise.getSeries() +
                        "  |  Reps: " + exercise.getReps() +
                        "  |  Peso: "+exercise.getWeight() +
                        "  |  Recupero: " + exercise.getRecoverySeconds() + "s"
        );
        holder.tvSeriesTotal.setText("/ " + exercise.getSeries());
        holder.tvSeriesCount.setText(String.valueOf(exercise.getSeriesDone()));

        // ── Stato checkbox ───────────────────────────────────────────────────
        holder.checkDone.setChecked(exercise.isCompleted());
        applyCompletedStyle(holder, exercise.isCompleted());

        // ── CheckBox ─────────────────────────────────────────────────────────
        holder.checkDone.setOnCheckedChangeListener((btn, isChecked) -> {
            exercise.setCompleted(isChecked);
            applyCompletedStyle(holder, isChecked);
            // Notifica WorkoutActivity se tutti sono completati
            boolean allDone = exercises.stream().allMatch(Exercise::isCompleted);
            if (allDone) listener.onAllCompleted();
        });

        // ── Contatore serie: + ───────────────────────────────────────────────
        holder.btnPlus.setOnClickListener(v -> {
            int done = exercise.getSeriesDone();
            if (done < exercise.getSeries()) {
                done++;
                exercise.setSeriesDone(done);
                holder.tvSeriesCount.setText(String.valueOf(done));
                // Auto-check quando tutte le serie sono completate
                if (done == exercise.getSeries()) {
                    holder.checkDone.setChecked(true);
                }
            }
        });

        // ── Contatore serie: - ───────────────────────────────────────────────
        holder.btnMinus.setOnClickListener(v -> {
            int done = exercise.getSeriesDone();
            if (done > 0) {
                done--;
                exercise.setSeriesDone(done);
                holder.tvSeriesCount.setText(String.valueOf(done));
                // Se si torna indietro, rimuovi il check
                if (exercise.isCompleted()) {
                    holder.checkDone.setChecked(false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    /** Barra il nome e smussa la card quando l'esercizio è completato. */
    private void applyCompletedStyle(ViewHolder holder, boolean completed) {
        if (completed) {
            holder.tvName.setPaintFlags(
                    holder.tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvName.setAlpha(0.5f);
        } else {
            holder.tvName.setPaintFlags(
                    holder.tvName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvName.setAlpha(1f);
        }
    }

    // ── ViewHolder ───────────────────────────────────────────────────────────

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDetails, tvSeriesCount, tvSeriesTotal;
        CheckBox checkDone;
        Button   btnPlus, btnMinus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName        = itemView.findViewById(R.id.tvExerciseName);
            tvDetails     = itemView.findViewById(R.id.tvDetails);
            tvSeriesCount = itemView.findViewById(R.id.tvSeriesCount);
            tvSeriesTotal = itemView.findViewById(R.id.tvSeriesTotal);
            checkDone     = itemView.findViewById(R.id.checkDone);
            btnPlus       = itemView.findViewById(R.id.btnSeriesPlus);
            btnMinus      = itemView.findViewById(R.id.btnSeriesMinus);
        }
    }
}