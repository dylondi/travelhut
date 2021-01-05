package com.example.travelhut.views.main.newsfeed.toolbar.user_search;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travelhut.R;
import com.example.travelhut.model.User;
import com.example.travelhut.views.ProfileFragment;
import com.example.travelhut.views.main.profile.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private FirebaseUser firebaseUser;
    private RecyclerView recyclerView;

    public UserSearchAdapter(Context mContext, List<User> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }


    @NonNull
    @Override
    public UserSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.v("Your Filter", "CLICKED ROW CLICKED ROW CLICKED ROW CLICKED ROW CLICKED ROW CLICKED ROW CLICKED ROW CLICKED ROW CLICKED ROW " );
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.user_item, parent, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        return new ViewHolder(view);
    }


    @Override
    public int getItemViewType(int position) {
        return R.layout.user_item;
    }

    //CHANGE TO MVVM
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Log.v("Your Filter", "CLICKED ROW " + position);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = mUsers.get(position);
        View view = holder.itemView;
        //Button followBtn = (AppCompatButton)holder.btn_follow;
        Button followBtn = (Button) holder.btn_follow;


        holder.btn_follow.setVisibility(View.VISIBLE);

        holder.username.setText(user.getUsername());
        holder.email.setText(user.getEmail());
        Glide.with(mContext).load(user.getImageurl()).into(holder.image_profile);
        isFollowing(user.getId(), holder.btn_follow);

        if(user.getId().equals(firebaseUser.getUid())){
            holder.btn_follow.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("Your Filter", "HOWYE HOWYE HOWYE HOWYE HOWYE HOWYE HOWYE HOWYE HOWYE HOWYE HOWYE HOWYE HOWYE HOWYE ");
                Log.d("logging","We are pm thois method");
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", user.getId());
                editor.apply();

                //mContext.startActivity(new Intent(mContext, ProfileFragment.class));

                recyclerView.setAlpha(0);
                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.relLayout2, new ProfileFragment()).commit();
            }
        });

        followBtn.setOnClickListener(v -> {
            if(holder.btn_follow.getText().toString().equals("follow")){
                FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                        .child("following").child(user.getId()).setValue(true);
                FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                        .child("followers").child(firebaseUser.getUid()).setValue(true);
            } else {
                FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                        .child("following").child(user.getId()).removeValue();
                FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                        .child("followers").child(firebaseUser.getUid()).removeValue();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public TextView email;
        public CircleImageView image_profile;
        public Button btn_follow;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            email = itemView.findViewById(R.id.email);
            image_profile = itemView.findViewById(R.id.image_profile);
            btn_follow = itemView.findViewById(R.id.btn_follow);
        }
    }

    private void isFollowing(String userid, Button button){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(userid).exists()){
                    button.setText("following");
                }else{
                    button.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
