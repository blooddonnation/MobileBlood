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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.dami.R;
import com.example.dami.adapters.HistoryAdapter;
import com.example.dami.models.BloodCenter;
import com.example.dami.models.DonationHistory;
import com.example.dami.utils.TokenManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private TokenManager tokenManager;
    private List<BloodCenter> allBloodCentersList = new ArrayList<>();
    private static final String TAG = "HistoryActivity";
    private static final String HISTORY_URL = "http://10.0.2.2:8081/api/donations/history";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        tokenManager = new TokenManager(this);
        setupRecyclerView();

        fetchDonationHistory();
        setupBackButton();
    }
    private String getBloodCenterNameById(Long id) {
        for (BloodCenter center : allBloodCentersList) {
            if (center.getId().equals(id)) {
                return center.getName();
            }
        }
        return "Unknown Center";
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.historyRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
    }

    private void setupBackButton() {
        ImageButton backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        } else {
            Log.e(TAG, "Back button is null. Check ID in layout.");
        }
    }

    private void fetchDonationHistory() {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                HISTORY_URL,
                null,
                response -> {
                    List<DonationHistory> historyList = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            String recipientName = obj.getString("recipientName");
                            String donationDate = obj.getString("donationDate").substring(0, 10); // only date
                            int volume = obj.getInt("volume");
                            String location = obj.getString("location");

                            historyList.add(new DonationHistory(recipientName, donationDate, volume, location));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.updateHistory(historyList);
                },
                error -> {
                    Log.e(TAG, "Error fetching donation history", error);
                    Toast.makeText(this, "Failed to fetch history", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + tokenManager.getToken());
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
