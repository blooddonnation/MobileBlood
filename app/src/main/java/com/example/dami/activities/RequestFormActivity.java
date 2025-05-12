package com.example.dami.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dami.R;
import com.example.dami.api.ApiConfig;
import com.example.dami.models.BloodDonationRequest;
import com.example.dami.models.BloodDonationRequestResponse;
import com.example.dami.retrofit.RequestApi;
import com.example.dami.utils.TokenManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RequestFormActivity extends AppCompatActivity {
    private static final String TAG = "RequestFormActivity";
    private AutoCompleteTextView bloodTypeDropdown;
    private TextInputEditText quantityEditText;
    private MaterialButton submitButton;
    private RequestApi requestApi;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_form);

        try {
            // Initialize TokenManager
            tokenManager = new TokenManager(this);

            // Initialize views
            initializeViews();

            // Initialize Retrofit client
            setupRetrofit();

            // Set up blood type dropdown
            setupBloodTypeDropdown();

            // Set up back button
            setupBackButton();

            // Set up submit button
            setupSubmitButton();

            // Handle window insets
            setupWindowInsets();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            Toast.makeText(this, "Error initializing app: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initializeViews() {
        try {
            ImageButton backButton = findViewById(R.id.backButton);
            bloodTypeDropdown = findViewById(R.id.bloodTypeDropdown);
            quantityEditText = findViewById(R.id.quantityEditText);
            submitButton = findViewById(R.id.submitButton);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage());
            throw e;
        }
    }

    private void setupRetrofit() {
        try {
            // Initialize logging interceptor
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
                Log.d("OkHttp", message);
            });
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

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
                                .header("Authorization", "Bearer " + tokenManager.getToken())
                                .method(original.method(), original.body())
                                .build();
                        
                        // Log the final request headers
                        Log.d(TAG, "Request headers: " + request.headers());
                        return chain.proceed(request);
                    })
                    .build();

            // Create Retrofit instance for requests
            Retrofit requestRetrofit = new Retrofit.Builder()
                    .baseUrl(ApiConfig.BASE_URL_Request_Post)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            requestApi = requestRetrofit.create(RequestApi.class);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up Retrofit: " + e.getMessage());
            throw e;
        }
    }

    private void setupBloodTypeDropdown() {
        try {
            String[] bloodTypes = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
            ArrayAdapter<String> bloodTypeAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, bloodTypes);
            bloodTypeDropdown.setAdapter(bloodTypeAdapter);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up blood type dropdown: " + e.getMessage());
        }
    }

    private void setupBackButton() {
        try {
            ImageButton backButton = findViewById(R.id.backButton);
            backButton.setOnClickListener(v -> finish());
        } catch (Exception e) {
            Log.e(TAG, "Error setting up back button: " + e.getMessage());
        }
    }

    private void setupSubmitButton() {
        try {
            submitButton.setOnClickListener(v -> submitRequest());
        } catch (Exception e) {
            Log.e(TAG, "Error setting up submit button: " + e.getMessage());
        }
    }

    private void setupWindowInsets() {
        try {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
                v.setPadding(
                        insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                        insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                        insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                        insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
                );
                return insets;
            });
        } catch (Exception e) {
            Log.e(TAG, "Error setting up window insets: " + e.getMessage());
        }
    }

    private void submitRequest() {
        try {
            // Get values from form
            String bloodType = bloodTypeDropdown.getText() != null ? 
                bloodTypeDropdown.getText().toString().trim() : "";
            String quantityStr = quantityEditText.getText() != null ? 
                quantityEditText.getText().toString().trim() : "";

            // Validate inputs
            if (bloodType.isEmpty() || quantityStr.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double quantity = Double.parseDouble(quantityStr);
                if (quantity <= 0) {
                    Toast.makeText(this, "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create request object with default values
                BloodDonationRequest request = new BloodDonationRequest();
                request.setBloodType(bloodType);
                request.setQuantity(quantity);
                request.setStatus("PENDING");
                request.setRequestedBy(1L); // Default user ID
                request.setBloodCenter(2L); // Blood center ID

                // Log the request body
                Log.d(TAG, "Request body: " + request.toString());
                Log.d(TAG, "Request URL: " + ApiConfig.BASE_URL_Request_Post + "api/requests");

                // Make API call
                Call<BloodDonationRequestResponse> call = requestApi.createRequest(request);
                call.enqueue(new Callback<BloodDonationRequestResponse>() {
                    @Override
                    public void onResponse(Call<BloodDonationRequestResponse> call, Response<BloodDonationRequestResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            BloodDonationRequestResponse responseBody = response.body();
                            Log.d(TAG, "Response received - ID: " + responseBody.getId() + 
                                      ", Blood Type: " + responseBody.getBloodType() + 
                                      ", Status: " + responseBody.getStatus() + 
                                      ", Quantity: " + responseBody.getQuantity() +
                                      ", Requested By: " + responseBody.getRequestedBy() +
                                      ", Blood Center: " + responseBody.getBloodCenter());
                            
                            Toast.makeText(RequestFormActivity.this,
                                    "Request submitted successfully",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            String errorBody = "";
                            try {
                                if (response.errorBody() != null) {
                                    errorBody = response.errorBody().string();
                                    Log.e(TAG, "Error response body: " + errorBody);
                                    Log.e(TAG, "Request URL: " + call.request().url());
                                    Log.e(TAG, "Request method: " + call.request().method());
                                    Log.e(TAG, "Request headers: " + call.request().headers());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Log.e(TAG, "Failed to submit request: " + response.code());
                            Log.e(TAG, "Response headers: " + response.headers().toString());
                            Toast.makeText(RequestFormActivity.this,
                                    "Failed to submit request: " + response.code() + " - " + errorBody,
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BloodDonationRequestResponse> call, Throwable t) {
                        Log.e(TAG, "Error submitting request: " + t.getMessage());
                        Log.e(TAG, "Request URL: " + call.request().url());
                        Log.e(TAG, "Request method: " + call.request().method());
                        Log.e(TAG, "Request headers: " + call.request().headers());
                        Toast.makeText(RequestFormActivity.this,
                                "Error: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid quantity", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in submitRequest: " + e.getMessage());
            Toast.makeText(this, "Error submitting request: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
