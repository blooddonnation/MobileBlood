package com.example.dami.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dami.R;
import com.example.dami.models.DonationHistory;

import java.util.List;

public class DonationAdapter extends RecyclerView.Adapter<DonationAdapter.DonationViewHolder> {
    private List<DonationHistory> donationList;

    public DonationAdapter(List<DonationHistory> donationList) {
        this.donationList = donationList;
    }

    @NonNull
    @Override
    public DonationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_donation, parent, false);
        return new DonationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonationViewHolder holder, int position) {
        DonationHistory donation = donationList.get(position);
        holder.centerNameTextView.setText(donation.getRecipientName());
        holder.dateTextView.setText(donation.getDonationDate());
        holder.bloodTypeTextView.setText(donation.getLocation());
        holder.statusTextView.setText(donation.getLocation());
        

    }

    @Override
    public int getItemCount() {
        return donationList.size();
    }

    public void updateDonations(List<DonationHistory> newDonations) {
        this.donationList = newDonations;
        notifyDataSetChanged();
    }

    static class DonationViewHolder extends RecyclerView.ViewHolder {
        TextView centerNameTextView;
        TextView dateTextView;
        TextView bloodTypeTextView;
        TextView statusTextView;

        DonationViewHolder(View itemView) {
            super(itemView);
            centerNameTextView = itemView.findViewById(R.id.centerNameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            bloodTypeTextView = itemView.findViewById(R.id.bloodTypeTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
        }
    }
} 