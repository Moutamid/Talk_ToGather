package com.moutamid.talk_togather.Adapters;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.talk_togather.Major_Activities.User_Profile_Activity;
import com.moutamid.talk_togather.Models.RoomDetails;
import com.moutamid.talk_togather.Models.User;
import com.moutamid.talk_togather.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Adapter_Upcoming extends RecyclerView.Adapter<Adapter_Upcoming.HolderAndroid> {

    private Context context;
    private ArrayList<RoomDetails> androidArrayList;
    private ArrayList<String> userList = new ArrayList<>();

    public Adapter_Upcoming(Context context, ArrayList<RoomDetails> androidArrayList) {
        this.context = context;
        this.androidArrayList = androidArrayList;
    }

    @NonNull
    @Override
    public HolderAndroid onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_upcoming, parent, false);
        return new HolderAndroid(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderAndroid holder, int position) {
        RoomDetails modelAndroid = androidArrayList.get(position);

        String title_tv = modelAndroid.getCategory();
        String heading_tv = modelAndroid.getTitle();
        String timer_tv = modelAndroid.getDate();

        getDaysHours(modelAndroid.getTimestamp(),holder.timer);

        holder.title.setText(title_tv);
        holder.heading.setText(heading_tv);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference roomDB = FirebaseDatabase.getInstance().getReference().child("Rooms");
        roomDB.child(modelAndroid.getId()).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String key = ds.getKey().toString();
                        userList.add(key);
                    }
                    if (userList.size() == 1) {
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users")
                                .child(userList.get(0));
                        db.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    User model = snapshot.getValue(User.class);
                                    if (model.getImageUrl().equals("")){
                                        Picasso.with(context)
                                                .load(R.drawable.profile)
                                                .into(holder.image1);
                                    }else {
                                        Picasso.with(context)
                                                .load(model.getImageUrl())
                                                .into(holder.image1);
                                    }
                                    holder.image1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (!model.getId().equals(user.getUid())) {
                                                Intent intent = new Intent(context, User_Profile_Activity.class);
                                                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.putExtra("userUid", model.getId());
                                                context.startActivity(intent);
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }else if (userList.size() == 2) {
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users")
                                .child(userList.get(0));
                        db.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    User model = snapshot.getValue(User.class);
                                    if (model.getImageUrl().equals("")){
                                        Picasso.with(context)
                                                .load(R.drawable.profile)
                                                .into(holder.image1);
                                    }else {
                                        Picasso.with(context)
                                                .load(model.getImageUrl())
                                                .into(holder.image1);
                                    }
                                    holder.image1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (!model.getId().equals(user.getUid())) {
                                                Intent intent = new Intent(context, User_Profile_Activity.class);
                                                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.putExtra("userUid", model.getId());
                                                context.startActivity(intent);
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        DatabaseReference db1 = FirebaseDatabase.getInstance().getReference().child("Users")
                                .child(userList.get(1));
                        db1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    User model = snapshot.getValue(User.class);
                                    if (model.getImageUrl().equals("")){
                                        Picasso.with(context)
                                                .load(R.drawable.profile)
                                                .into(holder.image2);
                                    }else {
                                        Picasso.with(context)
                                                .load(model.getImageUrl())
                                                .into(holder.image2);
                                    }
                                    holder.image2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (!model.getId().equals(user.getUid())) {
                                                Intent intent = new Intent(context, User_Profile_Activity.class);
                                                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.putExtra("userUid", model.getId());
                                                context.startActivity(intent);
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }else if (userList.size() == 3){
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users")
                                .child(userList.get(0));
                        db.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    User model = snapshot.getValue(User.class);
                                    if (model.getImageUrl().equals("")){
                                        Picasso.with(context)
                                                .load(R.drawable.profile)
                                                .into(holder.image1);
                                    }else {
                                        Picasso.with(context)
                                                .load(model.getImageUrl())
                                                .into(holder.image1);
                                    }
                                    holder.image1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (!model.getId().equals(user.getUid())) {
                                                Intent intent = new Intent(context, User_Profile_Activity.class);
                                                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.putExtra("userUid", model.getId());
                                                context.startActivity(intent);
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        DatabaseReference db1 = FirebaseDatabase.getInstance().getReference().child("Users")
                                .child(userList.get(1));
                        db1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    User model = snapshot.getValue(User.class);
                                    if (model.getImageUrl().equals("")){
                                        Picasso.with(context)
                                                .load(R.drawable.profile)
                                                .into(holder.image2);
                                    }else {
                                        Picasso.with(context)
                                                .load(model.getImageUrl())
                                                .into(holder.image2);
                                    }
                                    holder.image2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (!model.getId().equals(user.getUid())) {
                                                Intent intent = new Intent(context, User_Profile_Activity.class);
                                                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.putExtra("userUid", model.getId());
                                                context.startActivity(intent);
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        DatabaseReference db2 = FirebaseDatabase.getInstance().getReference().child("Users")
                                .child(userList.get(2));
                        db2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    User model = snapshot.getValue(User.class);
                                    if (model.getImageUrl().equals("")){
                                        Picasso.with(context)
                                                .load(R.drawable.profile)
                                                .into(holder.image3);
                                    }else {
                                        Picasso.with(context)
                                                .load(model.getImageUrl())
                                                .into(holder.image3);
                                    }
                                    holder.image3.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (!model.getId().equals(user.getUid())) {
                                                Intent intent = new Intent(context, User_Profile_Activity.class);
                                                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.putExtra("userUid", model.getId());
                                                context.startActivity(intent);
                                            }
                                        }
                                    });
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

    private void getDaysHours(long timestamp, TextView timer) {
        try {
            SimpleDateFormat sdf = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);

                //Date dateBefore = sdf.parse("04/21/2022");
                //  Date dateAfter = sdf.parse("04/25/2022");
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTimeInMillis(timestamp);
                Calendar calendar2 = Calendar.getInstance();
                String date = sdf.format(calendar1.getTime());
                String date1 = sdf.format(calendar2.getTime());
                Date dateBefore = sdf.parse(date);
                Date dateAfter = sdf.parse(date1);
                long timeDiff = Math.abs(dateAfter.getTime() - dateBefore.getTime());
                long daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
                int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(daysDiff);
                int minute = (int) TimeUnit.MILLISECONDS.toMinutes(daysDiff);
                int hours = (int) TimeUnit.MILLISECONDS.toHours(daysDiff);
                //  int days = (int) TimeUnit.MILLISECONDS.toDays(timeDiff);

                if (daysDiff == 0) {
                    timer.setText("Today");
                } else {
                    timer.setText(daysDiff + " day ");
                }
                System.out.println("The number of days between dates: " + daysDiff);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return androidArrayList.size();
    }

    class HolderAndroid extends RecyclerView.ViewHolder {

        private ImageView image1 , image2 , image3;
        private TextView title , heading , timer;

        HolderAndroid(@NonNull View itemView) {
            super(itemView);

            image1 = itemView.findViewById(R.id.img_1);
            image2 = itemView.findViewById(R.id.img_2);
            image3 = itemView.findViewById(R.id.img_3);
            title = itemView.findViewById(R.id.upcoming_title);
            heading = itemView.findViewById(R.id.upcoming_heading);
            timer = itemView.findViewById(R.id.timer);

        }
    }
}

