package com.example.travelhut.views.main.newsfeed.toolbar.user_search;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.travelhut.R;
import com.example.travelhut.viewmodel.main.newsfeed.toolbar.users.ProfileFragmentViewModel;
import com.example.travelhut.viewmodel.main.newsfeed.toolbar.users.ProfileFragmentViewModelFactory;
import com.example.travelhut.views.authentication.utils.User;
import com.example.travelhut.views.main.newsfeed.NewsFeedFragment;
import com.example.travelhut.views.main.newsfeed.newsfeed.PostsAdapter;
import com.example.travelhut.views.main.newsfeed.newsfeed.utils.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment {

    ImageView profileImage, backArrow;
    TextView followers, following, displayName, bio, url, username;
    Button followButton;

    FirebaseUser firebaseUser;
    String profileid;

    private PostsAdapter postsAdapter;
    private List<Post> postList;
    public RecyclerView recyclerView;
    private NewsFeedFragment newsFeedFragment;
    private ProfileFragmentViewModel profileFragmentViewModel;
    private static final String TAG = "ProfileFragment";




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");
        profileFragmentViewModel = ViewModelProviders.of(this, new ProfileFragmentViewModelFactory(getActivity().getApplication(), profileid)).get(ProfileFragmentViewModel.class);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();



        profileImage = view.findViewById(R.id.profile_image_fragment);
        followers = view.findViewById(R.id.num_followers_profile_fragment);
        following = view.findViewById(R.id.num_following_profile_fragment);
        displayName = view.findViewById(R.id.display_name_profile_fragment);
        bio = view.findViewById(R.id.bio_profile_fragment);
        url = view.findViewById(R.id.url_profile_fragment);
        followButton = view.findViewById(R.id.follow_button_profile_fragment);
        recyclerView = view.findViewById(R.id.profile_fragment_recycler_view);
        username = view.findViewById(R.id.profile_fragment_name);
        backArrow = view.findViewById(R.id.profile_fragment_back_arrow);
//        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        postsAdapter = new PostsAdapter(getContext(), postList);
        recyclerView.setAdapter(postsAdapter);

        userInfo();
        getFollowers();

        getProfileFeed();
        if(profileid.equals(firebaseUser.getUid())){
            followButton.setText("edit profile");
        }else{
            checkFollow();
        }

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(followButton.getText().toString().equals("follow")){
                    profileFragmentViewModel.follow(profileid);

                    addNotification();
                }else if(followButton.getText().toString().equals("following")){
                    profileFragmentViewModel.unFollow(profileid);
                }
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newsFeedFragment = new NewsFeedFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, (Fragment) newsFeedFragment).commit();
            }
        });


        return view;
    }


    private void addNotification(){
        profileFragmentViewModel.followNotification(profileid);
    }


    private void userInfo(){
        LiveData<DataSnapshot> liveData = profileFragmentViewModel.getDataSnapshotLiveData();

        liveData.observe(getViewLifecycleOwner(), dataSnapshot -> {

            if(dataSnapshot!=null) {
                User user = dataSnapshot.getValue(User.class);
                Log.d(TAG, "userInfo: imageuri: " + user.getImageurl());
                Glide.with(getContext()).load(user.getImageurl()).into(profileImage);
                displayName.setText(user.getDisplayname());
                username.setText(user.getUsername());
                bio.setText(user.getBio());
                url.setText(user.getUrl());

            }
        });
    }


    private void checkFollow(){

        profileFragmentViewModel.getIsFollowing().observe(getViewLifecycleOwner() , bool -> {
            if(bool){
                followButton.setText("following");
            }else{
                followButton.setText("follow");
            }
        });
    }

    private void getFollowers(){

        LiveData<DataSnapshot> numOfFollowers = profileFragmentViewModel.getFollowersSnapshot();

        numOfFollowers.observe(getViewLifecycleOwner(), dataSnapshot -> {
            if(dataSnapshot!=null) {
                followers.setText(""+dataSnapshot.getChildrenCount());
            }
        });

        LiveData<DataSnapshot> numOfFollowing = profileFragmentViewModel.getFollowingSnapshot();

        numOfFollowing.observe(getViewLifecycleOwner(), dataSnapshot -> {
            if(dataSnapshot!=null) {
                following.setText(""+dataSnapshot.getChildrenCount());
            }
        });
    }

//    private void getNumOfPosts(){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                int i = 0;
//                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    Post post = snapshot.getValue(Post.class);
//                    if(post.getPublisher().equals(profileid)){
//                        i++;
//                    }
//                }
//
//               // posts.setText(""+i);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private void getProfileFeed(){

        LiveData<DataSnapshot> liveData = profileFragmentViewModel.getPostsLiveData();
        liveData.observe(getViewLifecycleOwner(), dataSnapshot -> {
                            postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)){
                        postList.add(post);
                    }
                }
                //Collections.reverse(postList);
                postsAdapter.notifyDataSetChanged();
        });


//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                postList.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    Post post = snapshot.getValue(Post.class);
//                    if (post.getPublisher().equals(profileid)){
//                        postList.add(post);
//                    }
//                }
//                //Collections.reverse(postList);
//                postAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }
}