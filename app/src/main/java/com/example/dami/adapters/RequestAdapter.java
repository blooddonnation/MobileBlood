package com.example.dami.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.dami.R;
import com.example.dami.models.BloodDonationRequestResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private List<BloodDonationRequestResponse> requestList;

    public RequestAdapter(List<BloodDonationRequestResponse> requestList) {
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        BloodDonationRequestResponse request = requestList.get(position);
        holder.bloodTypeTextView.setText(request.getBloodType());
        holder.quantityTextView.setText(String.format("%.1f units", request.getQuantity()));
        holder.statusTextView.setText(request.getStatus());
        holder.centerNameTextView.setText(String.valueOf(request.getBloodCenter()));
        // Set status color based on the status
        int statusColor;
        switch (request.getStatus().toUpperCase()) {
            case "APPROVED":
                statusColor = holder.itemView.getContext().getResources().getColor(R.color.status_approved);
                break;
            case "PENDING":
                statusColor = holder.itemView.getContext().getResources().getColor(R.color.status_pending);
                break;
            case "REJECTED":
                statusColor = holder.itemView.getContext().getResources().getColor(R.color.status_rejected);
                break;
            default:
                statusColor = holder.itemView.getContext().getResources().getColor(R.color.status_default);
        }
        holder.statusTextView.setTextColor(statusColor);
        holder.donateButton.setOnClickListener(v -> {
            // 🔐 You can get donorId from SharedPreferences or pass it to the adapter
            SharedPreferences prefs = v.getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            long donorId = prefs.getLong("donorId", -1); // Make sure to store this during login

            if (donorId == -1) {
                Toast.makeText(v.getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            long requestId = request.getId(); // this comes from the current request item

            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("donorId", donorId);
                jsonBody.put("donationRequest", requestId);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            String url = "http://10.0.2.2:8080/api/donations"; // replace with your actual server IP

            RequestQueue queue = Volley.newRequestQueue(v.getContext());

            JsonObjectRequest requestObj = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    response -> {
                        Toast.makeText(v.getContext(), "Donation successful!", Toast.LENGTH_SHORT).show();
                    },
                    error -> {
                        Toast.makeText(v.getContext(), "Donation failed: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
            );

            queue.add(requestObj);
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public void updateRequests(List<BloodDonationRequestResponse> newRequests) {
//        this.requestList = newRequests;
//        notifyDataSetChanged();
        // Clear the old data
        this.requestList.clear();
        // Add new data
        this.requestList.addAll(newRequests);
        // Notify the adapter that the data set has changed
        notifyDataSetChanged();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView bloodTypeTextView;
        TextView quantityTextView;
        TextView statusTextView;
        TextView centerNameTextView;
        TextView centerLocationTextView;
        Button donateButton;
        RequestViewHolder(View itemView) {
            super(itemView);
            bloodTypeTextView = itemView.findViewById(R.id.bloodTypeTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            centerNameTextView = itemView.findViewById(R.id.centerNameTextView);
            centerLocationTextView = itemView.findViewById(R.id.centerLocationTextView);
            donateButton = itemView.findViewById(R.id.donateButton);
        }
    }

}