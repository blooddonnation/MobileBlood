package com.example.dami.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.dami.R;
import com.example.dami.adapters.RequestAdapter;
import com.example.dami.models.BloodDonationRequestResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DonationsActivity extends AppCompatActivity {

    private RecyclerView donationsRecyclerView;
    private RequestAdapter requestAdapter;
    private RequestQueue requestQueue;

    private static final String TAG = "DonationsActivity";
    private static final String BASE_URL = "http://10.0.2.2:8080/api/requests";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donations);

        setupViews();
        fetchDonationRequests();
    }

    private void setupViews() {
        donationsRecyclerView = findViewById(R.id.donationsRecyclerView);
        donationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        requestAdapter = new RequestAdapter(new ArrayList<>(), 1); // Assuming userId = 1
        donationsRecyclerView.setAdapter(requestAdapter);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        requestQueue = Volley.newRequestQueue(this);
    }

    private void fetchDonationRequests() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                BASE_URL,
                null,
                response -> {
                    Log.d(TAG, "Fetched response: " + response);
                    List<BloodDonationRequestResponse> donationList = parseDonationRequests(response);
                    requestAdapter.updateRequests(donationList);
                },
                error -> Log.e(TAG, "Failed to fetch donation requests", error)
        );

        requestQueue.add(jsonArrayRequest);
    }

    private List<BloodDonationRequestResponse> parseDonationRequests(JSONArray response) {
        List<BloodDonationRequestResponse> donations = new ArrayList<>();

        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject obj = response.getJSONObject(i);
                BloodDonationRequestResponse donation = new BloodDonationRequestResponse(
                        obj.getLong("id"),
                        obj.getString("bloodType"),
                        obj.getString("status"),
                        obj.getDouble("quantity"),
                        obj.getLong("requestedById"),
                        obj.getLong("bloodCenterId")
                );
                donations.add(donation);
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing donation request at index " + i, e);
            }
        }

        return donations;
    }
}
