package com.example.notetake;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    NoteAdapter noteAdapter;
    List<Note> noteList;
    DatabaseReference notesRef;
    FloatingActionButton fabAdd;
    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewNotes);
        fabAdd = findViewById(R.id.fabAddNote);
        bottomNavigation = findViewById(R.id.bottom_navigation);

        // RecyclerView ayarları
        noteList = new ArrayList<>();
        noteAdapter = new NoteAdapter(noteList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(noteAdapter);

        // Firebase referansı
        String userId = FirebaseAuth.getInstance().getUid();
        notesRef = FirebaseDatabase.getInstance().getReference("notes").child(userId);

        notesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                noteList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Note note = snap.getValue(Note.class);
                    noteList.add(note);
                }
                noteAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Veri okunamadı!", Toast.LENGTH_SHORT).show();
            }
        });

        // FAB tıklaması
        fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddNoteActivity.class));
        });

        // Bottom Navigation ayarları
        bottomNavigation.setSelectedItemId(R.id.nav_notes); // aktif olan sekme
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_notes) {
                return true; // zaten buradasın
            } else if (id == R.id.nav_add) {
                startActivity(new Intent(MainActivity.this, AddNoteActivity.class));
                return true;
            } else if (id == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            }

            return false;
        });
    }
}
