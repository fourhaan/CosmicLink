package com.example.volunteerapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.volunteerapp.Models.TaskModel;
import com.example.volunteerapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CommonTaskActivity extends AppCompatActivity {

    EditText titleEditText, descriptionEditText,taskhourEditText;
    ImageButton saveTaskBtn;
    TextView pageTitleTextView;
    String title, description, taskId, hours;
    private String uId, pId;
    private int tasknum,tworkhours;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.row_task);
        titleEditText = findViewById(R.id.task_title_text);
        descriptionEditText = findViewById(R.id.task_content_text);
        saveTaskBtn = findViewById(R.id.save_task_btn);
        pageTitleTextView = findViewById(R.id.page_title);
        taskhourEditText = findViewById(R.id.hours_task_text);


        // Receive data
        pId = getIntent().getStringExtra("pId");

        titleEditText.setText(title);
        descriptionEditText.setText(description);
        taskhourEditText.setText(hours);
        saveTaskBtn.setOnClickListener((v) -> saveTask());

    }

    void saveTask() {
        String taskTitle = titleEditText.getText().toString();
        String taskDescription = descriptionEditText.getText().toString();
        String taskHoursString  = taskhourEditText.getText().toString();
        int taskHours = Integer.parseInt(taskHoursString);
        if (taskTitle == null || taskTitle.isEmpty()) {
            titleEditText.setError("Title is required");
            return;
        }
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("Posts").child(pId);
        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve the value of workhours
                    tworkhours = dataSnapshot.child("workhours").getValue(int.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors if needed
            }
        });


        if(tworkhours<taskHours){
            taskhourEditText.setError("Invalid time");
            return;
        }
        boolean completed=false;

        long timestamp = System.currentTimeMillis();
        TaskModel task = new TaskModel(taskTitle, taskDescription, timestamp,taskHours,tasknum,completed);
        task.setTitle(taskTitle);
        task.setContent(taskDescription);
        saveTaskToFirebase(task);
    }

    void checkExistingTasks() {
        DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference("Tasks").child(pId);

        tasksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Check if there are existing tasks
                if (snapshot.exists()) {
                    // Iterate over all user IDs
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        // Iterate over tasks for each user and find the maximum tasknum
                        for (DataSnapshot taskSnapshot : userSnapshot.getChildren()) {
                            int existingTaskNum = Integer.parseInt(taskSnapshot.getKey());
                            if (existingTaskNum >= tasknum) {
                                tasknum = existingTaskNum + 1;
                            }
                        }
                    }
                } else {
                    // No existing tasks, initialize tasknum to 1
                    tasknum = 1;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors if needed
            }
        });
    }



    void saveTaskToFirebase(TaskModel task) {
        DatabaseReference commonTasksRef = FirebaseDatabase.getInstance().getReference("Tasks").child(pId);

        DatabaseReference userTaskRef = commonTasksRef.child(uId).child(String.valueOf(tasknum));
        userTaskRef.setValue(task);

        DatabaseReference projectUsersRef = FirebaseDatabase.getInstance().getReference("Participating").child(pId);

        projectUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check existing tasks for all users participating under the project
                checkExistingTasks();

                // Iterate over all user IDs
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();

                    // Skip the current user, as the task has already been saved for them
                    if (userId.equals(uId)) {
                        continue;
                    }

                    // Reference to the task for the other user under the common tasks
                    DatabaseReference otherUserTaskRef = commonTasksRef.child(userId).child(String.valueOf(tasknum));

                    // Save the task for the other user
                    otherUserTaskRef.setValue(task);
                }

                // Finish the activity after saving tasks for all users
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors if needed
            }
        });
    }



    void deleteTaskFromFirebase() {
        DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference("Tasks");

        if (taskId != null && !taskId.isEmpty()) {
            tasksRef.child(taskId).removeValue();
            finish();
        }
    }
}

