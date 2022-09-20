package com.moutamid.talk_togather.Major_Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.talk_togather.Adapters.FollowUserListAdapter;
import com.moutamid.talk_togather.Adapters.UnFollowUserListAdapter;
import com.moutamid.talk_togather.Models.User;
import com.moutamid.talk_togather.R;
import com.moutamid.talk_togather.SharedPreferencesManager;
import com.moutamid.talk_togather.databinding.ActivityFollowerDetailsBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FollowerDetails extends AppCompatActivity {

    private ActivityFollowerDetailsBinding b;
    private SharedPreferencesManager prefs;
    private boolean theme;
    private DatabaseReference followerDb,followingDB;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private List<User> userList;
    private String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityFollowerDetailsBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        prefs = new SharedPreferencesManager(this);
        theme = prefs.retrieveBoolean("theme",false);
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
        mode = getIntent().getStringExtra("mode");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        followerDb = FirebaseDatabase.getInstance().getReference().child("Followers");
        followingDB = FirebaseDatabase.getInstance().getReference().child("Following");
        userList = new ArrayList<>();

        b.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FollowerDetails.this,Profile_Activity.class));
                finish();
            }
        });

        if (mode.equals("follower")){
            b.title.setText(getString(R.string.folower_text));
            getFollowerList();
        }else {
            b.title.setText(getString(R.string.following_txt));
            getFollowingList();
        }
    }

    private void getFollowingList() {
        followingDB.child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            userList.clear();
                            for (DataSnapshot ds : snapshot.getChildren()){
                                String id = ds.getKey().toString();
                                Query query = FirebaseDatabase.getInstance().getReference().child("Users")
                                        .orderByChild("id").equalTo(id);
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                        if (snapshot1.exists()){
                                            for (DataSnapshot ds1 : snapshot1.getChildren()){
                                                User model = ds1.getValue(User.class);
                                                userList.add(model);
                                            }
                                            UnFollowUserListAdapter adapter = new UnFollowUserListAdapter(FollowerDetails.this,userList);
                                            b.recyclerViewUser.setAdapter(adapter);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getFollowerList() {

        followerDb.child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            userList.clear();
                            for (DataSnapshot ds : snapshot.getChildren()){
                                String id = ds.getKey().toString();
                                Query query = FirebaseDatabase.getInstance().getReference().child("Users")
                                        .orderByChild("id").equalTo(id);
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                        if (snapshot1.exists()){
                                            for (DataSnapshot ds1 : snapshot1.getChildren()){
                                                User model = ds1.getValue(User.class);
                                                userList.add(model);
                                            }
                                            FollowUserListAdapter adapter = new FollowUserListAdapter(FollowerDetails.this,userList);
                                            b.recyclerViewUser.setAdapter(adapter);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
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
}