package com.moutamid.talk_togather.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.talk_togather.Major_Activities.User_Profile_Activity;
import com.moutamid.talk_togather.Models.User;
import com.moutamid.talk_togather.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UnFollowUserListAdapter extends RecyclerView.Adapter<UnFollowUserListAdapter.UserListViewHolder> {

    private Context context;
    private List<User> userList;

    public UnFollowUserListAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.follower_row_users,parent,false);
        return new UserListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserListViewHolder holder, @SuppressLint("RecyclerView") int position) {
        User model = userList.get(position);
        holder.nameTxt.setText(model.getFname()+" " + model.getLname());
        holder.unfollow.setVisibility(View.VISIBLE);
        if (model.getImageUrl().equals("")){
            Picasso.with(context)
                    .load(R.drawable.profile)
                    .into(holder.profileImg);
        }else{
            Picasso.with(context)
                    .load(model.getImageUrl())
                    .into(holder.profileImg);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, User_Profile_Activity.class);
                intent.putExtra("userUid", model.getId());
                context.startActivity(intent);
            }
        });

        holder.unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unfollowUser(model.getId(),position);
            }
        });
    }

    private void unfollowUser(String id, int position) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseDatabase.getInstance().getReference().child("Following")
                .child(user.getUid()).child(id).removeValue();
        userList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,userList.size());
    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserListViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView profileImg;
        private TextView nameTxt,unfollow;

        public UserListViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.user_img);
            nameTxt = itemView.findViewById(R.id.user_name);
            unfollow = itemView.findViewById(R.id.unfollowingBtn);
        }
    }
}
