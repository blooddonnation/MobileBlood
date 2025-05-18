package com.example.dami.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dami.R;
import com.example.dami.models.DonationHistory;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private final List<DonationHistory> historyList;

    public HistoryAdapter(List<DonationHistory> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        DonationHistory donation = historyList.get(position);
        Context context = holder.itemView.getContext();

        holder.dateTextView.setText(donation.getDonationDate());
        holder.centerNameTextView.setText(donation.getRecipientName());
        holder.bloodTypeTextView.setText(donation.getLocation());

        // ✅ Fix: Convert volume (int) to string
        holder.quantityTextView.setText(String.valueOf(donation.getVolume()) + " mL");

        // Optional: Color customization
        int color = context.getResources().getColor(R.color.status_approved);
        holder.bloodTypeTextView.setTextColor(color);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void updateHistory(List<DonationHistory> newHistory) {
        historyList.clear();
        historyList.addAll(newHistory);
        notifyDataSetChanged();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView centerNameTextView;
        TextView bloodTypeTextView;
        TextView quantityTextView;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            centerNameTextView = itemView.findViewById(R.id.centerNameTextView);
            bloodTypeTextView = itemView.findViewById(R.id.bloodTypeTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
        }
    }
}
