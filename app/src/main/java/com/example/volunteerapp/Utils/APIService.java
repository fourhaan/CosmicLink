package com.example.volunteerapp.Utils;

import com.example.volunteerapp.Models.NotificationSender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",

                    // server key for using firebase messaging services
                    "Authorization: key=598316097840"}//Used Server ID instead of Server Key.
    )

    @POST("fcm/send")

    Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}
