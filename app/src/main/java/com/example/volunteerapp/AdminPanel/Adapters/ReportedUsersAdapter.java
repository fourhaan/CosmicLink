package com.example.volunteerapp.AdminPanel.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.volunteerapp.AdminPanel.Models.ReportedUser;
import com.example.volunteerapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// ReportedUsersAdapter.java
public class ReportedUsersAdapter extends RecyclerView.Adapter<ReportedUsersAdapter.ReportedUserViewHolder> {

    private Context context;
    private List<ReportedUser> reportedUsers;

    public ReportedUsersAdapter(Context context, List<ReportedUser> reportedUsers) {
        this.context = context;
        this.reportedUsers = reportedUsers;
    }

    @NonNull
    @Override
    public ReportedUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reported_user, parent, false);
        return new ReportedUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportedUserViewHolder holder, int position) {
        ReportedUser reportedUser = reportedUsers.get(position);

        // Set data to UI elements in the ViewHolder
        holder.textViewReportedUserId.setText("Reported User ID: " + reportedUser.getReportedUserId());
        holder.textViewReportedByUserId.setText("Reported By: " + reportedUser.getReportedByUserId());

        // Format timestamp to a readable date and time
        String formattedTimestamp = getFormattedTimestamp(reportedUser.getTimestamp());
        holder.textViewTimestamp.setText("Timestamp: " + formattedTimestamp);
    }

    @Override
    public int getItemCount() {
        return reportedUsers.size();
    }

    public class ReportedUserViewHolder extends RecyclerView.ViewHolder {
        TextView textViewReportedUserId;
        TextView textViewReportedByUserId;
        TextView textViewTimestamp;

        public ReportedUserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewReportedUserId = itemView.findViewById(R.id.textViewReportedUserId);
            textViewReportedByUserId = itemView.findViewById(R.id.textViewReportedByUserId);
            textViewTimestamp = itemView.findViewById(R.id.textViewTimestamp);
        }
    }

    // Helper method to format timestamp
    private String getFormattedTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}

