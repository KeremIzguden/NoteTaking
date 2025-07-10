package com.example.notetake;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> noteList;

    public NoteAdapter(List<Note> noteList) {
        this.noteList = noteList;
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView titleTv, contentTv;
        ImageView imageView;
        Button deleteBtn;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.textViewTitle);
            contentTv = itemView.findViewById(R.id.textViewContent);
            imageView = itemView.findViewById(R.id.imageViewNote);
            deleteBtn = itemView.findViewById(R.id.buttonDelete);
        }
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.titleTv.setText(note.title);
        holder.contentTv.setText(note.content);

        if (note.imageUrl != null && !note.imageUrl.isEmpty()) {
            holder.imageView.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(note.imageUrl)
                    .into(holder.imageView);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }

        // ❌ Sil Butonu
        holder.deleteBtn.setOnClickListener(v -> {
            String userId = FirebaseAuth.getInstance().getUid();
            DatabaseReference noteRef = FirebaseDatabase.getInstance()
                    .getReference("notes").child(userId).child(note.id);

            noteRef.removeValue()
                    .addOnSuccessListener(unused ->
                            Toast.makeText(holder.itemView.getContext(), "Not silindi", Toast.LENGTH_SHORT).show()
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(holder.itemView.getContext(), "Silinemedi: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });

        // ✏️ Not Kartına Tıklama (Düzenleme)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), AddNoteActivity.class);
            intent.putExtra("noteId", note.id);
            intent.putExtra("noteTitle", note.title);
            intent.putExtra("noteContent", note.content);
            intent.putExtra("noteImageUrl", note.imageUrl);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }
}
