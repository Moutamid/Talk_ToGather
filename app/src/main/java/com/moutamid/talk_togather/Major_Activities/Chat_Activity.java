package com.moutamid.talk_togather.Major_Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.talk_togather.Adapters.Adapter_Chat;
import com.moutamid.talk_togather.Models.BlockedUser;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Chat_Activity extends AppCompatActivity {

    ImageView close;

    private RecyclerView chat_rcycler;
    private ArrayList<Chat> chatArrayList;
    private Adapter_Chat adapter_chat;
    public static final String EXTRAS_USER = "user";
    public String idFromContact = "";
    private DatabaseReference mChatReference,mConversationReference,mUserReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    public String id,userUid;
    private TextView nameTxt,statusTxt;
    private CircleImageView profileImg;
    private EditText mMessageText;
    private ImageView sendBtn;
    private int unreadCount = 0;
    APIService apiService;
    private SharedPreferencesManager prefs;
    //private boolean notify;
    private String username = "";
    private boolean theme;
    private LinearLayoutManager linearLayoutManager;
    boolean isBlocked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new SharedPreferencesManager(this); //get SharedPreferencesManager  instance
        theme = prefs.retrieveBoolean("theme",false);//get stored theme, zero is default
        setContentView(R.layout.activity_chat);
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
        close = findViewById(R.id.close_chat);
        nameTxt = findViewById(R.id.name);
        statusTxt = findViewById(R.id.status);
        profileImg = findViewById(R.id.profile);
        mMessageText = findViewById(R.id.message);
        sendBtn = findViewById(R.id.send);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        idFromContact = getIntent().getStringExtra("userUid");
        id = idFromContact;
        userUid = user.getUid();
        mChatReference = FirebaseDatabase.getInstance().getReference().child("chats");
        mConversationReference = FirebaseDatabase.getInstance().getReference().child("conversation");
        mUserReference = FirebaseDatabase.getInstance().getReference().child("Users");
        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Chat_Activity.this , DashBoard.class);
                startActivity(intent);
                Animatoo.animateSlideDown(Chat_Activity.this);
                finish();
            }
        });
        chatArrayList = new ArrayList<>();
        chat_rcycler = findViewById(R.id.recyclerView_chat);
        linearLayoutManager = new LinearLayoutManager(Chat_Activity.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        linearLayoutManager.setStackFromEnd(true);
        chat_rcycler.setLayoutManager(linearLayoutManager);
        chat_rcycler.setNestedScrollingEnabled(false);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contoh = mMessageText.getText().toString();
                if (!TextUtils.isEmpty(contoh)) {
                    long timestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Blocked Users")
                            .child(idFromContact);

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                    BlockedUser blockedUser = snapshot.getValue(BlockedUser.class);

                                    if (blockedUser.getId().equals(user.getUid())) {
                                        isBlocked = true;
                                    }

                                    if (!isBlocked) {
                                        sentUserChat(contoh,timestamp);
                                    } else {
                                        sentChatWithoutNotify("text", contoh, timestamp, 0);
                                    }

                                }
                            }else {
                                sentUserChat(contoh,timestamp);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Chat_Activity.this, User_Profile_Activity.class);
                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("userUid", idFromContact);
                startActivity(intent);
            }
        });
        getUserDetails();
        getChatData();
    }

    private void sentUserChat(String contoh, long timestamp) {
        mConversationReference.child(idFromContact).child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Conversation model = snapshot.getValue(Conversation.class);
                            //if (model.getChatWithId().equals(user.getUid()) && model.getUserUid().equals(idFromContact)){
                            unreadCount = model.getUnreadChatCount() + 1;
                            sentChat("text", contoh, timestamp, unreadCount);
                            //}
                        } else {
                            unreadCount = unreadCount + 1;
                            sentChat("text", contoh, timestamp, unreadCount);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void sentChatWithoutNotify(String type, String contoh, long timestamp, int unreadCount) {
        //unreadCount = unreadCount + 1;
        Chat chatReciever = new Chat(type, contoh, user.getUid(), idFromContact, timestamp, unreadCount);

        Conversation conversationSender = new Conversation(type, user.getUid(), idFromContact, contoh,
                timestamp, 0);

        DatabaseReference senderReference = mConversationReference.child(user.getUid()).child(idFromContact);
        senderReference.setValue(conversationSender);


        DatabaseReference senderReference1 = mChatReference.child(user.getUid()).child(idFromContact);
        senderReference1.child(String.valueOf(timestamp)).setValue(chatReciever);

        mMessageText.setText("");

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

    private void getUserDetails() {
        mUserReference.child(idFromContact).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User model = snapshot.getValue(User.class);
                    nameTxt.setText(model.getFname()+ " " + model.getLname());
                    if (model.getImageUrl().equals("")){
                        Picasso.with(getApplicationContext())
                                .load(R.drawable.profile)
                                .into(profileImg);
                    }else {
                        Picasso.with(getApplicationContext())
                                .load(model.getImageUrl())
                                .into(profileImg);
                    }
                    if (model.getStatus().equals("Online")){
                        statusTxt.setText("Online");
                    }else {
                        statusTxt.setText("Offline");
                    }
                    username = model.getFname();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sentChat(String type, String contoh, long timestamp, int unreadCount) {

        //unreadCount = unreadCount + 1;
        Chat chatReciever = new Chat(type, contoh, user.getUid(), idFromContact, timestamp, unreadCount);

        Conversation conversationSender = new Conversation(type, user.getUid(), idFromContact, contoh,
                timestamp, 0);
        Conversation conversationReceiver = new Conversation(type, idFromContact, user.getUid(), contoh,
                timestamp, unreadCount);

        DatabaseReference senderReference = mConversationReference.child(user.getUid()).child(idFromContact);
        senderReference.setValue(conversationSender);
        DatabaseReference receiverReference = mConversationReference.child(idFromContact).child(user.getUid());
        receiverReference.setValue(conversationReceiver);


        DatabaseReference senderReference1 = mChatReference.child(user.getUid()).child(idFromContact);
        senderReference1.child(String.valueOf(timestamp)).setValue(chatReciever);
        DatabaseReference receiverReference1 = mChatReference.child(idFromContact).child(user.getUid());
        receiverReference1.child(String.valueOf(timestamp)).setValue(chatReciever);
        mMessageText.setText("");

        //if (!notify) {
            sendNotification(idFromContact, username, contoh);
        //}
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

        DatabaseReference notifyDB = FirebaseDatabase.getInstance().getReference().child("Notifications");
        notifyDB.child(idFromContact).child(String.valueOf(i1)).setValue(notification);
    }

    private void sendNotification(String receiver, String username, String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(user.getUid(), R.mipmap.ic_launcher, username+": "+message, "New Message", idFromContact);

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

    private void getChatData() {
        mChatReference.child(userUid).child(idFromContact)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            chatArrayList.clear();
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Chat chat = ds.getValue(Chat.class);
                                chatArrayList.add(chat);
                            }
                            adapter_chat = new Adapter_Chat(Chat_Activity.this,chatArrayList);
                            chat_rcycler.setAdapter(adapter_chat);
                            chat_rcycler.smoothScrollToPosition(chat_rcycler.getAdapter().getItemCount());
                        //    scrollRecyclerViewToEnd();
                            adapter_chat.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void scrollRecyclerViewToEnd() {
        adapter_chat.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                int msgCount = adapter_chat.getItemCount();

                int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();

               // if (positionStart >= (msgCount - 1)) {
                    chat_rcycler.scrollToPosition(msgCount);
                //}

            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status","Online");
        mUserReference.child(user.getUid()).updateChildren(hashMap);
    }

    @Override
    protected void onPause() {
        super.onPause();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status","Offline");
        mUserReference.child(user.getUid()).updateChildren(hashMap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status","Offline");
        mUserReference.child(user.getUid()).updateChildren(hashMap);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(Chat_Activity.this , DashBoard.class);
        startActivity(intent);
        Animatoo.animateSlideDown(Chat_Activity.this);
        finish();
    }
}