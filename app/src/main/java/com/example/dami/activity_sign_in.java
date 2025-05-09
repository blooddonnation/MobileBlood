package com.example.dami;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class activity_sign_in extends AppCompatActivity {
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        
        // Find views
        Button signInButton = findViewById(R.id.signInButton);
        TextView noAccountText = findViewById(R.id.noAccountText);
        ImageView logoImageView = findViewById(R.id.logoImageView);
        TextInputEditText emailEditText = findViewById(R.id.emailEditText);
        TextInputEditText passwordEditText = findViewById(R.id.passwordEditText);
        
        // Start the pulse animation
        Animation pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse);
        logoImageView.startAnimation(pulseAnimation);
        
        // Set up click listeners
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString();

                // Test login credentials
                if (email.equals("aa") && password.equals("aa")) {
                    // Set logged in to true
                    SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                    sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, true).apply();
                    
                    // Navigate to main activity
                    Intent intent = new Intent(activity_sign_in.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(activity_sign_in.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        noAccountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to sign up
                Intent intent = new Intent(activity_sign_in.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}