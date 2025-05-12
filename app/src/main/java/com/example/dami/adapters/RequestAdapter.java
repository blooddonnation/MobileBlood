package com.example.dami.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.example.dami.R;
import com.example.dami.models.BloodDonationRequestResponse;

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
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public void updateRequests(List<BloodDonationRequestResponse> newRequests) {
        this.requestList = newRequests;
        notifyDataSetChanged();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView bloodTypeTextView;
        TextView quantityTextView;
        TextView statusTextView;

        RequestViewHolder(View itemView) {
            super(itemView);
            bloodTypeTextView = itemView.findViewById(R.id.bloodTypeTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
        }
    }
} 