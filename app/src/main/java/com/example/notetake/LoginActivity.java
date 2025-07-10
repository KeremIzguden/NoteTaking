package com.example.notetake;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText emailEt, passwordEt;
    Button loginBtn, goToRegisterBtn;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEt = findViewById(R.id.editTextEmail);
        passwordEt = findViewById(R.id.editTextPassword);
        loginBtn = findViewById(R.id.buttonLogin);
        goToRegisterBtn = findViewById(R.id.buttonGoToRegister);
        mAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(view -> {
            String email = emailEt.getText().toString();
            String password = passwordEt.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Alanlar boş olamaz", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Giriş başarılı", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, HomeActivity.class)); // yönlendirme buraya
                            finish();
                        } else {
                            Toast.makeText(this, "Hata: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        goToRegisterBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });
    }
}
