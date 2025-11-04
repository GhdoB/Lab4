package com.example.lab4;

import android.content.Context;
import java.util.List;

public interface StorageManager {
    void saveNote(Context context, Note note);
    List<Note> loadNotes(Context context);
    void deleteNote(Context context, String noteId);
    void updateNote(Context context, Note note);
}