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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donations); // Make sure this is the layout with RecyclerView

        donationsRecyclerView = findViewById(R.id.donationsRecyclerView);
        donationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        requestAdapter = new RequestAdapter(new ArrayList<>());
        donationsRecyclerView.setAdapter(requestAdapter);

        // Set up the back button listener
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Finish the current activity and return to the previous one (MainActivity)
            onBackPressed();
        });

        fetchDonationRequests();
    }

    private void fetchDonationRequests() {
        String url = "http://10.0.2.2:8080/api/requests"; // Make sure the URL is correct

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d("Response", "Response received: " + response.toString());  // Log the response
                    List<BloodDonationRequestResponse> donations = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);

                            Long id = obj.getLong("id");
                            String bloodType = obj.getString("bloodType");
                            String status = obj.getString("status");
                            double quantity = obj.getDouble("quantity");
                            Long requestedBy = obj.getLong("requestedById");
                            Long bloodCenter = obj.getLong("bloodCenterId");

                            BloodDonationRequestResponse donation = new BloodDonationRequestResponse(
                                    id, bloodType, status, quantity, requestedBy, bloodCenter
                            );

                            donations.add(donation);
                        } catch (JSONException e) {
                            Log.e("JSON Error", "Parsing error", e);
                        }
                    }
                    if (donations.isEmpty()) {
                        Log.d("Response", "No donations found.");
                    } else {
                        Log.d("Response", "Donations list size: " + donations.size());
                    }
                    requestAdapter.updateRequests(donations);  // Update adapter with fetched data
                },
                error -> Log.e("Volley Error", "Request failed", error));

        queue.add(request);
    }

}
