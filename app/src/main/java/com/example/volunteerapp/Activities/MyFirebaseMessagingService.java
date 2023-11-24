package com.example.volunteerapp.Activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.app.TaskStackBuilder;
import android.app.PendingIntent;
import android.graphics.Color;
import android.os.Build;

import com.example.volunteerapp.Fragments.BfollowFragment;
import com.example.volunteerapp.Fragments.VolHomeFragment;
import com.example.volunteerapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String title="Heading of notification ", ourmessage = " request for Volunteer Ship";
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        // method to create notification channel called
        createNotificationChannel();

        //Create intent for activity you want to start.
        Intent resultIntent = new Intent(this, BfollowFragment.class);

        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);

        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // setting intent to open new activity on clicking notification
        Intent intent = new Intent(getApplicationContext(), VolHomeFragment.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            intent = new Intent(getApplicationContext(), LoginActivity.class);
        }

        title = message.getData().get("Title");
        ourmessage = message.getData().get("Message");

        String chanelID = "CosmicLinkNotifications";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),chanelID);
        builder.setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(title)
                .setContentText(ourmessage)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(resultPendingIntent);

        //Initialising NotificationManager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,builder.build());


        // Create an Intent for the activity you want to start
        //getFirebaseMessage(message.getNotification().getTitle(),message.getNotification().getBody());
    }

    public void createNotificationChannel(){
//        Intent intent = new Intent(getApplicationContext(), BfollowFragment.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

//        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_MUTABLE);
//        builder.setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "CosmicLink_Notification_Channel";
            String description = "This notification channel is to get Volunteer app notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("GetNotification", name, importance);
            channel.setDescription(description);

            // Registering the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }
}
