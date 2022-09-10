package com.moutamid.talk_togather.Major_Activities;

import static androidx.recyclerview.widget.RecyclerView.VERTICAL;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.talk_togather.Adapters.Adapter_Detail;
import com.moutamid.talk_togather.Adapters.UserListAdapter;
import com.moutamid.talk_togather.Models.RoomDetails;
import com.moutamid.talk_togather.Models.User;
import com.moutamid.talk_togather.R;
import com.moutamid.talk_togather.databinding.ActivitySearchBinding;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding b;
    private String type = "user";
    private DatabaseReference userDb,roomDb;
    private List<User> userList;
    private ArrayList<RoomDetails> roomDetailsList;
    private UserListAdapter userAdapter;
    private Adapter_Detail roomAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        userDb = FirebaseDatabase.getInstance().getReference().child("Users");
        roomDb = FirebaseDatabase.getInstance().getReference().child("Rooms");
        userList = new ArrayList<>();
        roomDetailsList = new ArrayList<>();
        b.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchActivity.this,DashBoard.class));
                finish();
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(VERTICAL);
        b.recyclerView.setLayoutManager(manager);
      
        b.users.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                b.users.setTextColor(Color.WHITE);
                b.users.setBackgroundResource(R.drawable.button);
                b.room.setTextColor(getColor(R.color.appClr2));
                b.room.setBackgroundResource(R.drawable.button2);
                type = "user";
                getUser("");
            }
        });
        b.room.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                b.room.setTextColor(Color.WHITE);
                b.room.setBackgroundResource(R.drawable.button);
                b.users.setTextColor(getColor(R.color.appClr2));
                b.users.setBackgroundResource(R.drawable.button2);
                type = "room";
                getRoom("");
            }
        });

        b.searchTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = charSequence.toString();
                if(type.equals("user")){
                    getUser(text);
                }else {
                    getRoom(text);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }


    private void getRoom(String text) {
        roomDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    roomDetailsList.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        RoomDetails model = ds.getValue(RoomDetails.class);
                        if (text.equals("")){
                            roomDetailsList.add(model);
                        }
                        else if (model.getTitle().toLowerCase().toString().contains(text)){
                            roomDetailsList.add(model);
                        }
                    }
                    roomAdapter = new Adapter_Detail(SearchActivity.this,roomDetailsList);
                    b.recyclerView.setAdapter(roomAdapter);
                    roomAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUser(String text) {
        userDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    userList.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        User model = ds.getValue(User.class);
                        String fullname = model.getFname() + " " + model.getLname();
                        if (text.equals("")){
                            userList.add(model);
                        }
                        else if (fullname.toLowerCase().toString().contains(text)){
                            userList.add(model);
                        }
                    }
                    userAdapter = new UserListAdapter(SearchActivity.this,userList);
                    b.recyclerView.setAdapter(userAdapter);
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}