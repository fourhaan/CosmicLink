package com.example.volunteerapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.volunteerapp.Adapters.TaskAddAdapter;
import com.example.volunteerapp.Adapters.VolTaskAdapter;
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

public class VolTaskActivity extends AppCompatActivity {

    FloatingActionButton addNoteBtn;
    RecyclerView recyclerView;
    VolTaskAdapter taskAdapter;
    List<TaskModel> task;
    private String uId,pId,workHours;
    private ProgressBar horProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vol_task);

        uId = getIntent().getStringExtra("uId");
        pId = getIntent().getStringExtra("pId");
        workHours = getIntent().getStringExtra("workhours");
        addNoteBtn = findViewById(R.id.add_note_btn);
        addNoteBtn.setVisibility(View.GONE);
        recyclerView = findViewById(R.id.recycler_view);
        setupRecyclerView();
    }

    void setupRecyclerView() {
        task = new ArrayList<>();
        taskAdapter = new VolTaskAdapter(this, task,pId,uId);
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
