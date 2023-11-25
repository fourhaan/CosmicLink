package com.example.volunteerapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.volunteerapp.Models.TaskModel;
import com.example.volunteerapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TaskAddAdapter extends RecyclerView.Adapter<TaskAddAdapter.NoteViewHolder> {
    private Context context;
    private List<TaskModel> task;

    public TaskAddAdapter(Context context, List<TaskModel> task) {
        this.context = context;
        this.task = task;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_task_item, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        TaskModel tasks = task.get(position);
        holder.titleTextView.setText("Task #"+tasks.getTasknum()+" : "+tasks.getTitle());
        holder.contentTextView.setText(tasks.getContent());
        String taskhrs = "Task Hours required : "+String.valueOf(tasks.getTaskhours())+" hours";
        holder.taskhourTextView.setText(taskhrs);

        long currentTime = System.currentTimeMillis();
        LocalDateTime currentDateTime = Instant.ofEpochMilli(currentTime)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String DateTime = currentDateTime.format(formatter);

        holder.timestampTextView.setText(DateTime);

        // Check the value of the 'completed' boolean
        if (tasks.isCompleted()) {
            holder.completed.setVisibility(View.VISIBLE);
            holder.notcompleted.setVisibility(View.GONE);
        } else {
            holder.completed.setVisibility(View.GONE);
            holder.notcompleted.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return task.size();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, contentTextView, timestampTextView,taskhourTextView;
        Button completed,notcompleted;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            contentTextView = itemView.findViewById(R.id.content_text_view);
            timestampTextView = itemView.findViewById(R.id.timestamp_text_view);
            taskhourTextView = itemView.findViewById(R.id.task_hour);
            completed = itemView.findViewById(R.id.completedBtn);
            notcompleted = itemView.findViewById(R.id.notcompletedBtn);
        }


    }
}