package com.example.dami.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dami.R;
import com.example.dami.api.ApiConfig;
import com.example.dami.models.JwtResponse;
import com.example.dami.models.LoginRequest;
import com.example.dami.retrofit.AuthApi;
import com.example.dami.utils.TokenManager;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = "SignInActivity";
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private Button signInButton;
    private Button signUpButton;
    private AuthApi authApi;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        try {
            // Initialize TokenManager
            tokenManager = new TokenManager(this);

            // Initialize views
            initializeViews();

            // Check if user is already logged in
            checkExistingSession();

            // Initialize Retrofit
            setupRetrofit();

            // Setup buttons
            setupButtons();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            Toast.makeText(this, "Error initializing app: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initializeViews() {
        try {
            emailInput = findViewById(R.id.emailEditText);
            passwordInput = findViewById(R.id.passwordEditText);
            signInButton = findViewById(R.id.signInButton);
            signUpButton = findViewById(R.id.signUpButton);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage());
            throw e;
        }
    }

    private void checkExistingSession() {
        try {
            if (tokenManager.isLoggedIn()) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking session: " + e.getMessage());
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

    private void setupButtons() {
        try {
            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String username = emailInput.getText() != null ? 
                            emailInput.getText().toString().trim() : "";
                        String password = passwordInput.getText() != null ? 
                            passwordInput.getText().toString().trim() : "";

                        if (username.isEmpty() || password.isEmpty()) {
                            Toast.makeText(SignInActivity.this, 
                                "Please fill in all fields", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        LoginRequest loginRequest = new LoginRequest(username, password);
                        authApi.login(loginRequest).enqueue(new Callback<JwtResponse>() {
                            @Override
                            public void onResponse(Call<JwtResponse> call, Response<JwtResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    // Store the token and user ID
                                    String token = response.body().getToken();
                                    Long userId = response.body().getUserId();
                                    tokenManager.saveToken(token);
                                    tokenManager.saveUserId(username); // Using username as user ID
                                    //tokenManager.saveUserId(userId);
                                    // Navigate to main activity
                                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
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
                                    Log.e(TAG, "Login failed with code: " + response.code());
                                    Log.e(TAG, "Response headers: " + response.headers().toString());
                                    
                                    Toast.makeText(SignInActivity.this,
                                        "Login failed: " + response.code() + " - " + errorBody,
                                        Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<JwtResponse> call, Throwable t) {
                                String errorMessage = t.getMessage();
                                Log.e(TAG, "Network error: " + errorMessage, t);
                                Toast.makeText(SignInActivity.this,
                                    "Error: " + errorMessage,
                                    Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "Error during login: " + e.getMessage());
                        Toast.makeText(SignInActivity.this,
                            "Login failed: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    }
                }
            });

            signUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Error navigating to sign up: " + e.getMessage());
                        Toast.makeText(SignInActivity.this,
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error setting up buttons: " + e.getMessage());
        }
    }
} 