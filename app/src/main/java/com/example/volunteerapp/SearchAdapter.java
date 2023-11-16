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


    public class SearchAdapter extends RecyclerView.Adapter<com.example.volunteerapp.SearchAdapter.viewholder> {
        Context searchActivity;
        ArrayList<Users> usersArrayList;
        public SearchAdapter(SearchActivity searchActivity, ArrayList<Users> usersArrayList) {
            this.searchActivity=searchActivity;
            this.usersArrayList=usersArrayList;
        }

        @NonNull
        @Override
        public com.example.volunteerapp.SearchAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(searchActivity).inflate(R.layout.user_item,parent,false);
            return new com.example.volunteerapp.SearchAdapter.viewholder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull com.example.volunteerapp.SearchAdapter.viewholder holder, int position) {

            Users users = usersArrayList.get(position);
            holder.username.setText("@"+users.getUsername());
            holder.fullname.setText(users.getFullname());
            Picasso.get().load(users.getImage_url()).into(holder.userimg);

//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(searchActivity, chatwindo.class);
//                    intent.putExtra("fullname",users.getFullname());
//                    intent.putExtra("reciverImg",users.getImage_url());
//                    intent.putExtra("uid",users.getUserId());
//                    searchActivity.startActivity(intent);
//
//                }
//            }
//            );

        }

        @Override
        public int getItemCount() {
            return usersArrayList.size();
        }

        public class viewholder extends RecyclerView.ViewHolder {
            CircleImageView userimg;
            TextView username;
            TextView fullname;
            public viewholder(@NonNull View itemView) {
                super(itemView);
                userimg = itemView.findViewById(R.id.userimg);
                username = itemView.findViewById(R.id.username);
                fullname = itemView.findViewById(R.id.fullname);
            }
        }
    }



