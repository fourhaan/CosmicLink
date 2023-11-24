package com.example.volunteerapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.volunteerapp.Activities.CustomViews.OrgProfileView;
import com.example.volunteerapp.Models.Data;
import com.example.volunteerapp.Models.NotificationSender;
import com.example.volunteerapp.Models.modelPost;
import com.example.volunteerapp.R;
import com.example.volunteerapp.Utils.APIService;
import com.example.volunteerapp.Utils.MyResponse;
import com.example.volunteerapp.Utils.Client;
import com.example.volunteerapp.Models.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {
    Context context;
    List<modelPost> postList;
    String myUid,senderName;

    private DatabaseReference interestedRef; //for interested database node
    private DatabaseReference postsRef;//reference for post
    private DatabaseReference bookmarkRef;
    boolean mProcessInterested = false;
    private APIService apiService;

    public AdapterPosts(Context context, List<modelPost> postList) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts,parent,false);
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
        long workhours = postList.get(position).getWorkhours();
        String workhoursString = String.valueOf(workhours);
        String dateofevent = postList.get(position).getDate();

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
        holder.date.setText("Start of Mission : "+dateofevent);
        holder.hours.setText("Total Mission Work Hours : "+workhoursString+" hours");

        //Set interested for each post
        setInterested(holder,pId);

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
        holder.moreBtn.setVisibility(View.GONE);

        holder.interestedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pInterested = Integer.parseInt(postList.get(holder.getAdapterPosition()).getpInterested());
                mProcessInterested = true;
                String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String postide = postList.get(holder.getAdapterPosition()).getpId();
                isParticipating(postide, new ParticipatingCallback() {
                    @Override
                    public void onResult(boolean isParticipating) {
                        if (!isParticipating) {
                            interestedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (mProcessInterested) {
                                        if (snapshot.child(postide).hasChild(myUid)) {
                                            // Already liked so remove like
                                            postsRef.child(postide).child("pInterested").setValue("" + (pInterested - 1));
                                            interestedRef.child(postide).child(myUid).removeValue();
                                            bookmarkRef.child(userUid).child(postide).removeValue(); // Fix here
                                            mProcessInterested = false;
                                        } else {
                                            // Like it if not liked already
                                            postsRef.child(postide).child("pInterested").setValue("" + (pInterested + 1));
                                            interestedRef.child(postide).child(myUid).setValue("Showed Interest");
                                            bookmarkRef.child(userUid).child(postide).setValue("true"); // Fix here
                                            mProcessInterested = false;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            Toast.makeText(context.getApplicationContext(), "Already Participating", Toast.LENGTH_SHORT).show();
                            holder.interestedBtn.setText("Participating");
                        }
                    }
                });

                //Fetching the token of Organization using his uID.
                FirebaseDatabase.getInstance().getReference().child("Tokens").child(uid)
                        .child("token")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String usertoken=snapshot.getValue(String.class);
                                sendNotifications(usertoken,"Request for Working!",
                                        senderName + " wants to Volunteer ");
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                //Updating the user tokens so that notification can be sent using the token.
                UpdateToken();

            }
        });



        holder.ReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showReportDialog(postList.get(position).getpId(),myUid);
            }
        });
        holder.picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrgProfileView.class);
                intent.putExtra("userId", postList.get(position).getUid());
                context.startActivity(intent);
            }
        });
    }

    private void showReportDialog(String postId, String userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Report Post");

        // Define a list of report behavior types
        String[] reportBehaviors = {"Inappropriate Content", "Spam", "Harassment", "Other"};

        // Initialize boolean array to track selected report behaviors
        boolean[] checkedItems = new boolean[reportBehaviors.length];

        builder.setMultiChoiceItems(reportBehaviors, checkedItems, (dialog, which, isChecked) -> {
            // Handle checkbox selection
            checkedItems[which] = isChecked;
        });

        builder.setPositiveButton("Submit", (dialog, which) -> {
            // Handle submit button click
            submitReport(postId, userId, reportBehaviors, checkedItems);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Handle cancel button click
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void submitReport(String postId, String userId, String[] reportBehaviors, boolean[] checkedItems) {
        // Generate a toast message
        Toast.makeText(context, "Thank you for reporting. We will look into it.", Toast.LENGTH_SHORT).show();

        // Add a database reference for the report
        DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("Reports").child(postId).child(userId);

        // Add the timestamp to the report
        reportsRef.child("timestamp").setValue(ServerValue.TIMESTAMP);

        for (int i = 0; i < reportBehaviors.length; i++) {
            if (checkedItems[i]) {
                // Add the reported behavior to the database
                reportsRef.child(reportBehaviors[i]).setValue(true);
            }
        }
    }


    //Updating the user tokens to send notifications
    private void UpdateToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(context.getApplicationContext(),
                                    "New token failed", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String token = task.getResult();
                        updateToken(token);
                    }
                });
    }

    private void updateToken(String token) {
        Token token1 = new Token(token);
        //Adding the token to the database so that it can be retrieved to send notifications to the user.
        FirebaseDatabase.getInstance().getReference("Tokens")
                .child(FirebaseAuth.getInstance().getUid()).setValue(token1);
    }

    //This Method Sends the notifications combining all class of
    //SendNotificationPack Package work together
    public void sendNotifications(String usertoken, String title, String message) {
        Data data = new Data(title, message);
        NotificationSender sender = new NotificationSender(data, usertoken);
        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call,
                                   Response<MyResponse> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    if (response.body().success != 1) {
                        Toast.makeText(context.getApplicationContext(),
                                "Sorry Organization could not be informed. Please try again later.",
                                Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(context.getApplicationContext(),
                                "Organization has been informed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {
            }
        });
    }

    private void setInterested(MyHolder holder, String postKey) {
        interestedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postKey).hasChild(myUid)){
                    //User has liked the post
                    holder.interestedBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_thumb_up_blue,0,0,0);
                    holder.interestedBtn.setText("Showed Interest");
                }
                else {
                    //User has not liked the post
                    holder.interestedBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_thumb_up_24,0,0,0);
                    holder.interestedBtn.setText("I am Interested");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface ParticipatingCallback {
        void onResult(boolean isParticipating);
    }

    // Modify the isParticipating method
    private void isParticipating(String postId, ParticipatingCallback callback) {
        DatabaseReference participatingRef = FirebaseDatabase.getInstance().getReference("Participating").child(postId).child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        participatingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Check if the snapshot has any children
                boolean isParticipating = snapshot.exists();
                callback.onResult(isParticipating);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                callback.onResult(false); // Assume not participating in case of an error
            }
        });
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
        ImageButton moreBtn;
        Button interestedBtn,ReportBtn,shareBtn,tag1,tag2,tag3,addressBtn,date,hours;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            //Passing the value of client class to the api service
            apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);


            picture = itemView.findViewById(R.id.picture);
            postImg = itemView.findViewById(R.id.postImg);
            displayName = itemView.findViewById(R.id.displayName);
            postTime = itemView.findViewById(R.id.postTime);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            interested = itemView.findViewById(R.id.interested);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            interestedBtn = itemView.findViewById(R.id.interestedBtn);
            ReportBtn = itemView.findViewById(R.id.notinterestedBtn);
            addressBtn = itemView.findViewById(R.id.address_show);
            date = itemView.findViewById(R.id.date);
            hours = itemView.findViewById(R.id.hours);
            tag1 = itemView.findViewById(R.id.tag1);
            tag2 = itemView.findViewById(R.id.tag2);
            tag3 = itemView.findViewById(R.id.tag3);
        }
    }



}