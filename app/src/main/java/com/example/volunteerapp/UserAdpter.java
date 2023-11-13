package com.example.volunteerapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserAdpter extends RecyclerView.Adapter<UserAdpter.viewholder> {
    Context chatActivity;
    ArrayList<Users> usersArrayList;
    public UserAdpter(ChatActivity chatActivity, ArrayList<Users> usersArrayList) {
        this.chatActivity=chatActivity;
        this.usersArrayList=usersArrayList;
    }

    @NonNull
    @Override
    public UserAdpter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(chatActivity).inflate(R.layout.user_item,parent,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdpter.viewholder holder, int position) {

        Users users = usersArrayList.get(position);
        holder.username.setText(users.getUsername());
        Picasso.get().load(users.getImage_url()).into(holder.userimg);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(chatActivity, chatwindo.class);
                intent.putExtra("username",users.getUsername());
                intent.putExtra("reciverImg",users.getImage_url());
                intent.putExtra("uid",users.getUserId());
                chatActivity.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        CircleImageView userimg;
        TextView username;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            userimg = itemView.findViewById(R.id.userimg);
            username = itemView.findViewById(R.id.username);
        }
    }
}
