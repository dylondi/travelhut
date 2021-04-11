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
import com.example.travelhut.model.utils.StringsRepository;
import com.example.travelhut.model.objects.User;
import com.example.travelhut.viewmodel.main.newsfeed.toolbar.users.UserSearchAdapterViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.ViewHolder> {


    //Instance Variables
    private final static String TAG = "UserSearchAdapter";
    private Context mContext;
    private List<User> mUsers;
    private FirebaseUser firebaseUser;
    private UserSearchAdapterViewModel userSearchAdapterViewModel;

    //Constructor
    public UserSearchAdapter(Context mContext, List<User> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }


    @NonNull
    @Override
    public UserSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder called");
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.user_view, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public int getItemViewType(int position) {
        return R.layout.user_view;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Log.i(TAG, "onBindViewHolder called, position: " + position);

        //Gets current user in recyclerView
        User user = mUsers.get(position);

        //Initialize userSearchAdapterViewModel
        userSearchAdapterViewModel = new UserSearchAdapterViewModel(user.getId());

        //Current firebase user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        setUserItem(holder, user);
        isFollowing(user.getId(), holder.followButtonSearch);

        //If this user is current user then remove option to follow/unfollow
        if (user.getId().equals(firebaseUser.getUid())) {
            holder.followButtonSearch.setVisibility(View.GONE);
        }

        //OnClickListener for current user in search
        holder.itemView.setOnClickListener(v -> {
            Log.i(TAG, "itemView clicked in RecyclerView");

            //Go to user profile
            showProfileFragment(user);
        });

        //OnClickListener for follow button in search
        holder.followButtonSearch.setOnClickListener(v -> {
            Log.i(TAG, "itemView clicked in RecyclerView");

            //Follow or unfollow user
            followOrUnfollowUser(holder, user);
        });

    }

    //Add notification
    private void addNotification(String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(StringsRepository.NOTIFICATIONS_CAP).child(userid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(StringsRepository.USER_ID, firebaseUser.getUid());
        hashMap.put(StringsRepository.TEXT, StringsRepository.STARTED_FOLLOWING_YOU);
        hashMap.put(StringsRepository.POST_ID, "");
        hashMap.put(StringsRepository.IS_POST, false);

        reference.push().setValue(hashMap);
    }


    //Follow or unfollow user based on if already following or not
    private void followOrUnfollowUser(@NonNull ViewHolder holder, User user) {

        //Checks if already following
        if (holder.followButtonSearch.getText().toString().equals(StringsRepository.FOLLOW)) {
            userSearchAdapterViewModel.follow(user.getId());
            addNotification(user.getId());
            Log.d("logging", "follow " + user.getId());
        } else {
            userSearchAdapterViewModel.unFollow(user.getId());
            Log.d("logging", "unfollow" + user.getId());
        }
    }

    //Go to user profile
    private void showProfileFragment(User user) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(StringsRepository.PREFS, Context.MODE_PRIVATE).edit();
        editor.putString(StringsRepository.PROFILE_ID, user.getId());
        editor.apply();
        ProfileFragment profileFragment = new ProfileFragment();
        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, profileFragment).commit();
    }

    //This method sets the data for the current item in recyclerView
    private void setUserItem(@NonNull ViewHolder holder, User user) {
        holder.followButtonSearch.setVisibility(View.VISIBLE);
        holder.usernameSearch.setText(user.getUsername());
        holder.emailSearch.setText(user.getEmail());
        Glide.with(mContext).load(user.getImageurl()).into(holder.profileImageSearch);
    }


    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        //Instance Variables
        public TextView usernameSearch;
        public TextView emailSearch;
        public CircleImageView profileImageSearch;
        public Button followButtonSearch;

        //Constructor
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            usernameSearch = itemView.findViewById(R.id.username);
            emailSearch = itemView.findViewById(R.id.email);
            profileImageSearch = itemView.findViewById(R.id.image_profile);
            followButtonSearch = itemView.findViewById(R.id.btn_follow);
        }
    }


    //This method checks if you are following each user
    private void isFollowing(final String userId, final Button button) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(StringsRepository.FOLLOW_CAP).child(firebaseUser.getUid()).child(StringsRepository.FOLLOWING);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userId).exists()) {

                    Log.d("logging", "text currently reads: " + button.getText());
                    button.setText(StringsRepository.FOLLOWING);
                    Log.d("logging", "change text to following : " + userId);
                } else {
                    Log.d("logging", "text currently reads: " + button.getText());
                    button.setText(StringsRepository.FOLLOW);
                    Log.d("logging", "change text to follow : " + userId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
