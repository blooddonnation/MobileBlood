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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.dami.R;
import com.example.dami.api.ApiConfig;
import com.example.dami.models.BloodCenter;
import com.example.dami.models.BloodDonationRequest;
import com.example.dami.models.BloodDonationRequestResponse;
import com.example.dami.retrofit.RequestApi;
import com.example.dami.utils.TokenManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.util.concurrent.TimeUnit;

public class RequestFormActivity extends AppCompatActivity {
    private static final String TAG = "RequestFormActivity";
    private AutoCompleteTextView bloodTypeDropdown;
    private AutoCompleteTextView centerDropdown;
    private TextInputEditText quantityEditText;
    private MaterialButton submitButton;
    private RequestApi requestApi;
    private List<BloodCenter> centers;
    private RequestQueue requestQueue;
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
            Log.d(TAG, "Initial value of quantityEditText: '" + quantityEditText.getText().toString() + "'");

            // Initialize Retrofit client
            setupRetrofit();

            // Set up blood type dropdown
            setupBloodTypeDropdown();

            // Set up center dropdown
            setupCenterDropdown();

            // Set up back button
            setupBackButton();

            // Set up submit button
            setupSubmitButton();

            // Handle window insets
            setupWindowInsets();

            // Initialize Volley request queue
            requestQueue = Volley.newRequestQueue(this);

            // Fetch centers
            fetchCenters();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            Toast.makeText(this, "Error initializing app: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initializeViews() {
        try {
            ImageButton backButton = findViewById(R.id.backButton);
            bloodTypeDropdown = findViewById(R.id.bloodTypeDropdown);
            centerDropdown = findViewById(R.id.centerDropdown);
            quantityEditText = findViewById(R.id.quantityEditText);
            submitButton = findViewById(R.id.submitButton);

            // Explicitly clear the quantity field on creation
            quantityEditText.setText("");
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
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
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

    private void setupCenterDropdown() {
        try {
            fetchCenters();
        } catch (Exception e) {
            Log.e(TAG, "Error setting up center dropdown: " + e.getMessage());
        }
    }

    private void fetchCenters() {
        String url = ApiConfig.BASE_URL_Banque + "api/centers";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    centers = parseCenters(response);
                    updateCenterDropdown();
                },
                error -> {
                    Log.e(TAG, "Error fetching centers: " + error.getMessage());
                    Toast.makeText(this, "Failed to load centers", Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(request);
    }

    private List<BloodCenter> parseCenters(JSONArray response) {
        List<BloodCenter> centerList = new ArrayList<>();
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject obj = response.getJSONObject(i);
                BloodCenter center = new BloodCenter(
                        obj.getLong("id"),
                        obj.getLong("idAdmin"),
                        obj.getString("latitude"),
                        obj.getString("longitude"),
                        obj.getString("name"),
                        obj.getString("location")
                );
                centerList.add(center);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing centers: " + e.getMessage());
        }
        return centerList;
    }

    private void updateCenterDropdown() {
        if (centers != null && !centers.isEmpty()) {
            ArrayAdapter<BloodCenter> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, centers);
            centerDropdown.setAdapter(adapter);
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

            Log.d(TAG, "Raw quantity input: '" + quantityStr + "'");

            // Get selected center
            String selectedCenterName = centerDropdown.getText() != null ?
                    centerDropdown.getText().toString().trim() : "";
            BloodCenter selectedCenter = null;

            // Find the center object that matches the selected name
            if (centers != null) {
                for (BloodCenter center : centers) {
                    if (center.getName().equals(selectedCenterName)) {
                        selectedCenter = center;
                        break;
                    }
                }
            }

            // Validate inputs
            if (bloodType.isEmpty() || quantityStr.isEmpty() || selectedCenter == null) {
                Log.d(TAG, "Validation failed - bloodType: " + bloodType + ", quantity: " + quantityStr + ", center: " + (selectedCenter != null));
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                // **Improved Quantity Parsing:**
                // Remove any non-numeric characters except the decimal point.
                // This ensures we only try to parse a string that *should* be a number.
                String cleanedQuantityStr = quantityStr.replaceAll("[^0-9.]", "");
                Log.d(TAG, "Cleaned quantity string: '" + cleanedQuantityStr + "'");

                if (cleanedQuantityStr.isEmpty()) {
                    Log.e(TAG, "Quantity string is empty after cleaning");
                    Toast.makeText(this, "Please enter a valid number for quantity", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the cleaned string contains only one decimal point
                int decimalCount = cleanedQuantityStr.length() - cleanedQuantityStr.replace(".", "").length();
                if (decimalCount > 1) {
                    Log.e(TAG, "Multiple decimal points found in quantity");
                    Toast.makeText(this, "Please enter a valid number with only one decimal point", Toast.LENGTH_SHORT).show();
                    return;
                }

                double quantity = Double.parseDouble(cleanedQuantityStr);
                Log.d(TAG, "Successfully parsed quantity: " + quantity);

                if (quantity <= 0) {
                    Log.d(TAG, "Quantity is not positive: " + quantity);
                    Toast.makeText(this, "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get user ID from TokenManager
                String userIdStr = tokenManager.getUserId();
                if (userIdStr == null) {
                    Log.e(TAG, "User ID is null - user not logged in");
                    Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d(TAG, "Creating request with quantity: " + quantity + ", bloodType: " + bloodType + ", centerId: " + selectedCenter.getId() + " user id :" + userIdStr);

                // Create request object
                BloodDonationRequest request = new BloodDonationRequest();
                request.setBloodType(bloodType);
                request.setQuantity(quantity);
                request.setStatus("PENDING");
                request.setRequestedBy(Long.valueOf(userIdStr));
                request.setBloodCenter(selectedCenter.getId());

                Log.d(TAG, "Request object created: " + request.toString());

                // Make API call
                Call<BloodDonationRequestResponse> call = requestApi.createRequest(request);

                call.enqueue(new Callback<BloodDonationRequestResponse>() {
                    @Override
                    public void onResponse(Call<BloodDonationRequestResponse> call,
                                           Response<BloodDonationRequestResponse> response) {
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "Request submitted successfully");
                            Toast.makeText(RequestFormActivity.this,
                                    "Request submitted successfully",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            String errorMessage = "Failed to submit request";
                            try {
                                if (response.errorBody() != null) {
                                    String errorBody = response.errorBody().string();
                                    Log.e(TAG, "Error response body: " + errorBody);
                                    errorMessage += ": " + errorBody;
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error reading error body", e);
                            }
                            Log.e(TAG, "Request submission failed with code: " + response.code());
                            Toast.makeText(RequestFormActivity.this,
                                    errorMessage,
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BloodDonationRequestResponse> call, Throwable t) {
                        Log.e(TAG, "Network error: " + t.getMessage(), t);
                        Toast.makeText(RequestFormActivity.this,
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });

            } catch (NumberFormatException e) {
                Log.e(TAG, "Error parsing quantity: " + e.getMessage() + " for input: '" + quantityStr + "'");
                Toast.makeText(this, "Please enter a valid number for quantity", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error submitting request", e);
            Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show();
        }
    }
}