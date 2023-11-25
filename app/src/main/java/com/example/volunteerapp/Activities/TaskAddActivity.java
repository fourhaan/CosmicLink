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

public class TaskAddActivity extends AppCompatActivity {

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
        uId = getIntent().getStringExtra("uId");
        pId = getIntent().getStringExtra("pId");

        titleEditText.setText(title);
        descriptionEditText.setText(description);
        taskhourEditText.setText(hours);
        saveTaskBtn.setOnClickListener((v) -> saveTask());

        // Check existing tasks and set tasknum accordingly
        checkExistingTasks();

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
        Intent intent = new Intent(TaskAddActivity.this, OrgTaskActivity.class);
        intent.putExtra("uId",uId);
        intent.putExtra("pId",pId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    void checkExistingTasks() {
        DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference("Tasks").child(pId).child(uId);

        tasksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Check if there are existing tasks
                if (snapshot.exists()) {
                    // Find the maximum tasknum
                    for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                        int existingTaskNum = Integer.parseInt(taskSnapshot.getKey());
                        if (existingTaskNum >= tasknum) {
                            tasknum = existingTaskNum + 1;
                        }
                    }
                } else {
                    // No existing tasks, initialize tasknum to 1
                    tasknum = 1;
                }

                // Retrieve workhours from posts/pId
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors if needed
            }
        });
    }


    void saveTaskToFirebase(TaskModel task) {
        DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference("Tasks").child(pId).child(uId).child(String.valueOf(tasknum));
        tasksRef.setValue(task);
        finish();
    }

    void deleteTaskFromFirebase() {
        DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference("Tasks");

        if (taskId != null && !taskId.isEmpty()) {
            tasksRef.child(taskId).removeValue();
            finish();
        }
    }
}
