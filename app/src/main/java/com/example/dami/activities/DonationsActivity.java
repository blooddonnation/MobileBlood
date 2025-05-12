package com.example.dami.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dami.R;
import com.example.dami.adapters.DonationAdapter;
import com.example.dami.models.DonationHistory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DonationsActivity extends AppCompatActivity {
    private RecyclerView donationsRecyclerView;
    private DonationAdapter donationAdapter;
    private List<DonationHistory> donations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_donations);

        // Set up back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set up RecyclerView for donations list
        donationsRecyclerView = findViewById(R.id.donationsRecyclerView);
        donationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Initialize donations list and adapter
        donations = new ArrayList<>();
        donationAdapter = new DonationAdapter(donations);
        donationsRecyclerView.setAdapter(donationAdapter);

        // Load sample data
        loadSampleData();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadSampleData() {
        // Add sample donations
        donations.add(createSampleDonation("Central Blood Bank", "A+", "15/03/2024", "COMPLETED"));
        donations.add(createSampleDonation("City Hospital", "O-", "20/03/2024", "SCHEDULED"));
        donations.add(createSampleDonation("Regional Medical Center", "B+", "25/03/2024", "SCHEDULED"));
        donations.add(createSampleDonation("Community Health Center", "AB+", "10/03/2024", "COMPLETED"));
        donations.add(createSampleDonation("General Hospital", "A-", "05/03/2024", "CANCELLED"));
        
        donationAdapter.notifyDataSetChanged();
    }

    private DonationHistory createSampleDonation(String centerName, String bloodType, 
            String date, String status) {
        return new DonationHistory(centerName, bloodType, date, status);
    }
} 