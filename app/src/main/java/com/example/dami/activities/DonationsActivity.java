package com.example.dami.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.dami.R;
import com.example.dami.adapters.RequestAdapter;
import com.example.dami.models.BloodCenter;
import com.example.dami.models.BloodDonationRequestResponse;
import com.example.dami.utils.TokenManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DonationsActivity extends AppCompatActivity {

    private RecyclerView donationsRecyclerView;
    private RequestAdapter requestAdapter;
    private RequestQueue requestQueue;
    private TokenManager tokenManager;
    private List<BloodCenter> allBloodCentersList = new ArrayList<>();
    private Map<Long, String> bloodCenterIdToName = new HashMap<>();

    private static final String TAG = "DonationsActivity";
    private static final String BASE_URL = "http://10.0.2.2:8084/api/requests";
    private static final String CENTERS_URL = "http://10.0.2.2:8083/api/centers";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donations);

        tokenManager = new TokenManager(this);
        setupViews();
        setupBackButton();
        fetchDonationRequests();
        fetchAllBloodCenters();
    }

    private void setupViews() {
        ImageButton backButton = findViewById(R.id.backButton);
        donationsRecyclerView = findViewById(R.id.donationsRecyclerView);
        donationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        String userIdString = tokenManager.getUserId();  // e.g. "123"
        long userId = Long.parseLong(userIdString);


        requestAdapter = new RequestAdapter(new ArrayList<>(), userId, tokenManager, bloodCenterIdToName);
        donationsRecyclerView.setAdapter(requestAdapter);

        requestQueue = Volley.newRequestQueue(this);
    }

    private void setupBackButton() {
        ImageButton backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        } else {
            Log.e(TAG, "Back button is null. Check ID in layout.");
        }
    }
    private void fetchAllBloodCenters() {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                CENTERS_URL,
                null,
                response -> {
                    try {
                        allBloodCentersList.clear();
                        bloodCenterIdToName.clear();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            BloodCenter center = new BloodCenter();
                            center.setId(obj.getLong("id"));
                            center.setName(obj.getString("name"));
                            allBloodCentersList.add(center);

                            bloodCenterIdToName.put(center.getId(), center.getName());
                        }

                        // Now that centers are loaded, fetch requests
                        fetchDonationRequests();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to parse blood centers", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Failed to fetch blood centers", error);
                    Toast.makeText(this, "Failed to load blood centers", Toast.LENGTH_LONG).show();
                }
        );

        requestQueue.add(request);
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
                error -> {
                    Log.e(TAG, "Failed to fetch donation requests", error);
                    String errorMessage = "Failed to fetch donation requests";
                    if (error instanceof AuthFailureError) {
                        errorMessage = "Authentication failed. Please log in again.";
                    } else if (error.networkResponse != null) {
                        errorMessage = "Server error: " + error.networkResponse.statusCode;
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = tokenManager.getToken();
                if (token != null) {
                    headers.put("Authorization", "Bearer " + token);
                }
                return headers;
            }
        };

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
