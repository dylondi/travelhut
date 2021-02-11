package com.example.travelhut.views.main.newsfeed.toolbar.user_search;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travelhut.R;
import com.example.travelhut.model.User;
import com.example.travelhut.viewmodel.main.newsfeed.toolbar.users.UserSearchAdapterViewModel;
import com.example.travelhut.views.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.ViewHolder>{

    private final static String TAG = "UserSearchAdapter";

    private Context mContext;
    private List<User> mUsers;
    private FirebaseUser firebaseUser;
    private UserSearchAdapterViewModel userSearchAdapterViewModel;


    public UserSearchAdapter(Context mContext, List<User> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }


    @NonNull
    @Override
    public UserSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder called" );
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.user_item, parent, false);
        //recyclerView = view.findViewById(R.id.recycler_view);
        return new ViewHolder(view);
    }


    @Override
    public int getItemViewType(int position) {
        return R.layout.user_item;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Log.i(TAG, "onBindViewHolder called, position: " + position);
        final User user = mUsers.get(position);

        userSearchAdapterViewModel = new UserSearchAdapterViewModel(user.getId());
        //firebaseUser = userSearchAdapterViewModel.getUserMutableLiveData().getValue();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        setUserItem(holder, user);
        isFollowing(user.getId(),holder.btn_follow);

        if(user.getId().equals(firebaseUser.getUid())){
            holder.btn_follow.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Log.i(TAG, "itemView clicked in RecyclerView");
            showProfileFragment(user);
        });

        holder.btn_follow.setOnClickListener(v -> {
            Log.i(TAG, "itemView clicked in RecyclerView");
            followOrUnfollowUser(holder, user);
        });

    }

    private void followOrUnfollowUser(@NonNull ViewHolder holder, User user) {
        if(holder.btn_follow.getText().toString().equals("follow")){
            userSearchAdapterViewModel.follow(user.getId());
            Log.d("logging","follow " + user.getId());
        } else {
            userSearchAdapterViewModel.unfollow(user.getId());
            Log.d("logging","unfollow" + user.getId());
        }
    }

    private void showProfileFragment(User user) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
        editor.putString("profileid", user.getId());
        editor.apply();
        ProfileFragment profileFragment = new ProfileFragment();
        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, profileFragment).commit();
    }

    private void setUserItem(@NonNull ViewHolder holder, User user) {
        holder.btn_follow.setVisibility(View.VISIBLE);
        holder.username.setText(user.getUsername());
        holder.email.setText(user.getEmail());
        Glide.with(mContext).load(user.getImageurl()).into(holder.image_profile);
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



    private void isFollowing(final String userid, final Button button) {

        //final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //DatabaseReference reference = userSearchAdapterViewModel.getReferenceMutableLiveData().getValue();


//        if(userSearchAdapterViewModel.getIsFollowing().getValue()){
//            button.setText("follow");
//        }else{
//            button.setText("unfollow");
//        }


//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.child(userid).exists()) {
//
//                    Log.d("logging", "text currently reads: " + button.getText());
//                    button.setText("following");
//                    Log.d("logging", "change text to following : " + userid);
//                } else {
//                    Log.d("logging", "text currently reads: " + button.getText());
//                    button.setText("follow");
//                    Log.d("logging", "change text to follow : " + userid);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
//     private void isFollowing(final String userid, final Button button){

        //final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

       // DatabaseReference reference = userSearchAdapterViewModel.getReferenceMutableLiveData().getValue();
          DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                  .child("Follow").child(firebaseUser.getUid()).child("following");

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(userid).exists()) {

                        Log.d("logging", "text currently reads: " + button.getText());
                        button.setText("following");
                        Log.d("logging", "change text to following : " + userid);
                    } else {
                        Log.d("logging", "text currently reads: " + button.getText());
                        button.setText("follow");
                        Log.d("logging", "change text to follow : " + userid);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

    }

}
