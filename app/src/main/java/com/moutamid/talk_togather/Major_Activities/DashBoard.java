package com.moutamid.talk_togather.Major_Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.moutamid.talk_togather.Adapters.Adapter_Catagories;
import com.moutamid.talk_togather.Adapters.Adapter_Live;
import com.moutamid.talk_togather.Adapters.Adapter_Upcoming;
import com.moutamid.talk_togather.Models.Conversation;
import com.moutamid.talk_togather.Models.Model_Catagories;
import com.moutamid.talk_togather.Models.Notification;
import com.moutamid.talk_togather.Models.RoomDetails;
import com.moutamid.talk_togather.Models.User;
import com.moutamid.talk_togather.Notifications.Token;
import com.moutamid.talk_togather.R;
import com.moutamid.talk_togather.SharedPreferencesManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashBoard extends AppCompatActivity {

    ImageView setting_icon , message_icon , notification_icon, search_icon ;
    private CircleImageView profile_img_dashboard;
    Button start_room_btn;

    private RecyclerView upcoming_recycler;
    private Adapter_Upcoming adapter_upcoming;

    private DatabaseReference db,notifyDB;
    private FirebaseAuth mAuth;
    private FirebaseUser user;


    private RecyclerView live_recycler;
    private Adapter_Live adapter_live;
    private View view1,view2;

    // data to shown in Categories recyclerview
    String gatheing = "gathering";
    private int[] catagories_heading = {
            R.string.category_art,
            R.string.category_business,
            R.string.category_education,
            R.string.category_food,
            R.string.category_money,
            R.string.category_family,
            R.string.category_psychology,
            R.string.category_christianity,
            R.string.category_health,
            R.string.category_leadership,
            R.string.category_sports,
            R.string.category_advice,
            R.string.category_technology,
            R.string.category_beauty,
            R.string.category_culture,
            R.string.category_lifestyle,
            R.string.category_gaming,
            R.string.category_travel,
            R.string.category_social};
    private String[] catagories_gathering = {"0 " + gatheing, "0 "+ gatheing, "0 "+ gatheing,
            "0 " + gatheing, "0 "+ gatheing, "0 "+ gatheing,"0 "+ gatheing,
            "0 "+ gatheing,"0 "+ gatheing,"0 "+ gatheing,"0 "+ gatheing,"0 "+ gatheing,"0 "+ gatheing,
            "0 "+ gatheing,"0 "+ gatheing,"0 "+ gatheing,"0 "+ gatheing,"0 "+ gatheing,"0 "+ gatheing};
    private int[] images1_catagories = {
            R.drawable.ic_icn_art,
            R.drawable.ic_icn_education,
            R.drawable.ic_icn_education,
            R.drawable.ic_icn_art,
            R.drawable.ic_icn_art,
            R.drawable.ic_icn_world,
            R.drawable.ic_icn_world,
            R.drawable.ic_icn_world,
            R.drawable.ic_icn_world,
            R.drawable.ic_icn_world,
            R.drawable.ic_icn_world,
            R.drawable.ic_icn_world,
            R.drawable.ic_icn_world,
            R.drawable.ic_icn_world,
            R.drawable.ic_icn_world,
            R.drawable.ic_icn_world,
            R.drawable.ic_icn_world,
            R.drawable.ic_icn_world,
            R.drawable.ic_icn_world};

    private RecyclerView catagories_recycler;
    private ArrayList<Model_Catagories> catagoriesArrayList;
    private Adapter_Catagories adapter_catagories;
    private DatabaseReference roomDB,mConversationReference;
    private ArrayList<RoomDetails> upcomingList;
    private ArrayList<RoomDetails> liveList;
    private SharedPreferencesManager prefs;
    private boolean theme;
    private TextView seeAll1,seeAll2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new SharedPreferencesManager(this);
        theme = prefs.retrieveBoolean("theme",false);//get stored theme, zero is default

        setContentView(R.layout.activity_dash_board);

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
        upcoming_recycler = findViewById(R.id.recyclerView_Upcoming);
        upcoming_recycler.setAlpha(0f);
        upcoming_recycler.animate().alpha(1f).setDuration(3000);

        live_recycler = findViewById(R.id.recycler_live_gathering);
        live_recycler.setAlpha(0f);
        live_recycler.animate().alpha(1f).setDuration(3000);

        catagories_recycler = findViewById(R.id.recycler_catagories);
        catagories_recycler.setAlpha(0f);
        catagories_recycler.animate().alpha(1f).setDuration(3000);

        start_room_btn = findViewById(R.id.start_room_btn);
        start_room_btn.setAlpha(0f);
        start_room_btn.animate().alpha(1f).setDuration(3000);

        setting_icon = findViewById(R.id.settings_icon);
        search_icon = findViewById(R.id.search_icon);
        message_icon = findViewById(R.id.message_icon);
        notification_icon = findViewById(R.id.notification_icon);
        profile_img_dashboard = findViewById(R.id.profile_dashbord_img);
        seeAll1 = findViewById(R.id.seeAll1);
        seeAll2 = findViewById(R.id.seeAll2);
        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        setting_icon = findViewById(R.id.settings_icon);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference().child("Users");
        roomDB = FirebaseDatabase.getInstance().getReference().child("Rooms");
        mConversationReference = FirebaseDatabase.getInstance().getReference().child("conversation").child(user.getUid());
        notifyDB = FirebaseDatabase.getInstance().getReference("Notifications")
                .child(user.getUid());
        getUserDetails();
        upcomingList = new ArrayList<>();
        liveList = new ArrayList<>();
        start_room_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start_room_btn.startAnimation(animation);
                Intent intent = new Intent(DashBoard.this , Start_Room_Activity.class);
                startActivity(intent);
                Animatoo.animateSlideUp(DashBoard.this);
                finish();
            }
        });

        setting_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashBoard.this , Settings_Activity.class);
                startActivity(intent);
                Animatoo.animateSlideUp(DashBoard.this);
                finish();
            }
        });
        search_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashBoard.this , SearchActivity.class);
                startActivity(intent);
                Animatoo.animateSlideUp(DashBoard.this);
                finish();
            }
        });
        message_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashBoard.this , Users_Activity.class);
                startActivity(intent);
                Animatoo.animateSlideUp(DashBoard.this);
                finish();
            }
        });
        notification_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashBoard.this , Notification_Activity.class);
                startActivity(intent);
                Animatoo.animateSlideUp(DashBoard.this);
                finish();
            }
        });
        profile_img_dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashBoard.this , Profile_Activity.class);
                startActivity(intent);
                Animatoo.animateSlideUp(DashBoard.this);
                finish();
            }
        });

        checkChatNewArrivals();
        checkNotificationNewArrivals();

        load_upcoming();
        load_live_gathering();
        load_catagories();

        seeAll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashBoard.this , SeeAllCategoryList.class);
                intent.putExtra("room",getString(R.string.upcoming_title));
                startActivity(intent);
                Animatoo.animateSlideUp(DashBoard.this);
                finish();
            }
        });
        seeAll2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashBoard.this , SeeAllCategoryList.class);
                intent.putExtra("room",getString(R.string.live_title));
                startActivity(intent);
                Animatoo.animateSlideUp(DashBoard.this);
                finish();
            }
        });

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(!task.isSuccessful()){
                    System.out.println("Fetching FCM registration token failed ");
                    return;
                }

                String token = task.getResult();
                updatetoken(token);
               // Toast.makeText(DashBoard.this,token,Toast.LENGTH_LONG).show();
            }
        });
    }


    private void updatetoken(String tokenRefreshed) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Tokens");
        Token uToken = new Token(tokenRefreshed);
        db.child(firebaseUser.getUid()).setValue(uToken);

    }

    private void checkNotificationNewArrivals() {
        notifyDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Notification notification = ds.getValue(Notification.class);
                        if (notification.isSeen()) {
                            notification_icon.setImageResource(R.drawable.notification_img);
                        } else {
                            notification_icon.setImageResource(R.drawable.notificztion_pending_img);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkChatNewArrivals() {
        mConversationReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String key = ds.getKey().toString();
                        Conversation conversation = ds.getValue(Conversation.class);
                        if (conversation.getUnreadChatCount() == 0) {
                            message_icon.setImageResource(R.drawable.message_img);
                        } else {
                            message_icon.setImageResource(R.drawable.message_pending_img);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void load_live_gathering() {

        Query query = roomDB.orderByChild("live").equalTo(true);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    liveList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        RoomDetails model = ds.getValue(RoomDetails.class);
                        long time = getHour(model.getTimestamp());
                        if(time <= 2){
                            liveList.add(model);
                        }else {
                            roomDB.child(model.getId()).removeValue();
                        }
                    }
                    view2.setVisibility(View.GONE);
                    live_recycler.setVisibility(View.VISIBLE);
                    adapter_live  = new Adapter_Live(DashBoard.this,liveList);
                    live_recycler.setAdapter(adapter_live);
                    adapter_live.notifyDataSetChanged();
                }else {
                    view2.setVisibility(View.VISIBLE);
                    live_recycler.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    long daysDiff;
    public long getHour(long time) {
        try {
            SimpleDateFormat sdf = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
                //Date dateBefore = sdf.parse("04/21/2022");
                //  Date dateAfter = sdf.parse("04/25/2022");
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTimeInMillis(time);
                Calendar calendar2 = Calendar.getInstance();
                String date = sdf.format(calendar1.getTime());
                String date1 = sdf.format(calendar2.getTime());
                Date dateBefore = sdf.parse(date);
                Date dateAfter = sdf.parse(date1);
                long timeDiff = Math.abs(dateAfter.getTime() - dateBefore.getTime());
                daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
                long hours = TimeUnit.DAYS.toHours(daysDiff);
                //  Toast.makeText(this, "" + hours, Toast.LENGTH_SHORT).show();
                System.out.println("The number of days between dates: " + daysDiff);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return daysDiff;
    }

    private void load_upcoming() {
        upcomingList.clear();
        Calendar calendar = Calendar.getInstance();

        int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i=1; i<=days; i++) {
            calendar.add(Calendar.DATE, 1);
            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH)+1;
            int dd = calendar.get(Calendar.DAY_OF_MONTH);
            String date = dd+"/"+mm+"/"+yy;

            Query query = roomDB.orderByChild("live").equalTo(false);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        //upcomingList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()){
                            RoomDetails model = ds.getValue(RoomDetails.class);
                            if (model.getDate().equals(date)) {
                                upcomingList.add(model);
                            }
                        }
                        view1.setVisibility(View.GONE);
                        upcoming_recycler.setVisibility(View.VISIBLE);
                        adapter_upcoming  = new Adapter_Upcoming(DashBoard.this,upcomingList);
                        upcoming_recycler.setAdapter(adapter_upcoming);
                        adapter_upcoming.notifyDataSetChanged();
                    }else {
                        view1.setVisibility(View.VISIBLE);
                        upcoming_recycler.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        //String date = dd+"/"+mm+"/"+yy;

    }

    private void getUserDetails() {
        db.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User model =snapshot.getValue(User.class);
                    if (model.getImageUrl().equals("")){
                        Picasso.with(getApplicationContext())
                                .load(R.drawable.profile)
                                .into(profile_img_dashboard);
                    }else {
                        Picasso.with(getApplicationContext())
                                .load(model.getImageUrl())
                                .into(profile_img_dashboard);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    private void load_catagories() {
        catagoriesArrayList = new ArrayList<>();

        for (int i = 0; i < catagories_gathering.length; i++) {
            Model_Catagories modelAndroid = new Model_Catagories(
                    catagories_heading[i],
                    catagories_gathering[i],
                    images1_catagories[i]
            );
            catagoriesArrayList.add(modelAndroid);
        }
        adapter_catagories = new Adapter_Catagories(this, catagoriesArrayList);
        catagories_recycler.setAdapter(adapter_catagories);
    }

    @Override
    protected void onResume() {
        super.onResume();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status","Online");
        hashMap.put("timestamp",System.currentTimeMillis());
        db.child(user.getUid()).updateChildren(hashMap);
    }

    @Override
    protected void onPause() {
        super.onPause();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status","Offline");
        db.child(user.getUid()).updateChildren(hashMap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status","Offline");
        db.child(user.getUid()).updateChildren(hashMap);
    }
}