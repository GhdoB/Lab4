package com.example.lab4;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private List<Note> notes;
    private OnNoteClickListener noteClickListener;
    private OnNoteLongClickListener noteLongClickListener;

    public interface OnNoteClickListener {
        void onNoteClick(Note note);
    }

    public interface OnNoteLongClickListener {
        void onNoteLongClick(Note note);
    }

    public NoteAdapter(List<Note> notes) {
        Log.d("NoteAdapter", "Constructor called with " + (notes != null ? notes.size() : "null") + " notes");
        this.notes = notes;
    }

    public void setOnNoteClickListener(OnNoteClickListener listener) {
        Log.d("NoteAdapter", "setOnNoteClickListener() called");
        this.noteClickListener = listener;
    }

    public void setOnNoteLongClickListener(OnNoteLongClickListener listener) {
        Log.d("NoteAdapter", "setOnNoteLongClickListener() called");
        this.noteLongClickListener = listener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("NoteAdapter", "onCreateViewHolder() called");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Log.d("NoteAdapter", "onBindViewHolder() called for position: " + position);
        if (notes == null || position >= notes.size()) {
            Log.e("NoteAdapter", "Invalid position or null notes list");
            return;
        }

        Note note = notes.get(position);
        holder.bind(note);

        holder.itemView.setOnClickListener(v -> {
            Log.d("NoteAdapter", "Note clicked at position: " + position);
            if (noteClickListener != null) {
                noteClickListener.onNoteClick(note);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            Log.d("NoteAdapter", "Note long clicked at position: " + position);
            if (noteLongClickListener != null) {
                noteLongClickListener.onNoteLongClick(note);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        int count = notes != null ? notes.size() : 0;
        Log.d("NoteAdapter", "getItemCount() called, returning: " + count);
        return count;
    }

    public void updateNotes(List<Note> newNotes) {
        Log.d("NoteAdapter", "updateNotes() called with " + (newNotes != null ? newNotes.size() : "null") + " notes");
        this.notes = newNotes;
        notifyDataSetChanged();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNoteName;
        private TextView tvNoteContent;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("NoteViewHolder", "Constructor called");

            // Initialize the TextViews
            tvNoteName = itemView.findViewById(R.id.tvNoteName);
            tvNoteContent = itemView.findViewById(R.id.tvNoteContent);

            // Check if views are found
            if (tvNoteName == null) {
                Log.e("NoteViewHolder", "tvNoteName not found in layout! Check note_item.xml");
            }
            if (tvNoteContent == null) {
                Log.e("NoteViewHolder", "tvNoteContent not found in layout! Check note_item.xml");
            }
        }

        public void bind(Note note) {
            Log.d("NoteViewHolder", "bind() called for note: " + (note != null ? note.getName() : "null"));

            if (tvNoteName != null) {
                tvNoteName.setText(note != null ? note.getName() : "No Title");
            } else {
                Log.e("NoteViewHolder", "tvNoteName is null during bind!");
            }

            if (tvNoteContent != null) {
                tvNoteContent.setText(note != null ? note.getContent() : "No Content");
            } else {
                Log.e("NoteViewHolder", "tvNoteContent is null during bind!");
            }
        }
    }
}