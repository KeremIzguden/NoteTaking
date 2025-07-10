package com.example.notetake;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddNoteActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_STORAGE_PERMISSION = 2;

    private EditText titleEt, contentEt;
    private Button saveBtn, chooseImageBtn;
    private ImageView imageView;
    private Uri imageUri;

    private FirebaseAuth mAuth;
    private DatabaseReference notesRef;
    private StorageReference storageRef;

    private String existingId;
    private String existingImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        titleEt = findViewById(R.id.editTextTitle);
        contentEt = findViewById(R.id.editTextContent);
        saveBtn = findViewById(R.id.buttonSaveNote);
        chooseImageBtn = findViewById(R.id.buttonChooseImage);
        imageView = findViewById(R.id.imageViewNote);

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getUid();
        notesRef = FirebaseDatabase.getInstance().getReference("notes").child(userId);
        storageRef = FirebaseStorage.getInstance().getReference("note_images");

        existingId = getIntent().getStringExtra("noteId");
        String existingTitle = getIntent().getStringExtra("noteTitle");
        String existingContent = getIntent().getStringExtra("noteContent");
        existingImageUrl = getIntent().getStringExtra("noteImageUrl");

        if (existingTitle != null) {
            titleEt.setText(existingTitle);
            contentEt.setText(existingContent);
            saveBtn.setText("Güncelle");

            if (existingImageUrl != null && !existingImageUrl.isEmpty()) {
                Glide.with(this).load(existingImageUrl).into(imageView);
            }
        }

        chooseImageBtn.setOnClickListener(v -> {
            if (checkPermission()) {
                openGallery();
            } else {
                requestPermission();
            }
        });

        saveBtn.setOnClickListener(v -> {
            String title = titleEt.getText().toString().trim();
            String content = contentEt.getText().toString().trim();

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Boş alan bırakmayın", Toast.LENGTH_SHORT).show();
                return;
            }

            if (imageUri != null) {
                StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpg");
                fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            saveNote(title, content, imageUrl);
                        })
                ).addOnFailureListener(e ->
                        Toast.makeText(this, "Fotoğraf yüklenemedi: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            } else {
                saveNote(title, content, existingImageUrl);
            }
        });
    }

    private void saveNote(String title, String content, @Nullable String imageUrl) {
        String noteId = (existingId != null) ? existingId : notesRef.push().getKey();
        Note note = new Note(noteId, title, content, imageUrl);

        notesRef.child(noteId).setValue(note).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, (existingId != null ? "Not güncellendi" : "Not kaydedildi"), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Kayıt başarısız", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    REQUEST_STORAGE_PERMISSION);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Galeriye erişim izni reddedildi", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }
}
