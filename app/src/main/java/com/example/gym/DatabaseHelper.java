package com.example.gym;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME    = "gymapp.db";
    private static final int    DB_VERSION = 2;

    // Tabella esercizi
    private static final String TABLE_EXERCISES  = "exercises";
    private static final String COL_ID           = "id";
    private static final String COL_NAME         = "name";
    private static final String COL_SERIES       = "series";
    private static final String COL_REPS         = "reps";

    private static final String COL_WEIGHT       = "weight";
    private static final String COL_RECOVERY     = "recovery";
    private static final String COL_MUSCLE_GROUP = "muscle_group";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_EXERCISES + " (" +
                COL_ID           + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME         + " TEXT NOT NULL, " +
                COL_SERIES       + " INTEGER NOT NULL, " +
                COL_REPS         + " TEXT  NOT NULL, " +
                COL_WEIGHT       + " TEXT NOT NULL,"+
                COL_RECOVERY     + " INTEGER NOT NULL, " +
                COL_MUSCLE_GROUP + " TEXT NOT NULL" +
                ")";
        db.execSQL(createTable);
        insertDefaultExercises(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISES);
        onCreate(db);
    }

    // ── CRUD ────────────────────────────────────────────────────────────────

    /** Inserisce un esercizio, restituisce l'id generato (-1 se errore). */
    public long insertExercise(String name, int series, String reps,String weight,
                               int recovery, String muscleGroup) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME,         name);
        values.put(COL_SERIES,       series);
        values.put(COL_REPS,         reps);
        values.put(COL_WEIGHT,       weight);
        values.put(COL_RECOVERY,     recovery);
        values.put(COL_MUSCLE_GROUP, muscleGroup);
        long id = db.insert(TABLE_EXERCISES, null, values);
        db.close();
        return id;
    }

    /** Restituisce tutti gli esercizi di un gruppo muscolare. */
    public List<Exercise> getExercisesByGroup(String muscleGroup) {
        List<Exercise> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_EXERCISES,
                null,
                COL_MUSCLE_GROUP + " = ?",
                new String[]{muscleGroup},
                null, null, null
        );
        if (cursor.moveToFirst()) {
            do {
                Exercise e = new Exercise(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_SERIES)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_REPS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_WEIGHT)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_RECOVERY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_MUSCLE_GROUP))
                );
                list.add(e);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    /** Restituisce tutti gli esercizi (utile per EditSchemaActivity). */
    public List<Exercise> getAllExercises() {
        List<Exercise> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_EXERCISES, null);
        if (cursor.moveToFirst()) {
            do {
                Exercise e = new Exercise(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_SERIES)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_REPS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_WEIGHT)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_RECOVERY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_MUSCLE_GROUP))
                );
                list.add(e);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    /** Aggiorna un esercizio esistente. */
    public int updateExercise(Exercise exercise) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME,         exercise.getName());
        values.put(COL_SERIES,       exercise.getSeries());
        values.put(COL_REPS,         exercise.getReps());
        values.put(COL_WEIGHT,       exercise.getWeight());
        values.put(COL_RECOVERY,     exercise.getRecoverySeconds());
        values.put(COL_MUSCLE_GROUP, exercise.getMuscleGroup());
        int rows = db.update(TABLE_EXERCISES, values,
                COL_ID + " = ?",
                new String[]{String.valueOf(exercise.getId())});
        db.close();
        return rows;
    }

    /** Elimina un esercizio per id. */
    public void deleteExercise(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_EXERCISES, COL_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    // ── Dati di default ─────────────────────────────────────────────────────

    /** Popola il DB con una scheda di esempio al primo avvio. */
    private void insertDefaultExercises(SQLiteDatabase db) {
        Object[][] defaults = {
                // { nome, serie, reps, recupero(s), gruppo }
                { "Squat",              4, "10","10", 90, "Gambe" },
                { "Leg press",          3, "12","12", 60, "Gambe" },
                { "Affondi",            3, "10","10", 60, "Gambe" },
                { "Curl bilanciere",    3, "10","10", 60, "Bicipiti e Petto" },
                { "Curl manubri",       3, "12","12", 60, "Bicipiti e Petto" },
                { "Panca piana",        4, "8","8",  90, "Bicipiti e Petto" },
                { "French press",       3, "10","10", 60, "Tricipiti e Petto" },
                { "Pushdown cavi",      3, "12","12", 60, "Tricipiti e Petto" },
                { "Panca inclinata",    3, "10","10", 75, "Tricipiti e Petto" },
                { "Stacco da terra",    4, "6","7", 120,"Schiena" },
                { "Trazioni",           3, "8","8",  90, "Schiena" },
                { "Rematore bilanciere",3, "10","10" ,75, "Schiena" },
        };
        for (Object[] row : defaults) {
            ContentValues v = new ContentValues();
            v.put(COL_NAME,         (String) row[0]);
            v.put(COL_SERIES,       (int)    row[1]);
            v.put(COL_REPS,         (String)    row[2]);
            v.put(COL_WEIGHT,       (String) row[3]);
            v.put(COL_RECOVERY,     (int)    row[4]);
            v.put(COL_MUSCLE_GROUP, (String) row[5]);
            db.insert(TABLE_EXERCISES, null, v);
        }
    }
}