package com.example.notetake;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    EditText emailEt, passwordEt;
    Button registerBtn, goToLoginBtn;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEt = findViewById(R.id.editTextEmail);
        passwordEt = findViewById(R.id.editTextPassword);
        registerBtn = findViewById(R.id.buttonRegister);
        goToLoginBtn = findViewById(R.id.buttonGoToLogin);
        mAuth = FirebaseAuth.getInstance();

        registerBtn.setOnClickListener(view -> {
            String email = emailEt.getText().toString();
            String password = passwordEt.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Alanlar boş olamaz", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Kayıt başarılı!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Hata: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        goToLoginBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}