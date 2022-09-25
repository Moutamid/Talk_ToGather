package com.moutamid.talk_togather.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.talk_togather.Models.Notification;
import com.moutamid.talk_togather.Models.User;
import com.moutamid.talk_togather.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context mContext;
    private List<Notification> mNotifications;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendDatabase;
    private User user1;

    public NotificationAdapter(Context mContext, List<Notification> mNotifications){
        this.mContext = mContext;
        this.mNotifications = mNotifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_notification, parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Notification notification = mNotifications.get(position);

        if(notification.getFromUserId() != null) {
            if(notification.getNotificationType().equals("follow_request")) {
                onFriendReq(notification,holder);
            }
           else if(notification.getNotificationType().equals("message_sent")){
                onMessageSent(notification,holder);
            }
        }
        
        checkMarks(holder.markBtn,holder.notification_item_back_view,notification.getId());

        holder.notification_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                markAsUnRead(notification.getNotificationType(),notification.getId());
                holder.markBtn.setVisibility(View.VISIBLE);
                holder.notification_item_back_view.setVisibility(View.VISIBLE);
                return true;
            }
        });

        holder.notification_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markAsRead(notification.getNotificationType(),notification.getId(),notification.getFromUserId());
                holder.markBtn.setVisibility(View.GONE);
                holder.notification_item_back_view.setVisibility(View.GONE);
            }
        });
    }

    private void checkMarks(TextView markBtn, View notification_item_back_view, long id) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Notifications")
                .child(firebaseUser.getUid()).orderByChild("id").equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Notification model = ds.getValue(Notification.class);
                        if (model.isSeen()){
                            markBtn.setVisibility(View.GONE);
                            notification_item_back_view.setVisibility(View.GONE);
                        }else {
                            markBtn.setVisibility(View.VISIBLE);
                            notification_item_back_view.setVisibility(View.VISIBLE);

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void markAsUnRead(String notificationType, long id) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications")
                .child(firebaseUser.getUid());
        if (notificationType.equals("follow_request")){
            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("seen",false);
            reference.child(String.valueOf(id)).updateChildren(hashMap);
        }else {
            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("seen",false);
            reference.child(String.valueOf(id)).updateChildren(hashMap);

        }
    }

    private void markAsRead(String notificationType, long id, String fromUserId) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications")
                .child(firebaseUser.getUid());
        if (notificationType.equals("follow_request")){
            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("seen",true);
            reference.child(String.valueOf(id)).updateChildren(hashMap);
        }else {
            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("seen",true);
            reference.child(String.valueOf(id)).updateChildren(hashMap);
            clearUnreadChat(fromUserId);
        }
    }
    private void clearUnreadChat(String chatWithId) {
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference conversationReference = FirebaseDatabase.getInstance().getReference().child("conversation").child(mFirebaseUser.getUid()).child(chatWithId).child("unreadChatCount");
        conversationReference.setValue(0);
    }

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView notificationBody;
        public TextView notificationTime;
        public TextView markBtn;
        public ImageView profile_image;
        public LinearLayout notification_layout;
        public View notification_item_back_view;

        public ViewHolder(View itemView){
            super(itemView);

            notificationBody = itemView.findViewById(R.id.notification_heading);
            notificationTime = itemView.findViewById(R.id.notification_time);
            profile_image = itemView.findViewById(R.id.notification_img);
            markBtn = itemView.findViewById(R.id.notification_status);
            notification_layout = itemView.findViewById(R.id.notification_layout);
            notification_item_back_view = itemView.findViewById(R.id.notification_item_back_view);


        }
    }

    private void onGroupMessageSent(Notification notification, ViewHolder holder){

        holder.notificationBody.setVisibility(View.VISIBLE);

        long timeInSeconds = (System.currentTimeMillis() - notification.getNotificationTime()) / 1000;
        String time = timeInSeconds + "s";
        if(timeInSeconds > 60 && timeInSeconds / 60 < 60){
            time = timeInSeconds / 60 + "m";
        }
        else if(timeInSeconds / 60 > 60 && timeInSeconds / 60 / 60 < 60){
            time = timeInSeconds / 60 / 60 + "h";
        }
        else if(timeInSeconds / 60 / 60 > 60 && timeInSeconds / 60 / 60 / 60 < 60){
            time = timeInSeconds / 60 / 60 / 60 + " day(s)";

        }

        holder.notificationTime.setText(time);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(notification.getFromUserId());
        final User[] user1 = {new User()};

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final User[] user = {dataSnapshot.getValue(User.class)};
                user1[0] = user[0];
                mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user[0].getId());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void onMessageSent(Notification notification, ViewHolder holder){

        holder.notificationBody.setVisibility(View.VISIBLE);


        long timeInSeconds = (System.currentTimeMillis() - notification.getNotificationTime()) / 1000;
        String time = timeInSeconds + "s";
        if(timeInSeconds > 60 && timeInSeconds / 60 < 60){
            time = timeInSeconds / 60 + "m";
        }
        else if(timeInSeconds / 60 > 60 && timeInSeconds / 60 / 60 < 60){
            time = timeInSeconds / 60 / 60 + "h";
        }
        else if(timeInSeconds / 60 / 60 > 60 && timeInSeconds / 60 / 60 / 60 < 60){
            time = timeInSeconds / 60 / 60 / 60 + " day(s)";

        }

        holder.notificationTime.setText(time);


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(notification.getFromUserId());
      //  final User[] user1 = {new User()};
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final User user = dataSnapshot.getValue(User.class);
                    //        user1[0] = user[0];
                    mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users")
                            .child(user.getId());
                    String userFirstName = "";
                    if (user.getFname().contains(" ")) {
                        userFirstName = user.getFname().substring(0, user.getFname().indexOf(" "));
                    } else {
                        userFirstName = user.getFname();
                    }
                    holder.notificationBody.setText(userFirstName + " sent you a message");
                    if (user.getImageUrl().equals("")) {
                        holder.profile_image.setImageResource(R.drawable.profile);
                    } else {
                        Picasso
                                .with(mContext)
                                .load(user.getImageUrl())
                                .into(holder.profile_image);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void onFriendReq(Notification notification, ViewHolder holder){

        long timeInSeconds = (System.currentTimeMillis() - notification.getNotificationTime()) / 1000;
        String time = timeInSeconds + "s";
        if(timeInSeconds > 60 && timeInSeconds / 60 < 60){
            time = timeInSeconds / 60 + "m";
        }
        else if(timeInSeconds / 60 > 60 && timeInSeconds / 60 / 60 < 60){
            time = timeInSeconds / 60 / 60 + "h";
        }
        else if(timeInSeconds / 60 / 60 > 60 && timeInSeconds / 60 / 60 / 60 < 60){
            time = timeInSeconds / 60 / 60 / 60 + " day(s)";

        }

        holder.notificationTime.setText(time);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(notification.getFromUserId());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final User[] user = {dataSnapshot.getValue(User.class)};
                mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user[0].getId());
                mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
                mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
                String userFirstName = "";
                if(user[0].getFname().contains(" ")){
                    userFirstName = user[0].getFname().substring(0, user[0].getFname().indexOf(" "));
                }
                else{
                    userFirstName = user[0].getFname();
                }
                holder.notificationBody.setText(userFirstName + " start following you....");
                if (user[0].getImageUrl().equals("")) {
                    holder.profile_image.setImageResource(R.drawable.profile);
                } else {
                    Picasso
                            .with(mContext)
                            .load(user[0].getImageUrl())
                            .into(holder.profile_image)
                    ;
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}