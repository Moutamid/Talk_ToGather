package com.moutamid.talk_togather.Major_Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.talk_togather.Adapters.NotificationAdapter;
import com.moutamid.talk_togather.Models.Notification;
import com.moutamid.talk_togather.R;
import com.moutamid.talk_togather.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Notification_Activity extends AppCompatActivity {

    ImageView close;
    private RecyclerView notification_recycler;
    private List<Notification> mOldNotifications;

    private NotificationAdapter adapter_notification;
    private SharedPreferencesManager prefs;
    private boolean theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new SharedPreferencesManager(this); //get SharedPreferencesManager  instance
        theme = prefs.retrieveBoolean("theme",false);//get stored theme, zero is default
        setContentView(R.layout.activity_notification);
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
        close = findViewById(R.id.close_notification);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Notification_Activity.this , DashBoard.class);
                startActivity(intent);
                Animatoo.animateSlideDown(Notification_Activity.this);
                finish();
            }
        });
        mOldNotifications = new ArrayList<>();
        notification_recycler = findViewById(R.id.recyclerView_notification);
        loadnotification();


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


    private void loadnotification() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mOldNotifications.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Notification notification = snapshot.getValue(Notification.class);
                  //  if(notification.getIsSeen()){
                        mOldNotifications.add(notification);
                    //}
                }


                adapter_notification = new NotificationAdapter(Notification_Activity.this, mOldNotifications);
                notification_recycler.setAdapter(adapter_notification);
                adapter_notification.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

   /* private void loadnotification() {
        notificationArrayList = new ArrayList<>();

        for (int i = 0; i < notification_headings.length; i++) {
            Model_Notification modelAndroid = new Model_Notification(
                    notification_headings[i],
                    notification_time[i],
                    notification_status[i],
                    images1_notificaton[i]
            );
            notificationArrayList.add(modelAndroid);
        }
        adapter_notification = new Adapter_Notification(this, notificationArrayList);
        notification_recycler.setAdapter(adapter_notification);
    }*/

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(Notification_Activity.this , DashBoard.class);
        startActivity(intent);
        Animatoo.animateSlideDown(Notification_Activity.this);
        finish();
    }
}