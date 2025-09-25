package com.mario.it;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class WelcomeActivity extends AppCompatActivity {

    private TextInputLayout nameInputLayout;
    private TextInputEditText nameInput;
    private MaterialButton saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String savedName = prefs.getString("username", null);

        // If already saved → go directly to MainActivity
        if (savedName != null) {
            Toast.makeText(this, "Welcome back, " + savedName + "!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_welcome);

        nameInputLayout = findViewById(R.id.textInputLayoutName);
        nameInput = findViewById(R.id.editName);
        saveBtn = findViewById(R.id.btnSave);

        saveBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            if (name.isEmpty()) {
                nameInputLayout.setError("Name cannot be empty");
            } else {
                nameInputLayout.setError(null);

                // Save name
                prefs.edit().putString("username", name).apply();

                // Animate transition
                Toast.makeText(this, "Welcome, " + name + "!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
    }
}