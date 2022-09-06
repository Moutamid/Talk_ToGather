package com.moutamid.talk_togather.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.moutamid.talk_togather.listener.ItemCheckboxClickListener;
import com.moutamid.talk_togather.listener.ItemClickListener;

import java.util.ArrayList;

public class categoriesListAdapter extends RecyclerView.Adapter<categoriesListAdapter.HolderAndroid> {

    private Context context;
    private int[] androidArrayList;
    private ItemCheckboxClickListener itemClickListener;

    public categoriesListAdapter(Context context, int[] androidArrayList) {
        this.context = context;
        this.androidArrayList = androidArrayList;
    }

    @NonNull
    @Override
    public HolderAndroid onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_selected_catagories, parent, false);
        return new HolderAndroid(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderAndroid holder, int position) {
        holder.nameTxt.setText(context.getString(androidArrayList[position]));
    }

    @Override
    public int getItemCount() {
        return androidArrayList.length;
    }

    class HolderAndroid extends RecyclerView.ViewHolder {

        private CheckBox checkBox;
        private TextView nameTxt;

        HolderAndroid(@NonNull View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.checkbox);
            nameTxt = itemView.findViewById(R.id.name);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (itemClickListener != null){
                        itemClickListener.onItemClick(getAdapterPosition(),b);
                    }
                }
            });
        }
    }

    public void  setItemClickListener(ItemCheckboxClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }
}
