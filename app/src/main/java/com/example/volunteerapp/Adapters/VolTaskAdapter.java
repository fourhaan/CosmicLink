package com.example.volunteerapp.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.volunteerapp.Models.TaskModel;
import com.example.volunteerapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VolTaskAdapter extends RecyclerView.Adapter<VolTaskAdapter.NoteViewHolder> {
    private Context context;
    private List<TaskModel> task;
    private int taskhr;
    private String pId, uId;

    public VolTaskAdapter(Context context, List<TaskModel> task,String pId,String uId) {
        this.context = context;
        this.task = task;
        this.pId = pId;
        this.uId = uId;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_task_item_vol, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        TaskModel tasks = task.get(position);
        holder.titleTextView.setText("Task #"+tasks.getTasknum()+" : "+tasks.getTitle());
        holder.contentTextView.setText(tasks.getContent());
        taskhr = tasks.getTaskhours();
        String taskhrs = "Task Hours required : "+String.valueOf(tasks.getTaskhours())+" hours";
        holder.taskhourTextView.setText(taskhrs);
        int tasknum = task.get(position).getTasknum();
        boolean isCompleted = task.get(position).isCompleted();

        long currentTime = System.currentTimeMillis();
        LocalDateTime currentDateTime = Instant.ofEpochMilli(currentTime)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String DateTime = currentDateTime.format(formatter);

        holder.timestampTextView.setText(DateTime);

        if (isCompleted) {
            // The task is already completed
            holder.completed.setEnabled(false);
            holder.completed.setText("Marked Done ☑️");
            holder.completed.setBackgroundColor(Color.TRANSPARENT);
        } else {
            // The task is not yet completed
            holder.completed.setEnabled(true);
            holder.completed.setText("Mark as Completed");
        }

        holder.completed.setOnClickListener(v -> {
            DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference("Tasks").child(pId).child(uId).child(String.valueOf(tasknum));

            // Assuming 'completed' is a boolean field in your TaskModel class
            tasksRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        TaskModel task = snapshot.getValue(TaskModel.class);

                        if (task != null && !task.isCompleted()) {
                            // Task is not completed, show confirmation dialog
                            showCompletionConfirmationDialog(tasksRef);
                        } else {
                            // Task is already completed, you can handle this case as needed
                            Toast.makeText(context, "Task is already completed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle errors if needed
                }
            });
        });
    }

    private void showCompletionConfirmationDialog(DatabaseReference tasksRef) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm Completion");
        builder.setMessage("Are you sure you want to mark this task as completed?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Update the 'completed' field to true
                tasksRef.child("completed").setValue(true);

                // Update workhours in Registered Users reference
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Registered Users").child(uId);

                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Check if workhours node exists
                            if (dataSnapshot.hasChild("workhours")) {
                                // Workhours node exists, add the current workhours to the existing value
                                int currentWorkhours = dataSnapshot.child("workhours").getValue(Integer.class);
                                usersRef.child("workhours").setValue(currentWorkhours + taskhr);
                            } else {
                                // Workhours node does not exist, set it to the current workhours
                                usersRef.child("workhours").setValue(taskhr);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle errors if needed
                    }
                });

                // Dismiss the dialog
                dialog.dismiss();
            }
        });


        builder.show();
    }

    @Override
    public int getItemCount() {
        return task.size();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, contentTextView, timestampTextView,taskhourTextView;
        Button completed;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            contentTextView = itemView.findViewById(R.id.content_text_view);
            timestampTextView = itemView.findViewById(R.id.timestamp_text_view);
            taskhourTextView = itemView.findViewById(R.id.task_hour);
            completed = itemView.findViewById(R.id.completedBtn);
        }


    }
}