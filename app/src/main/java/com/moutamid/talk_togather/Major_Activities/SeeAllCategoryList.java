package com.moutamid.talk_togather.Major_Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.talk_togather.Adapters.Adapter_Detail;
import com.moutamid.talk_togather.Adapters.Adapter_Live;
import com.moutamid.talk_togather.Adapters.Adapter_Upcoming;
import com.moutamid.talk_togather.Models.RoomDetails;
import com.moutamid.talk_togather.R;
import com.moutamid.talk_togather.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class SeeAllCategoryList extends AppCompatActivity {

    ImageView close;

    private RecyclerView detail_recycler;
    private ArrayList<RoomDetails> detailArrayList;
    private Adapter_Detail adapter_detail;
    private String room = "";
    private SharedPreferencesManager prefs;
    private TextView titleTxt;
    private DatabaseReference roomDB;
    private boolean theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_all_category_list);
        prefs = new SharedPreferencesManager(this);
        theme = prefs.retrieveBoolean("theme", false);//get stored theme, zero is default

        setContentView(R.layout.activity_catagory_details);
        if (theme) {
            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_YES);
        } else {

            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_NO);

        }
        getLocale();
        close = findViewById(R.id.close_detail);
        titleTxt = findViewById(R.id.settings_heading);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SeeAllCategoryList.this, DashBoard.class);
                startActivity(intent);
                Animatoo.animateSlideDown(SeeAllCategoryList.this);
                finish();
            }
        });
        roomDB = FirebaseDatabase.getInstance().getReference().child("Rooms");
        room = getIntent().getStringExtra("room");
        titleTxt.setText(room);
        detail_recycler = findViewById(R.id.recyclerView_detail);
        detail_recycler.setAlpha(0f);
        detail_recycler.animate().alpha(1f).setDuration(3000);
        detailArrayList = new ArrayList<>();
        if (room.equals(getString(R.string.upcoming_title))){
            loadUpcomingData();
        }else if (room.equals(getString(R.string.live_title))){
            loadLiveData();
        }

    }

    private void loadLiveData() {
        Query query = roomDB.orderByChild("live").equalTo(true);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    detailArrayList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        RoomDetails model = ds.getValue(RoomDetails.class);
                        detailArrayList.add(model);
                    }
                    adapter_detail = new Adapter_Detail(SeeAllCategoryList.this, detailArrayList);
                    detail_recycler.setAdapter(adapter_detail);
                    adapter_detail.notifyDataSetChanged();
                }else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadUpcomingData() {
        detailArrayList.clear();
        Calendar calendar = Calendar.getInstance();

        int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i=1; i<=days; i++) {
            calendar.add(Calendar.DATE, 1);
            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH)+1;
            int dd = calendar.get(Calendar.DAY_OF_MONTH);
            String date = dd+"/"+mm+"/"+yy;

            Query query = roomDB.orderByChild("timestamp").equalTo(0);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        //upcomingList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()){
                            RoomDetails model = ds.getValue(RoomDetails.class);
                            if (model.getDate().equals(date)) {
                                detailArrayList.add(model);
                            }
                        }

                        adapter_detail = new Adapter_Detail(SeeAllCategoryList.this, detailArrayList);
                        detail_recycler.setAdapter(adapter_detail);
                        adapter_detail.notifyDataSetChanged();
                    }else {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    private void getLocale() {

        String lang = prefs.retrieveString("lang", "");
        setLocale(lang);
    }

    private void setLocale(String lng) {

        Locale locale = new Locale(lng);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        prefs.storeString("lang", lng);
    }

    private void load_detail() {
        detailArrayList = new ArrayList<>();


        roomDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    detailArrayList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        RoomDetails model = ds.getValue(RoomDetails.class);
                        detailArrayList.add(model);
                    }
                    adapter_detail = new Adapter_Detail(SeeAllCategoryList.this, detailArrayList);
                    detail_recycler.setAdapter(adapter_detail);
                    adapter_detail.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SeeAllCategoryList.this, DashBoard.class);
        startActivity(intent);
        Animatoo.animateSlideDown(SeeAllCategoryList.this);
        finish();
    }
}