package com.moutamid.talk_togather.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.talk_togather.Models.Chat;
import com.moutamid.talk_togather.Models.User;
import com.moutamid.talk_togather.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_Chat extends RecyclerView.Adapter<Adapter_Chat.HolderAndroid> {

    private Context context;
    private ArrayList<Chat> androidArrayList;

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    public Adapter_Chat(Context context, ArrayList<Chat> androidArrayList) {
        this.context = context;
        this.androidArrayList = androidArrayList;
    }

    @Override
    public int getItemViewType(int position) {
        Chat chat = androidArrayList.get(position);

       /* if (position == 0){
            return TYPE_TIME;
        }*/
        return tes(chat);
    }

    private int tes(Chat chat) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser().getUid().equalsIgnoreCase(chat.getSenderUid())) {
            return MSG_TYPE_RIGHT;
        }
        return MSG_TYPE_LEFT;
    }


    @NonNull
    @Override
    public HolderAndroid onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        //inflate layouts: row_chat_left.xml for receiver, row_Chat_right.xml for sender
        View view;
        if (viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, viewGroup, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, viewGroup, false);
        }
        return new HolderAndroid(view);
    }

    public void addItem(Chat datum) {
        androidArrayList.add(datum);
        notifyItemInserted(androidArrayList.size() - 1);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderAndroid holder, int position) {
        Chat modelAndroid = androidArrayList.get(position);

        String message = modelAndroid.getMessage();
        holder.message_chat.setText(message);
        getProfilePic(modelAndroid.getSenderUid(),holder.sender_img);
        long current = System.currentTimeMillis()*1000;
        if (modelAndroid.getTimestamp() == current){
            holder.time_chat.setText("Just Now");
        }else {
            Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
            calendar.setTimeInMillis(modelAndroid.getTimestamp() * 1000);
            long tes = calendar.getTimeInMillis();
            DateFormat.format("M/dd/yyyy", calendar);
            CharSequence now = DateUtils.getRelativeTimeSpanString(tes, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
            holder.time_chat.setText(now);
        }
    }

    private void getProfilePic(String receiverUid, CircleImageView imageView) {

        //  Toast.makeText(mContext,receiverUid,Toast.LENGTH_LONG).show();

        DatabaseReference mUserReference = FirebaseDatabase.getInstance().getReference().child("Users").child(receiverUid);
        mUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                if (ds.exists()){
                    User model = ds.getValue(User.class);
                    if (model.getImageUrl().equals("")){
                        Picasso.with(context)
                                .load(R.drawable.profile)
                                .into(imageView);
                    }else{
                        Picasso.with(context)
                                .load(model.getImageUrl())
                                .into(imageView);
                    }



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setDate(String timestamp, TextView time_chat) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        try {
            long time = sdf.parse(timestamp).getTime();
            long now = System.currentTimeMillis();
            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            time_chat.setText(ago + "");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


   /* @Override
    public int getItemViewType(int position) {
        //get currently signed in user
        if (position % 2 != 0) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }*/

    @Override
    public int getItemCount() {
        return androidArrayList.size();
    }

    class HolderAndroid extends RecyclerView.ViewHolder {

        private CircleImageView sender_img ;
        private TextView message_chat ;
        private TextView time_chat ;

        HolderAndroid(@NonNull View itemView) {
            super(itemView);

            sender_img = itemView.findViewById(R.id.sender_img);
            message_chat = itemView.findViewById(R.id.message_chat);
            time_chat = itemView.findViewById(R.id.time_chat);

        }
    }
}

