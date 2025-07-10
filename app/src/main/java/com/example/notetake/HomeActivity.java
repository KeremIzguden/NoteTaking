package com.example.notetake;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    TextView welcomeText;
    Button goToNotesBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        welcomeText = findViewById(R.id.textViewWelcome);
        goToNotesBtn = findViewById(R.id.buttonGoToNotes);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        welcomeText.setText("Hoş geldiniz, " + email);

        goToNotesBtn.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_notes) {
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
                return true;
            } else if (id == R.id.nav_add) {
                startActivity(new Intent(HomeActivity.this, AddNoteActivity.class));
                return true;
            } else if (id == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
                return true;
            }

            return false;


        });
    }
}
