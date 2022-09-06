package com.moutamid.talk_togather.Major_Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.talk_togather.Models.User;
import com.moutamid.talk_togather.R;
import com.moutamid.talk_togather.SharedPreferencesManager;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile_Activity extends AppCompatActivity {

    ImageView close;
    ImageView edit_profile_btn;
    private CircleImageView profileImg;
    private TextView usernamTxt,fullnameTxt,emailTxt,bioTxt,timeTxt,phoneTxt,followerTxt,followingTxt;
    private DatabaseReference db,mFollowReqDatabase,mFollowingDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private SharedPreferencesManager prefs;
    private boolean theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new SharedPreferencesManager(this);
        theme = prefs.retrieveBoolean("theme",false);//get stored theme, zero is default
        setContentView(R.layout.activity_profile);
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
        edit_profile_btn = findViewById(R.id.edit_profile_btn);
        profileImg = findViewById(R.id.profile);
        usernamTxt = findViewById(R.id.fullname);
        fullnameTxt = findViewById(R.id.name);
        emailTxt = findViewById(R.id.email);
        bioTxt = findViewById(R.id.bio);
        timeTxt = findViewById(R.id.time);
        phoneTxt = findViewById(R.id.phone);
        followerTxt = findViewById(R.id.followers);
        followingTxt = findViewById(R.id.following);
        edit_profile_btn.setAlpha(0f);
        edit_profile_btn.animate().alpha(1f).setDuration(3000);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference().child("Users");
        mFollowReqDatabase = FirebaseDatabase.getInstance().getReference().child("Followers");
        mFollowingDatabase = FirebaseDatabase.getInstance().getReference().child("Following");
        edit_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile_Activity.this , Edit_Profile.class);
                startActivity(intent);
                Animatoo.animateSlideUp(Profile_Activity.this);
                finish();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile_Activity.this , DashBoard.class);
                startActivity(intent);
                Animatoo.animateSlideDown(Profile_Activity.this);
                finish();
            }
        });
        getUserDetails();
        getFollowers();
    }

    private void getFollowers() {
        mFollowReqDatabase.child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            followerTxt.setText(String.valueOf(snapshot.getChildrenCount()));
                        }else{
                            followerTxt.setText(String.valueOf(0));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        mFollowingDatabase.child(user.getUid())
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


    private void getUserDetails() {
        db.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User model =snapshot.getValue(User.class);
                    usernamTxt.setText(model.getFname() + " " + model.getLname());
                    fullnameTxt.setText(model.getFname() + " " + model.getLname());
                    emailTxt.setText(model.getEmail());
                    phoneTxt.setText(model.getPhone());
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


    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(Profile_Activity.this , DashBoard.class);
        startActivity(intent);
        Animatoo.animateSlideDown(Profile_Activity.this);
        finish();
    }
}