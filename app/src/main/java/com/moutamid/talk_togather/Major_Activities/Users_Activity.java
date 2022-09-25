package com.moutamid.talk_togather.Major_Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.ads.AdListener;
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
import com.moutamid.talk_togather.Adapters.ChatListAdapter;
import com.moutamid.talk_togather.Adapters.SelectedChatListAdapter;
import com.moutamid.talk_togather.Models.Conversation;
import com.moutamid.talk_togather.R;
import com.moutamid.talk_togather.SharedPreferencesManager;
import com.moutamid.talk_togather.listener.ItemCheckboxClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Users_Activity extends AppCompatActivity {

    ImageView close;
    ImageView delete;
    ImageView menu;
    CardView user_menu_card;
    ImageView user_menu_card_close;
    TextView blockBtn,selectBtn;

    private RecyclerView user_recycler,selected_user_recycler;
    private List<Conversation> conversationList = new ArrayList<>();
    private ChatListAdapter mAdapter;
    DatabaseReference mConversationReference;
    private FirebaseAuth mAuth;
    private SelectedChatListAdapter adapter;
    private FirebaseUser user;
    private SharedPreferencesManager prefs;
    private boolean theme;
    int pos = 0;
    private String uid = "";


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

        MobileAds.initialize(getApplicationContext(), getString(R.string.admob_app_id));
        AdView mAdView = (AdView) findViewById(R.id.adView);
        ImageView placeImage = (ImageView) findViewById(R.id.placeholder);
        mAdView.setAdListener(new AdListener() {
            private void showToast(String message) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLoaded() {
                Log.d("Error", String.valueOf("Ads Load"));
                if (mAdView.getVisibility() == View.GONE) {
                    mAdView.setVisibility(View.VISIBLE);
                    placeImage.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
              //  showToast(String.format("Ad failed to load with error code %d.", errorCode));
                Log.d("Error", String.valueOf(errorCode));
                mAdView.setVisibility(View.GONE);
                placeImage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdOpened() {
                Log.d("Error", String.valueOf("Ads Open"));
            }

            @Override
            public void onAdClosed() {
                Log.d("Error", String.valueOf("Ads Close"));
            }

            @Override
            public void onAdLeftApplication() {
                showToast("Ad left application.");
            }
        });

        AdRequest request = new AdRequest.Builder().build();
        mAdView.loadAd(request);
        getLocale();
        close = findViewById(R.id.close_user);
        delete = findViewById(R.id.delete_user);
        blockBtn = findViewById(R.id.block);
        selectBtn = findViewById(R.id.select);
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
        mConversationReference = FirebaseDatabase.getInstance().getReference().child("conversation");

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

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user_recycler.setVisibility(View.GONE);
                selected_user_recycler.setVisibility(View.VISIBLE);
                user_menu_card.setVisibility(View.GONE);
                menu.setVisibility(View.VISIBLE);
                user_menu_card_close.setVisibility(View.GONE);
                if (user != null) {
                    getSelectionChatList();
                }
            }
        });

        blockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blockUser(uid);

                user_recycler.setVisibility(View.VISIBLE);
                selected_user_recycler.setVisibility(View.GONE);
                user_menu_card.setVisibility(View.GONE);
                menu.setVisibility(View.VISIBLE);
                user_menu_card_close.setVisibility(View.GONE);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             /*   user_recycler.setVisibility(View.GONE);
                selected_user_recycler.setVisibility(View.VISIBLE);
                getSelectionChatList("delete");*/
                deleteChat(uid,pos);

                user_recycler.setVisibility(View.VISIBLE);
                selected_user_recycler.setVisibility(View.GONE);
                user_menu_card.setVisibility(View.GONE);
                menu.setVisibility(View.VISIBLE);
                user_menu_card_close.setVisibility(View.GONE);
            }
        });
        if (user != null) {
            getGeneralChatList();
        }
    }

    private void getSelectionChatList() {
        Query myQuery = mConversationReference.child(user.getUid()).orderByChild("timestamp");
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
                    adapter = new SelectedChatListAdapter(Users_Activity.this,
                            conversationList);
                    adapter.setItemCheckboxClickListener(new ItemCheckboxClickListener() {
                        @Override
                        public void onItemClick(int position, boolean b) {
                            if (b){
                                delete.setClickable(true);
                                blockBtn.setEnabled(true);
                            }else {
                                delete.setClickable(false);
                                blockBtn.setEnabled(false);
                            }
                            pos = position;
                            uid = conversationList.get(position).getChatWithId();
                        }
                    });
                    selected_user_recycler.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void deleteChat(String id, int position) {
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference conversationReference = FirebaseDatabase.getInstance().getReference()
                .child("conversation").child(mFirebaseUser.getUid()).child(id);
        conversationReference.removeValue();
        conversationList.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeRemoved(position,conversationList.size());
    }

    private void blockUser(String userId){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Blocked Users")
                .child(user.getUid()).child(userId);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", userId);
        reference.updateChildren(hashMap);
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
        Query myQuery = mConversationReference.child(user.getUid()).orderByChild("timestamp");
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