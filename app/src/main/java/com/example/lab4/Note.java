package com.example.lab4;

import android.util.Log;

import java.io.Serializable;

public class Note implements Serializable {
    private String id;
    private String name;
    private String content;
    private long timestamp;

    public Note() {
        this.timestamp = System.currentTimeMillis();
        this.id = String.valueOf(timestamp);
    }

    public Note(String name, String content) {
        this.id = String.valueOf(System.currentTimeMillis());
        this.name = name;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and setters
    public String getId() {
        Log.d("Note", "getId() called, returning: " + id);
        return id;
    }

    public void setId(String id) {
        Log.d("Note", "setId() called with: " + id);
        this.id = id;
    }

    public String getName() {
        Log.d("Note", "getName() called, returning: " + name);
        return name;
    }

    public void setName(String name) {
        Log.d("Note", "setName() called with: " + name);
        this.name = name;
    }

    public String getContent() {
        Log.d("Note", "getContent() called");
        return content;
    }

    public void setContent(String content) {
        Log.d("Note", "setContent() called");
        this.content = content;
    }

    public long getTimestamp() {
        Log.d("Note", "getTimestamp() called, returning: " + timestamp);
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        Log.d("Note", "setTimestamp() called with: " + timestamp);
        this.timestamp = timestamp;
    }
}
