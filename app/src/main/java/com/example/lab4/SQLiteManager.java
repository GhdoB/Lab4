package com.example.lab4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class SQLiteManager implements StorageManager {
    private static final String DATABASE_NAME = "NotesDatabase";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "notes";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    private SQLiteDatabase database;

    public SQLiteManager(Context context) {
        Log.d("SQLiteManager", "Constructor called");
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            database = dbHelper.getWritableDatabase();
            Log.d("SQLiteManager", "SQLite database initialized successfully");
        } catch (Exception e) {
            Log.e("SQLiteManager", "Error initializing SQLite database: " + e.getMessage());
        }
    }

    @Override
    public void saveNote(Context context, Note note) {
        Log.d("SQLiteManager", "saveNote() called for note: " + note.getName());
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ID, note.getId());
            values.put(COLUMN_NAME, note.getName());
            values.put(COLUMN_CONTENT, note.getContent());
            values.put(COLUMN_TIMESTAMP, note.getTimestamp());

            // Check if note exists
            Cursor cursor = database.query(TABLE_NAME, null,
                    COLUMN_ID + " = ?", new String[]{note.getId()}, null, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                // Update existing note
                database.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{note.getId()});
                Log.d("SQLiteManager", "Note updated successfully");
            } else {
                // Insert new note
                database.insert(TABLE_NAME, null, values);
                Log.d("SQLiteManager", "Note inserted successfully");
            }

            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("SQLiteManager", "Error saving note: " + e.getMessage());
        }
    }

    @Override
    public List<Note> loadNotes(Context context) {
        Log.d("SQLiteManager", "loadNotes() called");
        List<Note> notes = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = database.query(TABLE_NAME, null, null, null, null, null,
                    COLUMN_TIMESTAMP + " DESC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Note note = new Note();
                    note.setId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                    note.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                    note.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)));
                    note.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)));
                    notes.add(note);
                } while (cursor.moveToNext());
            }

            Log.d("SQLiteManager", "Loaded " + notes.size() + " notes from SQLite");
        } catch (Exception e) {
            Log.e("SQLiteManager", "Error loading notes: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return notes;
    }

    @Override
    public void deleteNote(Context context, String noteId) {
        Log.d("SQLiteManager", "deleteNote() called for ID: " + noteId);
        try {
            int rowsDeleted = database.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{noteId});
            Log.d("SQLiteManager", "Rows deleted: " + rowsDeleted);
        } catch (Exception e) {
            Log.e("SQLiteManager", "Error deleting note: " + e.getMessage());
        }
    }

    @Override
    public void updateNote(Context context, Note note) {
        Log.d("SQLiteManager", "updateNote() called for note: " + note.getName());
        saveNote(context, note);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            Log.d("DatabaseHelper", "DatabaseHelper constructor called");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d("DatabaseHelper", "onCreate() called - creating database table");
            String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " TEXT PRIMARY KEY, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_CONTENT + " TEXT, " +
                    COLUMN_TIMESTAMP + " INTEGER)";
            db.execSQL(createTableQuery);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d("DatabaseHelper", "onUpgrade() called from version " + oldVersion + " to " + newVersion);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}
