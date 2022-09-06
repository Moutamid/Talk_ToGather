package com.moutamid.talk_togather.Major_Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.talk_togather.Adapters.ChatListAdapter;
import com.moutamid.talk_togather.Adapters.SelectedChatListAdapter;
import com.moutamid.talk_togather.Models.Conversation;
import com.moutamid.talk_togather.R;
import com.moutamid.talk_togather.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class Users_Activity extends AppCompatActivity {

    ImageView close;
    ImageView delete;
    ImageView menu;
    CardView user_menu_card;
    ImageView user_menu_card_close;
    TextView blockBtn,blockReportBtn;

    private RecyclerView user_recycler,selected_user_recycler;
    private List<Conversation> conversationList = new ArrayList<>();
    private ChatListAdapter mAdapter;
    DatabaseReference mConversationReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private SharedPreferencesManager prefs;
    private boolean theme;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new SharedPreferencesManager(this);
        theme = prefs.retrieveBoolean("theme",false);//get stored theme, zero is default
        setContentView(R.layout.activity_users);
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
        close = findViewById(R.id.close_user);
        delete = findViewById(R.id.delete_user);
        blockBtn = findViewById(R.id.block);
        blockReportBtn = findViewById(R.id.report);
        menu = findViewById(R.id.menu_user);
        user_menu_card = findViewById(R.id.card_menu);
        user_menu_card_close = findViewById(R.id.menu_user_close);

        user_menu_card_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user_menu_card.setVisibility(View.GONE);
                menu.setVisibility(View.VISIBLE);
                user_menu_card_close.setVisibility(View.GONE);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mConversationReference = FirebaseDatabase.getInstance().getReference().child("conversation")
                .child(user.getUid());

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user_menu_card.setVisibility(View.VISIBLE);
                user_menu_card_close.setVisibility(View.VISIBLE);
                menu.setVisibility(View.GONE);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Users_Activity.this , DashBoard.class);
                startActivity(intent);
                Animatoo.animateSlideDown(Users_Activity.this);
                finish();
            }
        });
        user_recycler = findViewById(R.id.recyclerView_user);
        selected_user_recycler = findViewById(R.id.recyclerView_selected_user);
        blockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user_recycler.setVisibility(View.GONE);
                selected_user_recycler.setVisibility(View.VISIBLE);
                user_menu_card.setVisibility(View.GONE);
                menu.setVisibility(View.VISIBLE);
                user_menu_card_close.setVisibility(View.GONE);
                getSelectionChatList("block");

            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user_recycler.setVisibility(View.GONE);
                selected_user_recycler.setVisibility(View.VISIBLE);
                getSelectionChatList("delete");
            }
        });
        getGeneralChatList();
    }
    private void getSelectionChatList(String type) {
        Query myQuery = mConversationReference.orderByChild("timestamp");
        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    conversationList.clear();
                    //  mStartChatLayout.setVisibility(View.GONE);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Conversation conversation = snapshot.getValue(Conversation.class);
                        conversationList.add(conversation);
                    }
                    Collections.sort(conversationList, new Comparator<Conversation>() {
                        @Override
                        public int compare(Conversation conversation, Conversation t1) {
                            return Long.compare(t1.getTimestamp(),conversation.getTimestamp());
                        }
                    });
                    SelectedChatListAdapter adapter = new SelectedChatListAdapter(Users_Activity.this, conversationList,type);
                    selected_user_recycler.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

    private void getGeneralChatList() {
        Query myQuery = mConversationReference.orderByChild("timestamp");
        myQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    conversationList.clear();
                    //  mStartChatLayout.setVisibility(View.GONE);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Conversation conversation = snapshot.getValue(Conversation.class);
                        conversationList.add(conversation);
                        }
                    Collections.sort(conversationList, new Comparator<Conversation>() {
                        @Override
                        public int compare(Conversation conversation, Conversation t1) {
                            return Long.compare(t1.getTimestamp(),conversation.getTimestamp());
                        }
                    });
                    mAdapter = new ChatListAdapter(Users_Activity.this, conversationList);
                    user_recycler.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(Users_Activity.this , DashBoard.class);
        startActivity(intent);
        Animatoo.animateSlideDown(Users_Activity.this);
        finish();
    }
}