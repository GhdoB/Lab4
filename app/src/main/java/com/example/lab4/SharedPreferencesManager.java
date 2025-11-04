package com.example.lab4;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPreferencesManager implements StorageManager {
    private static final String PREF_NAME = "notes_prefs";
    private static final String NOTES_KEY = "notes_list";

    @Override
    public void saveNote(Context context, Note note) {
        Log.d("SharedPreferencesManager", "saveNote() called for note: " + note.getName());
        try {
            List<Note> notes = loadNotes(context);
            boolean found = false;

            for (int i = 0; i < notes.size(); i++) {
                if (notes.get(i).getId().equals(note.getId())) {
                    notes.set(i, note);
                    found = true;
                    break;
                }
            }

            if (!found) {
                notes.add(note);
            }

            saveNotesList(context, notes);
            Log.d("SharedPreferencesManager", "Note saved successfully");
        } catch (Exception e) {
            Log.e("SharedPreferencesManager", "Error saving note: " + e.getMessage());
        }
    }

    @Override
    public List<Note> loadNotes(Context context) {
        Log.d("SharedPreferencesManager", "loadNotes() called");
        try {
            if (context == null) {
                Log.e("SharedPreferencesManager", "Context is null!");
                return new ArrayList<>();
            }

            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String notesJson = prefs.getString(NOTES_KEY, "");

            if (notesJson.isEmpty()) {
                Log.d("SharedPreferencesManager", "No notes found, returning empty list");
                return new ArrayList<>();
            }

            Gson gson = new Gson();
            Type type = new TypeToken<List<Note>>(){}.getType();
            List<Note> notes = gson.fromJson(notesJson, type);
            Log.d("SharedPreferencesManager", "Loaded " + (notes != null ? notes.size() : 0) + " notes");
            return notes != null ? notes : new ArrayList<>();
        } catch (Exception e) {
            Log.e("SharedPreferencesManager", "Error loading notes: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void deleteNote(Context context, String noteId) {
        Log.d("SharedPreferencesManager", "deleteNote() called for ID: " + noteId);
        try {
            List<Note> notes = loadNotes(context);
            for (int i = 0; i < notes.size(); i++) {
                if (notes.get(i).getId().equals(noteId)) {
                    notes.remove(i);
                    saveNotesList(context, notes);
                    Log.d("SharedPreferencesManager", "Note deleted successfully");
                    break;
                }
            }
        } catch (Exception e) {
            Log.e("SharedPreferencesManager", "Error deleting note: " + e.getMessage());
        }
    }

    @Override
    public void updateNote(Context context, Note note) {
        Log.d("SharedPreferencesManager", "updateNote() called for note: " + note.getName());
        saveNote(context, note);
    }

    private void saveNotesList(Context context, List<Note> notes) {
        Log.d("SharedPreferencesManager", "saveNotesList() called with " + notes.size() + " notes");
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String notesJson = gson.toJson(notes);
        editor.putString(NOTES_KEY, notesJson);
        editor.apply();
    }
}