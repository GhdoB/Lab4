package com.example.lab4;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private List<Note> notes;
    private StorageManager currentStorageManager;
    private SharedPreferencesManager sharedPrefsManager;
    private SQLiteManager sqliteManager;
    private TextView tvEmptyState;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity", "onCreate() called");

        initializeViews();
        setupStorageManagers();
        setupRecyclerView();
        loadNotes();
        setupClickListeners();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeViews() {
        Log.d("MainActivity", "initializeViews() called");
        recyclerView = findViewById(R.id.rvAddedNotes);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        fab = findViewById(R.id.fab);
    }

    private void setupStorageManagers() {
        Log.d("MainActivity", "setupStorageManagers() called");
        try {
            sharedPrefsManager = new SharedPreferencesManager();
            sqliteManager = new SQLiteManager(this);
            // Default storage
            currentStorageManager = sharedPrefsManager;
            Log.d("MainActivity", "Storage managers initialized successfully");
        } catch (Exception e) {
            Log.e("MainActivity", "Error initializing storage managers: " + e.getMessage());
            sharedPrefsManager = new SharedPreferencesManager();
            currentStorageManager = sharedPrefsManager;
        }
    }

    private void setupRecyclerView() {
        Log.d("MainActivity", "setupRecyclerView() called");
        notes = new ArrayList<>();
        noteAdapter = new NoteAdapter(notes);

        noteAdapter.setOnNoteClickListener(note -> {
            Log.d("MainActivity", "Note clicked: " + note.getName());
            editNote(note);
        });

        noteAdapter.setOnNoteLongClickListener(note -> {
            Log.d("MainActivity", "Note long clicked: " + note.getName());
            showNoteOptionsDialog(note);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(noteAdapter);
    }

    private void setupClickListeners() {
        Log.d("MainActivity", "setupClickListeners() called");
        fab.setOnClickListener(v -> {
            Log.d("MainActivity", "FAB clicked - creating new note");
            createNewNote();
        });
    }

    private void createNewNote() {
        Log.d("MainActivity", "createNewNote() called");
        Intent intent = new Intent(this, AddNoteActivity.class);
        startActivityForResult(intent, 1);
    }

    private void editNote(Note note) {
        Log.d("MainActivity", "editNote() called for: " + note.getName());
        Intent intent = new Intent(this, AddNoteActivity.class);
        intent.putExtra("note", note);
        startActivityForResult(intent, 1);
    }

    private void showNoteOptionsDialog(Note note) {
        Log.d("MainActivity", "showNoteOptionsDialog() called for: " + note.getName());
        String[] options = {"Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Note Options")
                .setItems(options, (dialog, which) -> {
                    Log.d("MainActivity", "Delete option selected");
                    showDeleteConfirmationDialog(note);
                })
                .show();
    }

    private void showDeleteConfirmationDialog(Note note) {
        Log.d("MainActivity", "showDeleteConfirmationDialog() called");
        new AlertDialog.Builder(this)
                .setTitle("Delete Note")
                .setMessage("Are you sure you want to delete '" + note.getName() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    Log.d("MainActivity", "User confirmed deletion of note: " + note.getName());
                    deleteNote(note);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteNote(Note note) {
        Log.d("MainActivity", "deleteNote() called for: " + note.getName());
        currentStorageManager.deleteNote(this, note.getId());
        loadNotes();
        Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show();
    }

    private void loadNotes() {
        Log.d("MainActivity", "loadNotes() called");

        if (currentStorageManager == null) {
            Log.e("MainActivity", "currentStorageManager is null! Initializing...");
            setupStorageManagers();
        }

        if (notes == null) {
            Log.d("MainActivity", "Notes list was null, initializing it");
            notes = new ArrayList<>();
        }

        notes.clear();

        if (currentStorageManager != null) {
            List<Note> loadedNotes = currentStorageManager.loadNotes(this);
            if (loadedNotes != null) {
                notes.addAll(loadedNotes);
            }
        } else {
            Log.e("MainActivity", "currentStorageManager is still null after initialization!");
        }

        if (noteAdapter != null) {
            noteAdapter.updateNotes(notes);
        }

        if (tvEmptyState != null && recyclerView != null) {
            if (notes.isEmpty()) {
                tvEmptyState.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                tvEmptyState.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showStorageSelectionDialog() {
        Log.d("MainActivity", "showStorageSelectionDialog() called");
        String[] storageOptions = {"Shared Preferences", "SQLite Database"};
        int currentSelection = (currentStorageManager instanceof SharedPreferencesManager) ? 0 : 1;

        new AlertDialog.Builder(this)
                .setTitle("Select Storage Method")
                .setSingleChoiceItems(storageOptions, currentSelection, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            currentStorageManager = sharedPrefsManager;
                            Log.d("MainActivity", "Storage method changed to Shared Preferences");
                            break;
                        case 1:
                            currentStorageManager = sqliteManager;
                            Log.d("MainActivity", "Storage method changed to SQLite");
                            break;
                    }
                    loadNotes();
                    dialog.dismiss();
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("MainActivity", "onActivityResult() called - requestCode: " + requestCode + ", resultCode: " + resultCode);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadNotes();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MainActivity", "onResume() called");
        loadNotes();
    }
}