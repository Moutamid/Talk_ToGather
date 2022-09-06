package com.moutamid.talk_togather.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.talk_togather.Major_Activities.Catagory_Details_Activity;
import com.moutamid.talk_togather.Models.Model_Catagories;
import com.moutamid.talk_togather.R;

import java.util.ArrayList;

public class Adapter_Catagories extends RecyclerView.Adapter<Adapter_Catagories.HolderAndroid> {

    private Context context;
    private ArrayList<Model_Catagories> androidArrayList;

    public Adapter_Catagories(Context context, ArrayList<Model_Catagories> androidArrayList) {
        this.context = context;
        this.androidArrayList = androidArrayList;
    }

    @NonNull
    @Override
    public HolderAndroid onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_catagories, parent, false);
        return new HolderAndroid(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderAndroid holder, int position) {
        Model_Catagories modelAndroid = androidArrayList.get(position);

        String heading_tv = context.getString(modelAndroid.getHeading());
        String gathering = modelAndroid.getTime();
        int image_1 = modelAndroid.getImage1();

        holder.heading.setText(heading_tv);
        holder.gathering.setText(gathering);
        holder.image1.setImageResource(image_1);

        holder.card_catagories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context , Catagory_Details_Activity.class);
                intent.putExtra("category",heading_tv);
                context.startActivity(intent);
                Animatoo.animateSlideUp(context);
                ((Activity)context).finish();
            }
        });

        DatabaseReference roomDB = FirebaseDatabase.getInstance().getReference().child("Rooms");
        Query query = roomDB.orderByChild("category").equalTo(modelAndroid.getHeading());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        String key = ds.getKey().toString();
                        roomDB.child(key).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    holder.gathering.setText(snapshot.getChildrenCount() + " " + context.getString(R.string.gathering));
                                }else {
                                    holder.gathering.setText(0 + " " + context.getString(R.string.gathering));
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

    @Override
    public int getItemCount() {
        return androidArrayList.size();
    }

    class HolderAndroid extends RecyclerView.ViewHolder {

        private ImageView image1;
        private TextView heading , gathering;
        private CardView card_catagories;

        HolderAndroid(@NonNull View itemView) {
            super(itemView);

            image1 = itemView.findViewById(R.id.catagories_img);
            heading = itemView.findViewById(R.id.catagories_heading);
            gathering = itemView.findViewById(R.id.catagories_gathering);
            card_catagories = itemView.findViewById(R.id.card_catagories);

        }
    }
}
