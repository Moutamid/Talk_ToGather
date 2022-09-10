package com.moutamid.talk_togather.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.talk_togather.Major_Activities.Chat_Activity;
import com.moutamid.talk_togather.Major_Activities.Profile_Activity;
import com.moutamid.talk_togather.Major_Activities.Users_Activity;
import com.moutamid.talk_togather.Models.BlockedUser;
import com.moutamid.talk_togather.Models.Chat;
import com.moutamid.talk_togather.Models.Conversation;
import com.moutamid.talk_togather.Models.User;
import com.moutamid.talk_togather.R;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder> {

    private List<Conversation> conversationList;
    private DatabaseReference mUserReference;
    private Context mContext;
    boolean isBlocked = false;

    public ChatListAdapter(Context context, List<Conversation> conversations) {
        this.mContext = context;
        this.conversationList = conversations;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_users, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        mUserReference = FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String userUid = mUser.getUid();
        Conversation conversation = conversationList.get(position);
  //      if (!conversation.isHide()) {
            String id = conversation.getChatWithId();
            Log.i("listadapter", "id: " + id);

        Query query = mUserReference.orderByChild("id").equalTo(id);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                   for (DataSnapshot ds : dataSnapshot.getChildren()){
                       final User user = ds.getValue(User.class);
                       holder.name.setText(user.getFname() + " " + user.getLname());
                       if (user.getImageUrl().equals("")){
                           Picasso.with(mContext)
                                   .load(R.drawable.profile)
                                   .into(holder.image1);
                       }else{
                           Picasso.with(mContext)
                                   .load(user.getImageUrl())
                                   .into(holder.image1);
                       }
                       holder.itemView.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View view) {
                               Conversation conversation = conversationList.get(position);
                               DatabaseReference reference = FirebaseDatabase.getInstance().
                                       getReference("Blocked Users")
                                       .child(userUid).child(id);
                               reference.addValueEventListener(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                                       if (snapshot.exists()){
                                           showUnBlockDialogBox(id);
                                       }else {
                                           Conversation conversation = conversationList.get(position);
                                           clearUnreadChat(conversation.getChatWithId());
                                           Intent intent = new Intent(mContext, Chat_Activity.class);
                                           // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                           intent.putExtra(Chat_Activity.EXTRAS_USER, user);
                                           intent.putExtra("userUid", conversation.getChatWithId());
                                           mContext.startActivity(intent);
                                       }
                                   }

                                   @Override
                                   public void onCancelled(@NonNull DatabaseError error) {

                                   }
                               });
                           }
                       });
                   }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        long current = System.currentTimeMillis()*1000;
        if (conversation.getTimestamp() == current){
            holder.time.setText("Just Now");
        }else {
            Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
            calendar.setTimeInMillis(conversation.getTimestamp() * 1000);
            long tes = calendar.getTimeInMillis();
            DateFormat.format("M/dd/yyyy", calendar);
            CharSequence now = DateUtils.getRelativeTimeSpanString(tes, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
            holder.time.setText(now);
        }
        if (conversation.getUnreadChatCount() == 0){
            holder.number.setVisibility(View.GONE);
        }else {
            holder.number.setVisibility(View.VISIBLE);
            holder.number.setText(String.valueOf(conversation.getUnreadChatCount()));
        }

        if (!conversation.getLastMessage().equals("")) {
                if (conversation.getType().equals("text")) {
                    holder.last_msg.setText(conversation.getLastMessage());
                }
//                unreadMsg(conversation.getChatWithId(), holder.number);
            }else {
                holder.last_msg.setText("");
            }


    }

    private void showUnBlockDialogBox(String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = ((Users_Activity)mContext).getLayoutInflater();
        View add_view = inflater.inflate(R.layout.unblock_alert_dialog_screen,null);

        AppCompatButton addBtn = add_view.findViewById(R.id.yes);
        AppCompatButton cancelBtn = add_view.findViewById(R.id.cancel);
        builder.setView(add_view);
        AlertDialog alertDialog = builder.create();
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unBlockUser(id);
                alertDialog.dismiss();
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

    private void unBlockUser(String userId){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Blocked Users")
                .child(user.getUid()).child(userId);
        reference.removeValue();
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView image1 ;
        private ImageView user_check ;
        private ImageView close ;
        private TextView name , last_msg , time , number;
        private LinearLayout user_layout;
        private LinearLayout layout_goto_msg;
        private View user_item_back_view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image1 = itemView.findViewById(R.id.user_img);
            name = itemView.findViewById(R.id.user_name);
            last_msg = itemView.findViewById(R.id.user_last_msg);
            time = itemView.findViewById(R.id.user_time);
            number = itemView.findViewById(R.id.user_msg_number);
            user_layout = itemView.findViewById(R.id.user_layout);
            user_item_back_view = itemView.findViewById(R.id.user_item_view);
            layout_goto_msg = itemView.findViewById(R.id.layout_goto_msg);
            user_check = itemView.findViewById(R.id.user_check);
        }
    }

    private void clearUnreadChat(String chatWithId) {
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference conversationReference = FirebaseDatabase.getInstance().getReference().child("conversation").child(mFirebaseUser.getUid()).child(chatWithId).child("unreadChatCount");
        conversationReference.setValue(0);
    }

    private void unreadMsg(final String userId, final TextView msgCount){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("chats");
        String userUid = firebaseUser.getUid();

        db.child(userUid).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int unread = 0;
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Chat message = ds.getValue(Chat.class);
                    if (message.getReceiverUid().equals(firebaseUser.getUid()) && message.getSenderUid().equals(userId)){
                        unread++;
                    }

                }
                if (unread == 0) {
                    msgCount.setVisibility(View.GONE);
                }else {
                    msgCount.setVisibility(View.VISIBLE);
                    msgCount.setText(String.valueOf(unread));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
