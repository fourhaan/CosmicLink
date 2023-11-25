package com.example.volunteerapp.AdminPanel.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.volunteerapp.Activities.CustomViews.BookmarkPostDetails;
import com.example.volunteerapp.AdminPanel.Models.ReportedPost;
import com.example.volunteerapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.widget.Toast;

public class ReportedPostsAdapter extends RecyclerView.Adapter<ReportedPostsAdapter.ReportedPostViewHolder> {

    private Context context;
    private List<ReportedPost> reportedPosts;
    private DatabaseReference interestedRef; //for interested database node
    private DatabaseReference postsRef;//reference for post
    private DatabaseReference bookmarkRef;

    public ReportedPostsAdapter(Context context, List<ReportedPost> reportedPosts) {
        this.context = context;
        this.reportedPosts = reportedPosts;
        interestedRef = FirebaseDatabase.getInstance().getReference().child("Interested");
        bookmarkRef = FirebaseDatabase.getInstance().getReference().child("Bookmark");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
    }

    @NonNull
    @Override
    public ReportedPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reported_post, parent, false);
        return new ReportedPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportedPostViewHolder holder, int position) {
        ReportedPost reportedPost = reportedPosts.get(position);

        // Format timestamp to a readable date and time
        String formattedTimestamp = getFormattedTimestamp(reportedPost.getTimestamp());

        // Set reporter info
        holder.reporterInfo.setText("Reported by: " + reportedPost.getReportedById() + " at " + formattedTimestamp);

        // Set post ID
        holder.postId.setText("Post ID: " + reportedPost.getPostId());

        // Set boolean values (customize as needed)
        holder.harassment.setText("Harassment: " + reportedPost.isHarassment());
        holder.inappropriateContent.setText("Inappropriate Content: " + reportedPost.isInappropriateContent());
        holder.other.setText("Other: " + reportedPost.isOther());
        holder.spam.setText("Spam: " + reportedPost.isSpam());

        holder.del.setOnClickListener(v -> {
            showDeleteConfirmationDialog(reportedPosts.get(position));
        });

        holder.show.setOnClickListener(v -> {
            Intent intent = new Intent(context.getApplicationContext(), BookmarkPostDetails.class);
            intent.putExtra("pId", reportedPost.getPostId());
            context.startActivity(intent);
        });


    }

    private void showDeleteConfirmationDialog(ReportedPost post) {
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

    private void deletePost(ReportedPost post) {
        postsRef.child(post.getPostId()).removeValue();
        // Reference to the "Interested" node
        DatabaseReference interestedNodeRef = interestedRef.child(post.getPostId());

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
                    bookmarkRef.child(volunteerUid).child(post.getPostId()).removeValue(new DatabaseReference.CompletionListener() {
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

        // Remove the post from the "Participating" node
        DatabaseReference partref = FirebaseDatabase.getInstance().getReference("Participating/");
        partref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    partref.child(post.getPostId()).removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error != null) {
                                Toast.makeText(context, "Failed to remove from Participating", Toast.LENGTH_SHORT).show();
                            } else {
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error if necessary
                Toast.makeText(context, "Failed to remove from Participating", Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference reportref = FirebaseDatabase.getInstance().getReference("Reports/");
        reportref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    reportref.child(post.getPostId()).removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error != null) {
                                Toast.makeText(context, "Failed to remove from Reports", Toast.LENGTH_SHORT).show();
                            } else {
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error if necessary
                Toast.makeText(context, "Failed to remove from Reports", Toast.LENGTH_SHORT).show();
            }
        });


        notifyDataSetChanged();

        Toast.makeText(context, "Post deleted successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return reportedPosts.size();
    }

    public class ReportedPostViewHolder extends RecyclerView.ViewHolder {
        TextView reporterInfo;
        TextView postId;
        TextView harassment;
        TextView inappropriateContent;
        TextView other;
        TextView spam;
        Button del;
        Button show;

        public ReportedPostViewHolder(@NonNull View itemView) {
            super(itemView);
            reporterInfo = itemView.findViewById(R.id.textViewReportedById);
            postId = itemView.findViewById(R.id.textViewPostId);
            harassment = itemView.findViewById(R.id.textViewHarassment);
            inappropriateContent = itemView.findViewById(R.id.textViewInappropriateContent);
            other = itemView.findViewById(R.id.textViewOther);
            spam = itemView.findViewById(R.id.textViewSpam);
            del = itemView.findViewById(R.id.del_post);
            show = itemView.findViewById(R.id.show_post);
        }
    }

    // Helper method to format timestamp
    private String getFormattedTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(timestamp);
    }


}

