package com.example.dami;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {
    private List<BloodDonationRequest> requests;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    public RequestAdapter(List<BloodDonationRequest> requests) {
        this.requests = requests;
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
        BloodDonationRequest request = requests.get(position);
        holder.bind(request);
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        private TextView bloodTypeTextView;
        private TextView quantityTextView;
        private TextView statusTextView;
        private TextView centerTextView;
        private TextView dateTextView;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            bloodTypeTextView = itemView.findViewById(R.id.bloodTypeTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            centerTextView = itemView.findViewById(R.id.centerTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }

        public void bind(BloodDonationRequest request) {
            bloodTypeTextView.setText(request.getBloodType());
            quantityTextView.setText(String.format("%.1f units", request.getQuantity()));
            statusTextView.setText(request.getStatus());
            if (request.getBloodCenter() != null) {
<<<<<<< HEAD
                centerTextView.setText(request.getBloodCenter().getName());
=======
                centerTextView.setText(request.getBloodCenter().getNamecenter());
>>>>>>> origin/rajae
            }
            dateTextView.setText(request.getCreatedAt().format(DATE_FORMATTER));
        }
    }
} 