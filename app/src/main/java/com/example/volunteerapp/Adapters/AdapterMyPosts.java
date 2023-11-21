package com.example.volunteerapp.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.volunteerapp.Models.modelPost;
import com.example.volunteerapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterMyPosts extends RecyclerView.Adapter<AdapterMyPosts.MyHolder> {
    Context context;
    List<modelPost> postList;
    String myUid;

    private DatabaseReference interestedRef; //for interested database node
    private DatabaseReference postsRef;//reference for post
    private DatabaseReference bookmarkRef;
    boolean mProcessInterested = false;

    public AdapterMyPosts(Context context, List<modelPost> postList) {
        this.context = context;
        this.postList = postList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        interestedRef = FirebaseDatabase.getInstance().getReference().child("Interested");
        bookmarkRef = FirebaseDatabase.getInstance().getReference().child("Bookmark");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate layout row_posts.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_my_post,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //Get data
        String uid = postList.get(position).getUid();
        String uEmail = postList.get(position).getuEmail();
        String uName = postList.get(position).getuName();
        String uDp = postList.get(position).getuDp();
        String pId = postList.get(position).getpId();
        String pTitle = postList.get(position).getpTitle();
        String pDescription = postList.get(position).getpDescr();
        String pImage = postList.get(position).getpImage();
        String pTimeStamp = postList.get(position).getpTime();
        String pTags = postList.get(position).getpTags();
        String Address = postList.get(position).getAddress();
        String pInterested = postList.get(position).getpInterested();//Contains total number of Interested Volunteers.

        //Convert timestamp to dd/mm/yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();

        //set data
        holder.displayName.setText(uName);
        holder.postTime.setText(pTime);
        holder.title.setText(pTitle);
        holder.description.setText(pDescription);
        holder.interested.setText(pInterested+"+" + " Interests");
        holder.addressBtn.setText("Location : "+Address);

        //adding the three tags to 3 buttons
        String[] tagsArray = pTags.split(" ");
        holder.tag1.setText(tagsArray[0]);
        holder.tag2.setText(tagsArray[1]);
        holder.tag3.setText(tagsArray[2]);

        //Set postImage
        //If there is no image
        if(pImage.equals("no_image")){
            //To hide imageview
            holder.postImg.setVisibility(View.GONE);
            Picasso.get().load(uDp).into(holder.picture);
        }
        else {
            try {
                Picasso.get().load(pImage).into(holder.postImg);
                Picasso.get().load(uDp).into(holder.picture);
            } catch (Exception e) {

            }
        }

        //Handle click buttons

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show the edit dialog
                showEditDialog(postList.get(position));
            }
        });


        holder.deleateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDeleteConfirmationDialog(postList.get(position));
            }
        });
    }

    private void showDeleteConfirmationDialog(modelPost post) {
        // Create a confirmation dialog
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_confirmation);

        // Initialize views from the dialog layout
        Button confirmButton = dialog.findViewById(R.id.confirmButton);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);

        // Handle confirm button click
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call method to delete the post
                deletePost(post);
                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        // Handle cancel button click
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        // Show the confirmation dialog
        dialog.show();
    }

    private void deletePost(modelPost post) {
        postsRef.child(post.getpId()).removeValue();
        // Reference to the "Interested" node
        DatabaseReference interestedNodeRef = interestedRef.child(post.getpId());

        // Remove the post from the "Interested" node
        interestedNodeRef.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, DatabaseReference ref) {
                if (error != null) {
                    Toast.makeText(context, "Failed to remove from Interested", Toast.LENGTH_SHORT).show();
                } else {
                }
            }
        });

        // Remove the post from the "Bookmark" node
        bookmarkRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String volunteerUid = dataSnapshot.getKey();
                    bookmarkRef.child(volunteerUid).child(post.getpId()).removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error != null) {
                                Toast.makeText(context, "Failed to remove from Bookmark", Toast.LENGTH_SHORT).show();
                            } else {
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error if necessary
                Toast.makeText(context, "Failed to remove from Bookmark", Toast.LENGTH_SHORT).show();
            }
        });

        notifyDataSetChanged();

        Toast.makeText(context, "Post deleted successfully", Toast.LENGTH_SHORT).show();
    }

    private void showEditDialog(modelPost post) {
        // Create the dialog
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_edit_post);

        // Initialize views from the dialog layout
        EditText editTitle = dialog.findViewById(R.id.editTitle);
        EditText editDescription = dialog.findViewById(R.id.editDescription);
        Button saveButton = dialog.findViewById(R.id.saveButton);

        // Set current data to the dialog
        editTitle.setText(post.getpTitle());
        editDescription.setText(post.getpDescr());

        // Handle save button click
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the edited title, description, and tags
                String editedTitle = editTitle.getText().toString().trim();
                String editedDescription = editDescription.getText().toString().trim();

                postsRef.child(post.getpId()).child("pTitle").setValue(editedTitle);
                postsRef.child(post.getpId()).child("pDescr").setValue(editedDescription);

                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        // Show the dialog
        dialog.show();
    }


    @Override
    public int getItemCount() {
        return postList.size();
    }

    //View holder class
    class MyHolder extends RecyclerView.ViewHolder{

        //This is the views from row_posts.xml
        ImageView picture,postImg;
        TextView displayName,postTime,title,description,interested;
        ImageButton editBtn;
        Button interestedBtn,deleateBtn,shareBtn,tag1,tag2,tag3,addressBtn;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            picture = itemView.findViewById(R.id.picture);
            postImg = itemView.findViewById(R.id.postImg);
            displayName = itemView.findViewById(R.id.displayName);
            postTime = itemView.findViewById(R.id.postTime);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            interested = itemView.findViewById(R.id.interested);
            editBtn = itemView.findViewById(R.id.editBtn);
            interestedBtn = itemView.findViewById(R.id.interestedBtn);
            deleateBtn = itemView.findViewById(R.id.notinterestedBtn);
            addressBtn = itemView.findViewById(R.id.address_show);
//            commentBtn = itemView.findViewById(R.id.commentBtn);
//            shareBtn = itemView.findViewById(R.id.shareBtn);
            tag1 = itemView.findViewById(R.id.tag1);
            tag2 = itemView.findViewById(R.id.tag2);
            tag3 = itemView.findViewById(R.id.tag3);


        }
    }

}