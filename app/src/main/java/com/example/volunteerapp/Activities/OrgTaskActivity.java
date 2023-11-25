package com.example.volunteerapp.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.volunteerapp.Adapters.TaskAddAdapter;
import com.example.volunteerapp.Models.TaskModel;
import com.example.volunteerapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrgTaskActivity extends AppCompatActivity {

    FloatingActionButton addNoteBtn;
    RecyclerView recyclerView;
    TaskAddAdapter taskAdapter;
    List<TaskModel> task;
    private String uId,pId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_add);

        uId = getIntent().getStringExtra("uId");
        pId = getIntent().getStringExtra("pId");

        addNoteBtn = findViewById(R.id.add_note_btn);
        recyclerView = findViewById(R.id.recycler_view);


        addNoteBtn.setOnClickListener(v -> {
            Intent intent = new Intent(OrgTaskActivity.this, TaskAddActivity.class);
            intent.putExtra("uId",uId);
            intent.putExtra("pId",pId);
            startActivity(intent);
        });

        setupRecyclerView();
    }

    void setupRecyclerView() {
        task = new ArrayList<>();
        taskAdapter = new TaskAddAdapter(this, task);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(taskAdapter);

        DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference("Tasks").child(pId).child(uId);

        tasksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                task.clear();
                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    TaskModel taskvalue = taskSnapshot.getValue(TaskModel.class);
                    task.add(taskvalue);
                }
                taskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors if needed
            }
        });
    }


}
