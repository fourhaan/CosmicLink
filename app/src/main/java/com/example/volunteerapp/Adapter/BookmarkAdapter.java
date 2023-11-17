package com.example.volunteerapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.volunteerapp.Models.modelPost;
import com.example.volunteerapp.R;

import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.MyHolder>{
    Context context;
    List<modelPost> postList;
    public BookmarkAdapter(Context context, List<modelPost> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate layout row_posts.xml
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_vol_bookmark,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkAdapter.MyHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        public MyHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
