package com.example.lab4;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AddNoteActivity extends AppCompatActivity {
    private EditText etNoteName;
    private EditText etNoteContent;
    private Button btnSave;
    private StorageManager sharedPrefsManager;
    private StorageManager sqliteManager;
    private Note existingNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_note);
        Log.d("AddNoteActivity", "onCreate() called");

        initializeViews();
        setupStorageManagers();
        loadNoteData();
        setupClickListeners();
    }

    private void initializeViews() {
        Log.d("AddNoteActivity", "initializeViews() called");
        etNoteName = findViewById(R.id.etNoteName);
        etNoteContent = findViewById(R.id.etNoteContent);
        btnSave = findViewById(R.id.btnSave);
    }

    private void setupStorageManagers() {
        Log.d("AddNoteActivity", "setupStorageManagers() called");
        sharedPrefsManager = new SharedPreferencesManager();
        sqliteManager = new SQLiteManager(this);
    }

    private void loadNoteData() {
        Log.d("AddNoteActivity", "loadNoteData() called");
        existingNote = (Note) getIntent().getSerializableExtra("note");

        if (existingNote != null) {
            Log.d("AddNoteActivity", "Editing existing note: " + existingNote.getName());
            etNoteName.setText(existingNote.getName());
            etNoteContent.setText(existingNote.getContent());
        } else {
            Log.d("AddNoteActivity", "Creating new note");
        }
    }

    private void setupClickListeners() {
        Log.d("AddNoteActivity", "setupClickListeners() called");
        btnSave.setOnClickListener(v -> {
            Log.d("AddNoteActivity", "Save button clicked");
            saveNote();
        });
    }

    private void saveNote() {
        Log.d("AddNoteActivity", "saveNote() called");
        String name = etNoteName.getText().toString().trim();
        String content = etNoteContent.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Log.d("AddNoteActivity", "Validation failed - name is empty");
            etNoteName.setError("Note name is required");
            return;
        }

        if (TextUtils.isEmpty(content)) {
            Log.d("AddNoteActivity", "Validation failed - content is empty");
            etNoteContent.setError("Note content is required");
            return;
        }

        Log.d("AddNoteActivity", "Validation passed - showing storage selection");
        showStorageSelectionDialog(name, content);
    }

    private void showStorageSelectionDialog(String name, String content) {
        Log.d("AddNoteActivity", "showStorageSelectionDialog() called");
        String[] storageOptions = {"Save to Shared Preferences", "Save to SQLite Database"};

        new AlertDialog.Builder(this)
                .setTitle("Select Storage Method")
                .setItems(storageOptions, (dialog, which) -> {
                    StorageManager selectedManager = null;

                    switch (which) {
                        case 0:
                            selectedManager = sharedPrefsManager;
                            Log.d("AddNoteActivity", "User selected Shared Preferences");
                            break;
                        case 1:
                            selectedManager = sqliteManager;
                            Log.d("AddNoteActivity", "User selected SQLite");
                            break;
                    }

                    if (selectedManager != null) {
                        saveNoteToStorage(selectedManager, name, content);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveNoteToStorage(StorageManager storageManager, String name, String content) {
        Log.d("AddNoteActivity", "saveNoteToStorage() called with manager: " + storageManager.getClass().getSimpleName());

        Note note;
        if (existingNote != null) {
            note = existingNote;
            note.setName(name);
            note.setContent(content);
            Log.d("AddNoteActivity", "Updating existing note");
        } else {
            note = new Note(name, content);
            Log.d("AddNoteActivity", "Creating new note");
        }

        storageManager.saveNote(this, note);

        Log.d("AddNoteActivity", "Note saved successfully");
        Toast.makeText(this, "Note saved successfully!", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }
}