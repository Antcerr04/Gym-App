package com.example.gym;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {

    public interface OnAllCompletedListener {
        void onAllCompleted();
    }

    // Callback per salvare il peso aggiornato nel DB
    public interface OnWeightUpdatedListener {
        void onWeightUpdated(Exercise exercise, String newWeight);
    }

    private final List<Exercise> exercises;
    private final OnAllCompletedListener completedListener;
    private final OnWeightUpdatedListener weightListener;

    public ExerciseAdapter(List<Exercise> exercises,
                           OnAllCompletedListener completedListener,
                           OnWeightUpdatedListener weightListener) {
        this.exercises         = exercises;
        this.completedListener = completedListener;
        this.weightListener    = weightListener;
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

        holder.tvName.setText(exercise.getName());
        updateDetailsText(holder.tvDetails, exercise);

        holder.tvSeriesTotal.setText("/ " + exercise.getSeries());
        holder.tvSeriesCount.setText(String.valueOf(exercise.getSeriesDone()));

        holder.checkDone.setChecked(exercise.isCompleted());
        applyCompletedStyle(holder, exercise.isCompleted());

        // ── Long press su tvDetails → dialog modifica peso ───────────────────
        holder.tvDetails.setOnLongClickListener(v -> {
            showWeightDialog(v.getContext(), exercise, holder);
            return true;
        });

        // ── CheckBox ─────────────────────────────────────────────────────────
        holder.checkDone.setOnCheckedChangeListener((btn, isChecked) -> {
            exercise.setCompleted(isChecked);
            applyCompletedStyle(holder, isChecked);
            boolean allDone = exercises.stream().allMatch(Exercise::isCompleted);
            if (allDone) completedListener.onAllCompleted();
        });

        // ── Contatore serie + ────────────────────────────────────────────────
        holder.btnPlus.setOnClickListener(v -> {
            int done = exercise.getSeriesDone();
            if (done < exercise.getSeries()) {
                done++;
                exercise.setSeriesDone(done);
                holder.tvSeriesCount.setText(String.valueOf(done));
                if (done == exercise.getSeries()) {
                    holder.checkDone.setChecked(true);
                }
            }
        });
    }

    private void showWeightDialog(Context context, Exercise exercise, ViewHolder holder) {
        // EditText per il nuovo peso
        EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("es. 80kg / corpo libero");
        input.setText(exercise.getWeight());
        input.selectAll();
        int pad = (int) (16 * context.getResources().getDisplayMetrics().density);
        input.setPadding(pad, pad, pad, pad);

        new AlertDialog.Builder(context)
                .setTitle("Modifica peso — " + exercise.getName())
                .setView(input)
                .setPositiveButton("Salva", (dialog, which) -> {
                    String newWeight = input.getText().toString().trim();
                    if (newWeight.isEmpty()) {
                        Toast.makeText(context, "Inserisci un peso valido",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    exercise.setWeight(newWeight);
                    updateDetailsText(holder.tvDetails, exercise);
                    // Persisti nel DB tramite callback
                    if (weightListener != null) {
                        weightListener.onWeightUpdated(exercise, newWeight);
                    }
                })
                .setNegativeButton("Annulla", null)
                .show();
    }

    private void updateDetailsText(TextView tv, Exercise exercise) {
        tv.setText(
                "Serie: " + exercise.getSeries() +
                        "  |  Reps: " + exercise.getReps() +
                        "  |  Peso: " + exercise.getWeight() +
                        "  |  Recupero: " + exercise.getRecoverySeconds() + "s"
        );
    }

    private void applyCompletedStyle(ViewHolder holder, boolean completed) {
        if (completed) {
            holder.tvName.setPaintFlags(
                    holder.tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvName.setAlpha(0.4f);
        } else {
            holder.tvName.setPaintFlags(
                    holder.tvName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvName.setAlpha(1f);
        }
    }

    @Override
    public int getItemCount() { return exercises.size(); }

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
        }
    }
}