package com.example.dami.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.dami.R;
import com.example.dami.models.BloodDonationRequestResponse;
import com.example.dami.utils.TokenManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private final List<BloodDonationRequestResponse> requestList;
    private final long userId;
    private final TokenManager tokenManager;
    private static final String TAG = "RequestAdapter";
    //Map<Long, String> bloodCenterIdToName;
    private final Map<Long, String> bloodCenterIdToName;
    public RequestAdapter(List<BloodDonationRequestResponse> requestList, long userId, TokenManager tokenManager,  Map<Long, String> bloodCenterIdToName) {
        this.requestList = requestList;
        this.userId = userId;
        this.tokenManager = tokenManager;
        this.bloodCenterIdToName = bloodCenterIdToName;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donation, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        BloodDonationRequestResponse request = requestList.get(position);
        Context context = holder.itemView.getContext();

        holder.bloodTypeTextView.setText(request.getBloodType());
        holder.quantityTextView.setText(String.format("%.1f units", request.getQuantity()));
        holder.statusTextView.setText(request.getStatus());
        String centerName = bloodCenterIdToName.get(request.getBloodCenter());
        if (centerName == null) {
            centerName = "Unknown Center";  // fallback
        }
        holder.centerNameTextView.setText(centerName);

        // Status color
        int color;
        switch (request.getStatus().toUpperCase()) {
            case "APPROVED":
                color = context.getResources().getColor(R.color.status_approved);
                break;
            case "PENDING":
                color = context.getResources().getColor(R.color.status_pending);
                break;
            case "REJECTED":
                color = context.getResources().getColor(R.color.status_rejected);
                break;
            default:
                color = context.getResources().getColor(R.color.status_default);
        }
        holder.statusTextView.setTextColor(color);

        holder.donateButton.setOnClickListener(v -> {
            String token = tokenManager.getToken();
            if (token == null || token.isEmpty()) {
                Toast.makeText(context, "You are not logged in!", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "Token being sent: " + token);

            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("recipientType", "bloodcenter");
                jsonBody.put("recipientId", request.getBloodCenter());
                jsonBody.put("recipientName", request.getBloodCenterName());
                jsonBody.put("volume", request.getQuantity());
                jsonBody.put("location", "Casablanca");
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error building donation request", Toast.LENGTH_SHORT).show();
                return;
            }


            String url = "http://10.0.2.2:8081/api/donations"; // Make sure your endpoint is correct
            RequestQueue queue = Volley.newRequestQueue(context);

            JsonObjectRequest postRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonBody,
                    response -> {
                        Toast.makeText(context, "Donation successful!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Response: " + response.toString());
                    },
                    error -> {
                        Log.e(TAG, "Error: " + error.toString());
                        if (error.networkResponse != null) {
                            String body = new String(error.networkResponse.data);
                            Log.e(TAG, "Body: " + body);
                            Toast.makeText(context, "Error: " + body, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "Bearer " + token);
                    return headers;
                }
            };

            queue.add(postRequest);
        });
    }

        @Override
    public int getItemCount() {
        return requestList.size();
    }

    public void updateRequests(List<BloodDonationRequestResponse> newRequests) {
        requestList.clear();
        requestList.addAll(newRequests);
        notifyDataSetChanged();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView bloodTypeTextView;
        TextView quantityTextView;
        TextView statusTextView;
        TextView centerNameTextView;
        Button donateButton;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            bloodTypeTextView = itemView.findViewById(R.id.bloodTypeTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            centerNameTextView = itemView.findViewById(R.id.centerNameTextView);
            donateButton = itemView.findViewById(R.id.donateButton);
        }
    }
}
