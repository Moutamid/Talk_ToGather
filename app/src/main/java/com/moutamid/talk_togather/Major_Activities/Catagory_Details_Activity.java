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
import com.moutamid.talk_togather.Models.RoomDetails;
import com.moutamid.talk_togather.R;
import com.moutamid.talk_togather.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.Locale;

public class Catagory_Details_Activity extends AppCompatActivity {

    ImageView close;

    private RecyclerView detail_recycler;
    private ArrayList<RoomDetails> detailArrayList;
    private Adapter_Detail adapter_detail;
    private String category = "";
    private SharedPreferencesManager prefs;
    private TextView titleTxt;
    private DatabaseReference roomDB;
    private boolean theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new SharedPreferencesManager(this);
        theme = prefs.retrieveBoolean("theme",false);//get stored theme, zero is default

        setContentView(R.layout.activity_catagory_details);
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
        close = findViewById(R.id.close_detail);
        titleTxt = findViewById(R.id.settings_heading);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Catagory_Details_Activity.this , DashBoard.class);
                startActivity(intent);
                Animatoo.animateSlideDown(Catagory_Details_Activity.this);
                finish();
            }
        });
        roomDB = FirebaseDatabase.getInstance().getReference().child("Rooms");
        category = getIntent().getStringExtra("category");
        titleTxt.setText(category);
        detail_recycler = findViewById(R.id.recyclerView_detail);
        detail_recycler.setAlpha(0f);
        detail_recycler.animate().alpha(1f).setDuration(3000);

        load_detail();
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

    private void load_detail() {
        detailArrayList = new ArrayList<>();

        Query query = roomDB.orderByChild("category").equalTo(category);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    detailArrayList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        RoomDetails model = ds.getValue(RoomDetails.class);
                        detailArrayList.add(model);
                    }
                    adapter_detail = new Adapter_Detail(Catagory_Details_Activity.this, detailArrayList);
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
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(Catagory_Details_Activity.this , DashBoard.class);
        startActivity(intent);
        Animatoo.animateSlideDown(Catagory_Details_Activity.this);
        finish();
    }
}