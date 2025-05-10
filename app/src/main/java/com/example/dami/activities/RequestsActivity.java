package com.example.dami.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.dami.R;
import com.example.dami.adapters.RequestAdapter;
import com.example.dami.models.BloodDonationRequest;

public class RequestsActivity extends AppCompatActivity {
    private RecyclerView requestsRecyclerView;
    private RequestAdapter requestAdapter;
    private List<BloodDonationRequest> requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        // Initialize views
        ImageButton backButton = findViewById(R.id.backButton);
        requestsRecyclerView = findViewById(R.id.requestsRecyclerView);
        FloatingActionButton addRequestFab = findViewById(R.id.addRequestFab);

        // Set up RecyclerView
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        requests = new ArrayList<>();
        requestAdapter = new RequestAdapter(requests);
        requestsRecyclerView.setAdapter(requestAdapter);

        // Set up back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set up FAB
        addRequestFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RequestsActivity.this, RequestFormActivity.class);
                startActivity(intent);
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

        // Load sample data
        loadSampleData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO: Refresh the list from the database
    }

    private void loadSampleData() {
        // Add some sample requests
        requests.add(createSampleRequest("A+", 2.0, "Central Blood Bank", "Pending"));
        requests.add(createSampleRequest("O-", 1.5, "City Hospital", "Approved"));
        requests.add(createSampleRequest("B+", 3.0, "Regional Medical Center", "Pending"));
        requestAdapter.notifyDataSetChanged();
    }

    private BloodDonationRequest createSampleRequest(String bloodType, double quantity, 
            String center, String status) {
        BloodDonationRequest request = new BloodDonationRequest();
        request.setBloodType(bloodType);
        request.setQuantity(quantity);
        request.setStatus(status);
        request.setCreatedAt(LocalDateTime.now());
        // TODO: Set blood center and user
        return request;
    }
} 