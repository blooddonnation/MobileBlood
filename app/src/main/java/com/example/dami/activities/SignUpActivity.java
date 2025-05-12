package com.example.dami.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.dami.api.ApiConfig;
import com.example.dami.models.RegisterRequest;
import com.example.dami.retrofit.AuthApi;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.button.MaterialButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
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
    private AuthApi authApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        try {
            // Initialize views
            initializeViews();

            // Setup blood type spinner
            setupBloodTypeSpinner();

            // Setup date picker
            setupDatePicker();

            // Initialize Retrofit with logging
            setupRetrofit();

            // Setup sign up button
            setupSignUpButton();

            // Setup window insets
            setupWindowInsets();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            Toast.makeText(this, "Error initializing app: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initializeViews() {
        try {
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
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage());
            throw e;
        }
    }

    private void setupBloodTypeSpinner() {
        try {
            ArrayAdapter<CharSequence> bloodTypeAdapter = ArrayAdapter.createFromResource(
                    this,
                    R.array.blood_types,
                    android.R.layout.simple_spinner_item
            );
            bloodTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bloodTypeSpinner.setAdapter(bloodTypeAdapter);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up blood type spinner: " + e.getMessage());
        }
    }

    private void setupDatePicker() {
        try {
            dateOfBirthEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                SignUpActivity.this,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                        try {
                                            LocalDate selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                            dateOfBirthEditText.setText(selectedDate.format(formatter));
                                        } catch (Exception e) {
                                            Log.e(TAG, "Error setting date: " + e.getMessage());
                                            Toast.makeText(SignUpActivity.this, 
                                                "Error setting date", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                },
                                year, month, day
                        );
                        datePickerDialog.show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error showing date picker: " + e.getMessage());
                        Toast.makeText(SignUpActivity.this, 
                            "Error showing date picker", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error setting up date picker: " + e.getMessage());
        }
    }

    private void setupRetrofit() {
        try {
            // Initialize logging interceptor
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
                Log.d("OkHttp", message);
            });
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Log the base URL
            Log.d(TAG, "Using base URL: " + ApiConfig.AUTH_BASE_URL);

            // Create OkHttpClient with interceptors
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(chain -> {
                        okhttp3.Request original = chain.request();
                        // Log the full URL and headers
                        Log.d(TAG, "Making request to: " + original.url());
                        Log.d(TAG, "Request method: " + original.method());
                        
                        okhttp3.Request request = original.newBuilder()
                                .header("Content-Type", "application/json")
                                .header("Accept", "*/*")
                                .header("Accept-Encoding", "gzip, deflate, br")
                                .header("Connection", "keep-alive")
                                .header("User-Agent", "PostmanRuntime/7.32.3")
                                .method(original.method(), original.body())
                                .build();
                        
                        // Log the final request headers
                        Log.d(TAG, "Request headers: " + request.headers());
                        return chain.proceed(request);
                    })
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            // Create Retrofit instance
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConfig.AUTH_BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            authApi = retrofit.create(AuthApi.class);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up Retrofit: " + e.getMessage());
            throw e;
        }
    }

    private void setupSignUpButton() {
        try {
            signUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String username = usernameEditText.getText() != null ? 
                            usernameEditText.getText().toString().trim() : "";
                        String password = passwordEditText.getText() != null ? 
                            passwordEditText.getText().toString().trim() : "";
                        String confirmPassword = confirmPasswordEditText.getText() != null ? 
                            confirmPasswordEditText.getText().toString() : "";
                        String email = emailEditText.getText() != null ? 
                            emailEditText.getText().toString().trim() : "";
                        String dateOfBirthStr = dateOfBirthEditText.getText() != null ? 
                            dateOfBirthEditText.getText().toString().trim() : "";
                        String bloodType = bloodTypeSpinner.getSelectedItem() != null ? 
                            bloodTypeSpinner.getSelectedItem().toString() : "";

                        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || 
                            email.isEmpty() || dateOfBirthStr.isEmpty() || 
                            bloodType.equals("Select Blood Type")) {
                            Toast.makeText(SignUpActivity.this, 
                                "Please fill in all fields", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!password.equals(confirmPassword)) {
                            Toast.makeText(SignUpActivity.this, 
                                "Passwords do not match", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Parse date of birth
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDate dateOfBirth = LocalDate.parse(dateOfBirthStr, formatter);

                        // Create registration request
                        RegisterRequest registerRequest = new RegisterRequest(
                            username,
                            password,
                            email,
                            bloodType,
                            dateOfBirth,
                            "USER" // Default role
                        );

                        // Log the request body
                        Log.d(TAG, "Request body: " + registerRequest.toString());

                        // Make API call
                        authApi.register(registerRequest).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this,
                                        "Registration successful! Please sign in.",
                                        Toast.LENGTH_LONG).show();
                                    // Navigate to sign in screen
                                    Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    String errorBody = "";
                                    try {
                                        if (response.errorBody() != null) {
                                            errorBody = response.errorBody().string();
                                            Log.e(TAG, "Error response body: " + errorBody);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    Log.e(TAG, "Registration failed with code: " + response.code());
                                    Log.e(TAG, "Response headers: " + response.headers().toString());
                                    
                                    Toast.makeText(SignUpActivity.this,
                                        "Registration failed: " + response.code() + " - " + errorBody,
                                        Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                String errorMessage = t.getMessage();
                                Log.e(TAG, "Network error: " + errorMessage, t);
                                Toast.makeText(SignUpActivity.this,
                                    "Error: " + errorMessage,
                                    Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "Error during registration: " + e.getMessage());
                        Toast.makeText(SignUpActivity.this,
                            "Registration failed: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error setting up sign up button: " + e.getMessage());
        }
    }

    private void setupWindowInsets() {
        try {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        } catch (Exception e) {
            Log.e(TAG, "Error setting up window insets: " + e.getMessage());
        }
    }
}