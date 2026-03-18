package com.example.gym;

public class Exercise {
    private int id;
    private String name;
    private int series;
    private String reps;

    private String weight;
    private int recoverySeconds;
    private String muscleGroup;
    private boolean completed = false;
    private int seriesDone = 0;

    // Costruttore, getter e setter
    public Exercise(int id, String name, int series, String reps,String weight,
                    int recoverySeconds, String muscleGroup) {
        this.id = id;
        this.name = name;
        this.series = series;
        this.reps = reps;
        this.weight=weight;
        this.recoverySeconds = recoverySeconds;
        this.muscleGroup = muscleGroup;
    }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public int getSeriesDone() { return seriesDone; }
    public void setSeriesDone(int seriesDone) { this.seriesDone = seriesDone; }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSeries() {
        return series;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    public String getReps() {
        return reps;
    }

    public void setReps(String reps) {
        this.reps = reps;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public int getRecoverySeconds() {
        return recoverySeconds;
    }

    public void setRecoverySeconds(int recoverySeconds) {
        this.recoverySeconds = recoverySeconds;
    }

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public void setMuscleGroup(String muscleGroup) {
        this.muscleGroup = muscleGroup;
    }
}