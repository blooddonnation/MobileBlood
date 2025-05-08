package com.example.dami;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<DonationHistory> historyList;

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
        DonationHistory history = historyList.get(position);
        holder.centerNameTextView.setText(history.getCenterName());
        holder.dateTextView.setText(history.getDate());
        holder.bloodTypeTextView.setText(history.getBloodType());
        holder.statusTextView.setText(history.getStatus());
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView centerNameTextView;
        TextView dateTextView;
        TextView bloodTypeTextView;
        TextView statusTextView;

        HistoryViewHolder(View itemView) {
            super(itemView);
            centerNameTextView = itemView.findViewById(R.id.centerNameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            bloodTypeTextView = itemView.findViewById(R.id.bloodTypeTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
        }
    }
} 