package com.moutamid.talk_togather.listener;


import com.moutamid.talk_togather.Notifications.MyResponse;
import com.moutamid.talk_togather.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(

            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAz-p60C0:APA91bF-DAR2zuF-taYkRfIRqhpvWY6VDS8xOCNLUoAIl98mwxsXk65OPYVp4aUxVK8WfCVU7cgRetT-4ZrIUcXh2IPEP0Oxz3KhQcl596deU3tqnj8Qms6SMY_HlRJmciRNVMsXVzdH"
            }

    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
