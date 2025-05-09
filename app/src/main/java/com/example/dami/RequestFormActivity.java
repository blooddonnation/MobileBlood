package com.example.dami;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dami.retrofit.CenterApi;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RequestFormActivity extends AppCompatActivity {
    private AutoCompleteTextView bloodTypeDropdown;
    private TextInputEditText quantityEditText;
    private AutoCompleteTextView bloodCenterDropdown;
    private MaterialButton submitButton;
    private List<BloodCenter> bloodCenters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_form);

        // Initialize views
        ImageButton backButton = findViewById(R.id.backButton);
        bloodTypeDropdown = findViewById(R.id.bloodTypeDropdown);
        quantityEditText = findViewById(R.id.quantityEditText);
        bloodCenterDropdown = findViewById(R.id.bloodCenterDropdown);
        submitButton = findViewById(R.id.submitButton);

        // Set up blood type dropdown
        String[] bloodTypes = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        ArrayAdapter<String> bloodTypeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, bloodTypes);
        bloodTypeDropdown.setAdapter(bloodTypeAdapter);

        // Fetch blood centers from API
        fetchBloodCenters();

        // Set up back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set up submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRequest();
            }
        });

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            v.setPadding(
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            );
            return insets;
        });
    }

    private void fetchBloodCenters() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8082/")  // Replace with your actual backend URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CenterApi centerApi = retrofit.create(CenterApi.class);
        Call<List<BloodCenter>> call = centerApi.getCenters();

        call.enqueue(new Callback<List<BloodCenter>>() {
            @Override
            public void onResponse(Call<List<BloodCenter>> call, Response<List<BloodCenter>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Set the blood centers received from the API
                    bloodCenters = response.body();
                    ArrayAdapter<BloodCenter> centerAdapter = new ArrayAdapter<>(RequestFormActivity.this,
                            android.R.layout.simple_dropdown_item_1line, bloodCenters);
                    bloodCenterDropdown.setAdapter(centerAdapter);
                } else {
                    Toast.makeText(RequestFormActivity.this, "Failed to load blood centers", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<BloodCenter>> call, Throwable t) {
                Toast.makeText(RequestFormActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitRequest() {
        // Get values from form
        String bloodType = bloodTypeDropdown.getText().toString().trim();
        String quantityStr = quantityEditText.getText().toString().trim();
        String selectedCenterName = bloodCenterDropdown.getText().toString().trim();

        // Validate inputs
        if (bloodType.isEmpty() || quantityStr.isEmpty() || selectedCenterName.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Find the selected blood center
        BloodCenter selectedCenter = null;
        for (BloodCenter center : bloodCenters) {
            if (center.getNamecenter().equals(selectedCenterName)) {
                selectedCenter = center;
                break;
            }
        }

        if (selectedCenter == null) {
            Toast.makeText(this, "Please select a valid blood center", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double quantity = Double.parseDouble(quantityStr);
            if (quantity <= 0) {
                Toast.makeText(this, "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create request object
            BloodDonationRequest request = new BloodDonationRequest(
                    bloodType,
                    quantity,
                    selectedCenter.getId()
            );

            // TODO: Send request to backend API
            // For now, just show success message
            Toast.makeText(this, "Request submitted successfully", Toast.LENGTH_SHORT).show();
            finish();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid quantity", Toast.LENGTH_SHORT).show();
        }
    }
}
