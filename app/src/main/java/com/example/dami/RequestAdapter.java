package com.example.dami;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private List<BloodDonationRequest> requests;

    public RequestAdapter(List<BloodDonationRequest> requests) {
        this.requests = requests;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BloodDonationRequest request = requests.get(position);
        holder.bind(request);
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public void updateRequests(List<BloodDonationRequest> newRequests) {
        this.requests = newRequests;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView bloodTypeTextView;
        private final TextView quantityTextView;
        private final TextView centerTextView;
        private final TextView statusTextView;
        private final TextView dateTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bloodTypeTextView = itemView.findViewById(R.id.bloodTypeTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            centerTextView = itemView.findViewById(R.id.centerTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }

        public void bind(BloodDonationRequest request) {
            bloodTypeTextView.setText(request.getBloodType());
            quantityTextView.setText(String.format("%.1f units", request.getQuantity()));
            centerTextView.setText(request.getBloodCenter() != null ? 
                request.getBloodCenter().getNamecenter() : "Center not specified");
            statusTextView.setText(request.getStatus());
            dateTextView.setText(request.getCreatedAt().format(DATE_FORMATTER));
        }
    }
} 