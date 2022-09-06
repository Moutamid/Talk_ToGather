package com.moutamid.talk_togather.Major_Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.talk_togather.Models.Chat;
import com.moutamid.talk_togather.Models.Conversation;
import com.moutamid.talk_togather.Models.Notification;
import com.moutamid.talk_togather.Models.User;
import com.moutamid.talk_togather.Notifications.Client;
import com.moutamid.talk_togather.Notifications.Data;
import com.moutamid.talk_togather.Notifications.MyResponse;
import com.moutamid.talk_togather.Notifications.Sender;
import com.moutamid.talk_togather.Notifications.Token;
import com.moutamid.talk_togather.R;
import com.moutamid.talk_togather.SharedPreferencesManager;
import com.moutamid.talk_togather.listener.APIService;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class User_Profile_Activity extends AppCompatActivity {

    ImageView close;
    private CircleImageView profileImg;
    private TextView usernamTxt,fullnameTxt,bioTxt,timeTxt,followerTxt,followingTxt,followingBtn,followBtn;
    private DatabaseReference db;
    private Button sayBtn;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String userId;
    private int unreadCount = 0;
    private DatabaseReference mFollowReqDatabase,mFollowingDatabase,mConversationReference,mChatReference,notifyDB;
    private APIService apiService;
    private String username = "";
    private String mCurrent_state;
    private SharedPreferencesManager prefs;
    //private boolean notify;
    private boolean theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new SharedPreferencesManager(this);
        theme = prefs.retrieveBoolean("theme",false);//get stored theme, zero is default
        setContentView(R.layout.activity_user_profile);
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.bounce);

        if (theme){
            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_YES);
        }else {

            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_NO);

        }
        getLocale();
        close = findViewById(R.id.close_profile);
        profileImg = findViewById(R.id.profile);
        usernamTxt = findViewById(R.id.fullname);
        fullnameTxt = findViewById(R.id.name);
        bioTxt = findViewById(R.id.bio);
        timeTxt = findViewById(R.id.time);
        followerTxt = findViewById(R.id.followers);
        followingTxt = findViewById(R.id.following);
        followBtn = findViewById(R.id.follow);
        followingBtn = findViewById(R.id.followingBtn);
        sayBtn = findViewById(R.id.say);
        prefs = new SharedPreferencesManager(User_Profile_Activity.this);
      //  notify = prefs.retrieveBoolean("isNotify",false);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userId = getIntent().getStringExtra("userUid");
        mFollowReqDatabase = FirebaseDatabase.getInstance().getReference().child("Followers");
        mFollowingDatabase = FirebaseDatabase.getInstance().getReference().child("Following");
        mChatReference = FirebaseDatabase.getInstance().getReference().child("chats");
        mConversationReference = FirebaseDatabase.getInstance().getReference().child("conversation");
        notifyDB = FirebaseDatabase.getInstance().getReference().child("Notifications");
        db = FirebaseDatabase.getInstance().getReference().child("Users");
        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);
        getUserDetails();
        getFollowers();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_Profile_Activity.this , DashBoard.class);
                startActivity(intent);
                Animatoo.animateSlideDown(User_Profile_Activity.this);
                finish();
            }
        });

        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                followBtn.startAnimation(animation);
                followUsers();
                followBtn.setVisibility(View.GONE);
                followingBtn.setVisibility(View.VISIBLE);
            }
        });

        sayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sayBtn.startAnimation(animation);
                long timestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                mConversationReference.child(userId).child(user.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    Conversation model = snapshot.getValue(Conversation.class);
                                    //if (model.getChatWithId().equals(user.getUid()) && model.getUserUid().equals(idFromContact)){
                                    unreadCount = model.getUnreadChatCount() + 1;
                                    sentChat("text","hi",timestamp,unreadCount);
                                    //}
                                }else {
                                    unreadCount = unreadCount + 1;
                                    sentChat("text","hi",timestamp,unreadCount);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

            }
        });

        followingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        checkFollowing();
    }

    private void checkFollowing() {
        mFollowReqDatabase.child(userId)
                .child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            followingBtn.setVisibility(View.VISIBLE);
                            followBtn.setVisibility(View.GONE);
                        }else {
                            followingBtn.setVisibility(View.GONE);
                            followBtn.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void sentChat(String type, String contoh, long timestamp, int unreadCount) {
        //unreadCount = unreadCount + 1;
        Chat chatReciever = new Chat(type, contoh, user.getUid(), userId, timestamp, unreadCount);

        Conversation conversationSender = new Conversation(type, user.getUid(), userId, contoh,
                timestamp, 0);
        Conversation conversationReceiver = new Conversation(type, userId, user.getUid(), contoh,
                timestamp, unreadCount);

        DatabaseReference senderReference = mConversationReference.child(user.getUid()).child(userId);
        senderReference.setValue(conversationSender);
        DatabaseReference receiverReference = mConversationReference.child(userId).child(user.getUid());
        receiverReference.setValue(conversationReceiver);


        DatabaseReference senderReference1 = mChatReference.child(user.getUid()).child(userId);
        senderReference1.child(String.valueOf(timestamp)).setValue(chatReciever);
        DatabaseReference receiverReference1 = mChatReference.child(userId).child(user.getUid());
        receiverReference1.child(String.valueOf(timestamp)).setValue(chatReciever);

        Intent intent = new Intent(User_Profile_Activity.this, Chat_Activity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      //  intent.putExtra(Chat_Activity.EXTRAS_USER, user);
        intent.putExtra("userUid", userId);
        startActivity(intent);

      //  if (!notify) {
            sendNotification(userId, username, contoh);
       // }
        int min = 1;
        int max = 1000000000;
        Random r = new Random();
        int i1 = r.nextInt(max - min + 1) + min;


        Notification notification = new Notification();
        notification.setId(i1);
        notification.setFromUserId(user.getUid());
        notification.setNotificationType("message_sent");
        notification.setSeen(false);
        notification.setNotificationTime(System.currentTimeMillis());

        notifyDB.child(userId).child(String.valueOf(i1)).setValue(notification);

    }

    private void getLocale(){

        String lang = prefs.retrieveString("lang","");
        setLocale(lang);
    }

    private void setLocale(String lng) {

        Locale locale = new Locale(lng);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
        prefs.storeString("lang",lng);
    }

    private void sendNotification(String receiver, String username, String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(user.getUid(), R.mipmap.ic_launcher, username+": "+message,
                            "New Message", userId);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code() == 200){
                                        if (response.body().success != 1){
                                            System.out.println("Failed to send notification!");
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void followUsers() {
        HashMap<String,String> followRequest = new HashMap<>();
        followRequest.put("id",user.getUid());
        mFollowReqDatabase.child(userId).child(user.getUid()).setValue(followRequest);

        HashMap<String,String> followingRequest = new HashMap<>();
        followingRequest.put("id",userId);
        mFollowingDatabase.child(user.getUid()).child(userId).setValue(followingRequest);

        getFollowers();
       // if (!notify) {
            sendFollowNotification(userId, username);
        //}


        int min = 1;
        int max = 1000000000;
        Random r = new Random();
        int i1 = r.nextInt(max - min + 1) + min;


        Notification notification = new Notification();
        notification.setId(i1);
        notification.setFromUserId(user.getUid());
        notification.setNotificationType("follow_request");
        notification.setSeen(false);
        notification.setNotificationTime(System.currentTimeMillis());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", notification.getId());
        hashMap.put("fromUserId", notification.getFromUserId());
        hashMap.put("notificationType", notification.getNotificationType());
        hashMap.put("isSeen", notification.isSeen());
        hashMap.put("notificationTime", notification.getNotificationTime());


        notifyDB.child(userId).child(String.valueOf(i1)).setValue(notification);
        Toast.makeText(User_Profile_Activity.this, "Request Sent Successfully.", Toast.LENGTH_SHORT).show();

    }
    private void getFollowers() {
        mFollowReqDatabase.child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            followerTxt.setText(String.valueOf(snapshot.getChildrenCount()));
                        }else {
                            followerTxt.setText(String.valueOf(0));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        mFollowingDatabase.child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            followingTxt.setText(String.valueOf(snapshot.getChildrenCount()));
                        }else {
                            followingTxt.setText(String.valueOf(0));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void sendFollowNotification(String receiver, String username) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(user.getUid(), R.mipmap.ic_launcher, username+" start following you.. ",
                            "Follow Request", userId);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code() == 200){
                                        if (response.body().success != 1){
                                            System.out.println("Failed to send notification!");
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getUserDetails() {
        db.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User model =snapshot.getValue(User.class);
                    username = model.getFname();
                    usernamTxt.setText(model.getFname() + " " + model.getLname());
                    fullnameTxt.setText(model.getFname() + " " + model.getLname());
                    bioTxt.setText(model.getBio());

                    SimpleDateFormat sdf  = new SimpleDateFormat("hh:mm aa");
                    Date date = new Date(model.getTimestamp());
                    String time = sdf.format(date);
                    timeTxt.setText(time);
                    if (model.getImageUrl().equals("")){
                        Picasso.with(getApplicationContext())
                                .load(R.drawable.profile)
                                .into(profileImg);
                    }else {
                        Picasso.with(getApplicationContext())
                                .load(model.getImageUrl())
                                .into(profileImg);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(User_Profile_Activity.this , DashBoard.class);
        startActivity(intent);
        Animatoo.animateSlideDown(User_Profile_Activity.this);
        finish();
    }
}