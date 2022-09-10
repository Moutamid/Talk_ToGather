package com.moutamid.talk_togather.Major_Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.database.ValueEventListener;
import com.moutamid.talk_togather.Models.User;
import com.moutamid.talk_togather.R;
import com.moutamid.talk_togather.SharedPreferencesManager;
import com.moutamid.talk_togather.databinding.ActivityProfileBinding;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile_Activity extends AppCompatActivity {

    ImageView close;
    ImageView edit_profile_btn;
    private ActivityProfileBinding b;
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
        b = ActivityProfileBinding.inflate(getLayoutInflater());
        prefs = new SharedPreferencesManager(this);
        theme = prefs.retrieveBoolean("theme",false);//get stored theme, zero is default
        setContentView(b.getRoot());
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
        b.fbImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLinkDialog("fb");
            }
        });
        b.twitterImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLinkDialog("twitter");
            }
        });
        b.instaImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLinkDialog("insta");
            }
        });
        b.tiktokImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLinkDialog("tiktok");
            }
        });
        b.linkdinImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLinkDialog("linkdin");
            }
        });


        getUserDetails();
        getFollowers();
    }

    private void showLinkDialog(String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Profile_Activity.this);
        LayoutInflater inflater = getLayoutInflater();
        View add_view = inflater.inflate(R.layout.link_alert_dialog_screen,null);
        EditText linkTxt = add_view.findViewById(R.id.link);
        AppCompatButton addBtn = add_view.findViewById(R.id.save);
        AppCompatButton cancelBtn = add_view.findViewById(R.id.cancel);
        builder.setView(add_view);
        AlertDialog alertDialog = builder.create();
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String link = linkTxt.getText().toString();
                if (!link.isEmpty()){
                    HashMap<String,Object> hashMap = new HashMap<>();
                    if(type.equals("fb")){
                        hashMap.put("facebookId",link);
                    }else if(type.equals("insta")){
                        hashMap.put("instaId",link);
                    }else if(type.equals("twitter")){
                        hashMap.put("twitterId",link);
                    }else if(type.equals("tiktok")){
                        hashMap.put("tiktokId",link);
                    }else if(type.equals("linkdin")){
                        hashMap.put("linkdinId",link);
                    }
                    db.child(user.getUid()).updateChildren(hashMap);
                    alertDialog.dismiss();
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

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
                    if (snapshot.child("facebookId").exists()){
                        b.imageView1.setImageResource(R.drawable.ic_add);
                    }else {
                        b.imageView1.setImageResource(R.drawable.ic_plus);
                    }

                    if (snapshot.child("instaId").exists()){
                        b.imageView2.setImageResource(R.drawable.ic_add);
                    }else {
                        b.imageView2.setImageResource(R.drawable.ic_plus);
                    }
                    if (snapshot.child("twitterId").exists()){
                        b.imageView3.setImageResource(R.drawable.ic_add);
                    }else {
                        b.imageView3.setImageResource(R.drawable.ic_plus);
                    }
                    if (snapshot.child("tiktokId").exists()){
                        b.imageView4.setImageResource(R.drawable.ic_add);
                    }else {
                        b.imageView4.setImageResource(R.drawable.ic_plus);
                    }
                    if (snapshot.child("linkdinId").exists()){
                        b.imageView5.setImageResource(R.drawable.ic_add);
                    }else {
                        b.imageView5.setImageResource(R.drawable.ic_plus);
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