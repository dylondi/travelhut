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
import com.example.travelhut.model.StringsRepository;
import com.example.travelhut.views.authentication.utils.User;
import com.example.travelhut.viewmodel.main.newsfeed.toolbar.users.UserSearchAdapterViewModel;
import com.example.travelhut.views.ProfileFragment;
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

        //gets current user in recyclerView
        final User user = mUsers.get(position);

        //initialize userSearchAdapterViewModel
        userSearchAdapterViewModel = new UserSearchAdapterViewModel(user.getId());

        //current firebase user
        //firebaseUser = userSearchAdapterViewModel.getUserMutableLiveData().getValue();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        setUserItem(holder, user);
        isFollowing(user.getId(),holder.btn_follow);

        //if this user is current user then remove option to follow/unfollow
        if(user.getId().equals(firebaseUser.getUid())){
            holder.btn_follow.setVisibility(View.GONE);
        }

        //OnClickListener for current user in search
        holder.itemView.setOnClickListener(v -> {
            Log.i(TAG, "itemView clicked in RecyclerView");

            //go to user profile
            showProfileFragment(user);
        });

        //OnClickListener for follow button in search
        holder.btn_follow.setOnClickListener(v -> {
            Log.i(TAG, "itemView clicked in RecyclerView");

            //follow or unfollow user
            followOrUnfollowUser(holder, user);
        });

    }

    //add notification ****NOT FINISHED****
    private void addNotification(String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "started following you");
        hashMap.put("postid", "");
        hashMap.put("ispost", false);

        reference.push().setValue(hashMap);
    }


    //follow or unfollow user based on if already following or not
    private void followOrUnfollowUser(@NonNull ViewHolder holder, User user) {

        //checks if already following
        if(holder.btn_follow.getText().toString().equals(StringsRepository.FOLLOW)){
            userSearchAdapterViewModel.follow(user.getId());
            addNotification(user.getId());
            Log.d("logging","follow " + user.getId());
        } else {
            userSearchAdapterViewModel.unFollow(user.getId());
            Log.d("logging","unfollow" + user.getId());
        }
    }

    //go to user profile
    private void showProfileFragment(User user) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
        editor.putString("profileid", user.getId());
        editor.apply();
        ProfileFragment profileFragment = new ProfileFragment();
        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, profileFragment).commit();
    }

    //this method sets the data for the current item in recyclerView
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


    //this method checks if you are following each user
    private void isFollowing(final String userid, final Button button) {



//        LiveData<DataSnapshot> liveData = userSearchAdapterViewModel.getFollowing();

//
//        liveData.observeForever(new Observer<DataSnapshot>() {
//            @Override
//            public void onChanged(DataSnapshot dataSnapshot) {
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
//        });
//
          DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                  .child(StringsRepository.FOLLOW_CAP).child(firebaseUser.getUid()).child(StringsRepository.FOLLOWING);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(userid).exists()) {

                        Log.d("logging", "text currently reads: " + button.getText());
                        button.setText(StringsRepository.FOLLOWING);
                        Log.d("logging", "change text to following : " + userid);
                    } else {
                        Log.d("logging", "text currently reads: " + button.getText());
                        button.setText(StringsRepository.FOLLOW);
                        Log.d("logging", "change text to follow : " + userid);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

    }

}
