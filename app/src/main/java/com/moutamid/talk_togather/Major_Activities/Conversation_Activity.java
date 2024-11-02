package com.moutamid.talk_togather.Major_Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.talk_togather.AGApplication;
import com.moutamid.talk_togather.Adapters.Adapter_Conversation;
import com.moutamid.talk_togather.Models.Model_Conversation;
import com.moutamid.talk_togather.Models.RoomDetails;
import com.moutamid.talk_togather.Models.User;
import com.moutamid.talk_togather.R;
import com.moutamid.talk_togather.SharedPreferencesManager;
import com.moutamid.talk_togather.listener.ItemClickListener;
import com.moutamid.talk_togather.model.AGEventHandler;
import com.moutamid.talk_togather.model.ConstantApp;
import com.moutamid.talk_togather.model.EngineConfig;
import com.moutamid.talk_togather.model.MyEngineEventHandler;
import com.moutamid.talk_togather.model.WorkerThread;
import com.squareup.picasso.Picasso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

public class Conversation_Activity extends AppCompatActivity implements AGEventHandler {

    private TextView leave_room;
    private TextView nameTxt, categoryTxt, titleTxt;
    private CircleImageView profileImg;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference db, userDB;
    private RecyclerView recyclerView;
    private ImageView micOn, micOff;
    private String id = "";
    private String creatorId = "";
    private ArrayList<Model_Conversation> conversationArrayList = new ArrayList<>();
    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;
    private final static Logger log = LoggerFactory.getLogger(Chat_Activity.class);
    //    private volatile boolean mAudioMuted = false;
    private SharedPreferencesManager prefs;
    private boolean micStatus;
    private boolean asked = false;
    private volatile int mAudioRouting = -1; // Default
    private boolean theme;
    private Adapter_Conversation adapter_conversation;
    private boolean muteLocal = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new SharedPreferencesManager(this); //get SharedPreferencesManager  instance
        theme = prefs.retrieveBoolean("theme", false);//get stored theme, zero is default
        setContentView(R.layout.activity_conversation);
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.bounce);

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
        leave_room = findViewById(R.id.leave_room);
        nameTxt = findViewById(R.id.name);
        categoryTxt = findViewById(R.id.text_heading);
        titleTxt = findViewById(R.id.text_title);
        profileImg = findViewById(R.id.profile);

//        micOn = findViewById(R.id.speakerOn);
//        micOff = findViewById(R.id.speakerOff);

        recyclerView = findViewById(R.id.recyclerView_convesation);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        micStatus = prefs.retrieveBoolean("isMicEnable", false);
        id = getIntent().getStringExtra("id");
        creatorId = getIntent().getStringExtra("createdId");
        db = FirebaseDatabase.getInstance().getReference().child("Rooms");
        userDB = FirebaseDatabase.getInstance().getReference().child("Users");
        leave_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leave_room.startAnimation(animation);
                doLeaveChannel();
            }
        });

//        micOn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                micOn.setVisibility(View.GONE);
//                micOff.setVisibility(View.VISIBLE);
//                muteLocal = false;
//                rtcEngine().muteLocalAudioStream(true);
//            }
//        });
//
//        micOff.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                micOn.setVisibility(View.VISIBLE);
//                micOff.setVisibility(View.GONE);
//                muteLocal = true;
//                rtcEngine().muteLocalAudioStream(false);
//            }
//        });

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, PERMISSION_REQ_ID_RECORD_AUDIO)) {
            ((AGApplication) getApplication()).initWorkerThread();
            getRoomDetails();
            checkRoomDB();
            getRoomsUsers();
            asked = true;
//            if (muteLocal){
//                micOn.setVisibility(View.VISIBLE);
//                micOff.setVisibility(View.GONE);
//            }else {
//                micOn.setVisibility(View.GONE);
//                micOff.setVisibility(View.VISIBLE);
//                rtcEngine().muteLocalAudioStream(true);
//            }
//            rtcEngine().muteLocalAudioStream(muteLocal);
        }


    }

    private boolean checkSelfPermission(String permission, String writeExternalStorage, int recordAudio) {
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission, writeExternalStorage},
                    recordAudio);
            return true;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQ_ID_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,PERMISSION_REQ_ID_RECORD_AUDIO);
                    ((AGApplication) getApplication()).initWorkerThread();
                    getRoomDetails();
                    checkRoomDB();
                    getRoomsUsers();
                    asked = true;
                } else {
                    finish();
                }
                break;
            }
        }
    }

    private void getRoomsUsers() {
        db.child(id).child("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            conversationArrayList.clear();
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                Model_Conversation conversation = ds.getValue(Model_Conversation.class);
                                if (!conversation.getId().equals(user.getUid())) {
                                    conversationArrayList.add(conversation);
                                }
                            }
                            adapter_conversation = new Adapter_Conversation(Conversation_Activity.this,
                                    conversationArrayList, id);
                            recyclerView.setAdapter(adapter_conversation);
                            adapter_conversation.setItemClickListener(new ItemClickListener() {
                                @Override
                                public void onItemClick(int position, View view) {
                                    Model_Conversation model = conversationArrayList.get(position);
//                                    if (model.isMic_status()){
//                                        // saveMicStatus(false,model.getId());
//                                        rtcEngine().muteRemoteAudioStream(config().mUid,true);
//                                    }else {
//                                        // saveMicStatus(true,model.getId());
//                                        rtcEngine().muteRemoteAudioStream(config().mUid,false);
//                                    }
                                }
                            });
                            adapter_conversation.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

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


    // Tutorial Step 7
    public void onLocalAudioMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.setImageResource(R.drawable.ic_baseline_mic_none_24);
            iv.setBackgroundResource(R.drawable.shape_circle);
            // iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setImageResource(R.drawable.ic_baseline_mic_off_24);
            iv.setBackgroundResource(R.drawable.shape_circle_white);
            //iv.setColorFilter(getResources().getColor(R.color.appClr2), PorterDuff.Mode.CLEAR);
        }

        // Stops/Resumes sending the local audio stream.
//        rtcEngine().muteLocalAudioStream(iv.isSelected());
    }

    private void checkRoomDB() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", user.getUid());
        hashMap.put("mic_status", micStatus);
        db.child(id).child("users").child(user.getUid()).updateChildren(hashMap);
    }

    private void getRoomDetails() {
        db.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    RoomDetails model = snapshot.getValue(RoomDetails.class);
                    titleTxt.setText(model.getTitle());
                    categoryTxt.setText(model.getCategory());
                    userDB.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                User model = snapshot.getValue(User.class);
                                nameTxt.setText(model.getFname() + " " + model.getLname());
                                if (model.getImageUrl().equals("")) {
                                    Picasso.with(getApplicationContext())
                                            .load(R.drawable.profile)
                                            .into(profileImg);
                                } else {
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
                    String channelName = model.getCategory();
                    worker().joinChannel(channelName, config().mUid);
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
        Intent intent = new Intent(Conversation_Activity.this, DashBoard.class);
        startActivity(intent);
        Animatoo.animateSlideDown(Conversation_Activity.this);
        finish();
    }

    private void doLeaveChannel() {
        worker().leaveChannel(config().mChannel);
        db.child(id).child("users").child(user.getUid()).removeValue();
        quitCall();
    }

    private void quitCall() {
        if (creatorId.equals(user.getUid())) {
            db.child(id).removeValue();
        }
        Intent intent = new Intent(Conversation_Activity.this, DashBoard.class);
        startActivity(intent);
        Animatoo.animateSlideDown(Conversation_Activity.this);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (asked) {
            worker().leaveChannel(config().mChannel);
            db.child(id).child("users").child(user.getUid()).removeValue();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (asked) {
            worker().leaveChannel(config().mChannel);
            db.child(id).child("users").child(user.getUid()).removeValue();
        }
    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        String msg = "onJoinChannelSuccess " + channel + " " + (uid & 0xFFFFFFFFL) + " " + elapsed;
        log.debug(msg);


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

//                rtcEngine().setEnableSpeakerphone(mAudioRouting != 3);
//                rtcEngine().muteLocalAudioStream(false);
            }
        });

    }

    @Override
    public void onUserOffline(int uid, int reason) {

    }

    @Override
    public void onExtraCallback(int type, Object... data) {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

                doHandleExtraCallback(type, data);
            }
        });
    }

    private void doHandleExtraCallback(int type, Object... data) {
        int peerUid;
        boolean muted;

        switch (type) {
            case AGEventHandler.EVENT_TYPE_ON_USER_AUDIO_MUTED: {
                peerUid = (Integer) data[0];
                muted = (boolean) data[1];

                notifyMessageChanged("mute: " + (peerUid & 0xFFFFFFFFL) + " " + muted);
                break;
            }

            case AGEventHandler.EVENT_TYPE_ON_AUDIO_QUALITY: {
                peerUid = (Integer) data[0];
                int quality = (int) data[1];
                short delay = (short) data[2];
                short lost = (short) data[3];

                notifyMessageChanged("quality: " + (peerUid & 0xFFFFFFFFL) + " " + quality + " " + delay + " " + lost);
                break;
            }

            case AGEventHandler.EVENT_TYPE_ON_SPEAKER_STATS: {
                IRtcEngineEventHandler.AudioVolumeInfo[] infos = (IRtcEngineEventHandler.AudioVolumeInfo[]) data[0];

                if (infos.length == 1 && infos[0].uid == 0) { // local guy, ignore it
                    break;
                }

                StringBuilder volumeCache = new StringBuilder();
                for (IRtcEngineEventHandler.AudioVolumeInfo each : infos) {
                    peerUid = each.uid;
                    int peerVolume = each.volume;

                    if (peerUid == 0) {
                        continue;
                    }

                    volumeCache.append("volume: ").append(peerUid & 0xFFFFFFFFL).append(" ").append(peerVolume).append("\n");
                }

                if (volumeCache.length() > 0) {
                    String volumeMsg = volumeCache.substring(0, volumeCache.length() - 1);
                    notifyMessageChanged(volumeMsg);

                    if ((System.currentTimeMillis() / 1000) % 10 == 0) {
                        log.debug(volumeMsg);
                    }
                }
                break;
            }

            case AGEventHandler.EVENT_TYPE_ON_APP_ERROR: {
                int subType = (int) data[0];

                if (subType == ConstantApp.AppError.NO_NETWORK_CONNECTION) {
                    showLongToast("No Internet ");
                }

                break;
            }

            case AGEventHandler.EVENT_TYPE_ON_AGORA_MEDIA_ERROR: {
                int error = (int) data[0];
                String description = (String) data[1];

                notifyMessageChanged(error + " " + description);

                break;
            }

            case AGEventHandler.EVENT_TYPE_ON_AUDIO_ROUTE_CHANGED: {
                notifyHeadsetPlugged((int) data[0]);
                break;
            }
        }
    }

    private void notifyMessageChanged(String msg) {
        Toast.makeText(Conversation_Activity.this, msg, Toast.LENGTH_SHORT).show();

    }

    public RtcEngine rtcEngine() {
        return ((AGApplication) getApplication()).getWorkerThread().getRtcEngine();
    }

    protected final WorkerThread worker() {
        return ((AGApplication) getApplication()).getWorkerThread();
    }

    public final EngineConfig config() {
        return ((AGApplication) getApplication()).getWorkerThread().getEngineConfig();
    }

    protected final MyEngineEventHandler event() {
        return ((AGApplication) getApplication()).getWorkerThread().eventHandler();
    }

    public final void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void notifyHeadsetPlugged(final int routing) {
        log.info("notifyHeadsetPlugged " + routing);

        mAudioRouting = routing;

    }

}