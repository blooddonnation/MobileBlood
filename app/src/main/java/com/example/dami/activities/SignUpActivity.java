package com.example.dami.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dami.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.button.MaterialButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class SignUpActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextInputLayout usernameTextInputLayout;
    private TextInputEditText usernameEditText;
    private TextInputLayout passwordTextInputLayout;
    private TextInputEditText passwordEditText;
    private TextInputLayout confirmPasswordTextInputLayout;
    private TextInputEditText confirmPasswordEditText;
    private TextInputLayout emailTextInputLayout;
    private TextInputEditText emailEditText;
    private EditText dateOfBirthEditText;
    private Spinner bloodTypeSpinner;
    private MaterialButton signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        titleTextView = findViewById(R.id.titleTextView);
        usernameTextInputLayout = findViewById(R.id.usernameTextInputLayout);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordTextInputLayout = findViewById(R.id.passwordTextInputLayout);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordTextInputLayout = findViewById(R.id.confirmPasswordTextInputLayout);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        emailTextInputLayout = findViewById(R.id.emailTextInputLayout);
        emailEditText = findViewById(R.id.emailEditText);
        dateOfBirthEditText = findViewById(R.id.dateOfBirthEditText);
        bloodTypeSpinner = findViewById(R.id.bloodTypeSpinner);
        signUpButton = findViewById(R.id.signUpButton);

        // Populate the blood type spinner
        ArrayAdapter<CharSequence> bloodTypeAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.blood_types,
                android.R.layout.simple_spinner_item
        );
        bloodTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodTypeSpinner.setAdapter(bloodTypeAdapter);

        // Date of Birth Date Picker
        dateOfBirthEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        SignUpActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // Month is 0-indexed in DatePickerDialog
                                LocalDate selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                dateOfBirthEditText.setText(selectedDate.format(formatter));
                            }
                        },
                        year, month, day
                );
                datePickerDialog.show();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();
                String email = emailEditText.getText().toString().trim();
                String dateOfBirthString = dateOfBirthEditText.getText().toString();
                String bloodType = bloodTypeSpinner.getSelectedItem().toString();

                // Basic validation (add more robust validation)
                if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty() || dateOfBirthString.isEmpty() || bloodType.equals("Select Blood Type")) {
                    Toast.makeText(SignUpActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if passwords match
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Basic date format validation
                try {
                    LocalDate.parse(dateOfBirthString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                } catch (Exception e) {
                    Toast.makeText(SignUpActivity.this, "Invalid date format (YYYY-MM-DD)", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Here you would typically implement your user registration logic
                // (e.g., sending data to a backend service, Firebase, etc.)
                Toast.makeText(SignUpActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                
                // Navigate to main activity
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Close the sign up activity
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}