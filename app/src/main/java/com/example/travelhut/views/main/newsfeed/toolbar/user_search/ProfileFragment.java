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
import com.example.travelhut.model.utils.StringsRepository;
import com.example.travelhut.viewmodel.main.newsfeed.toolbar.users.ProfileFragmentViewModel;
import com.example.travelhut.viewmodel.main.newsfeed.toolbar.users.ProfileFragmentViewModelFactory;
import com.example.travelhut.model.objects.User;
import com.example.travelhut.views.main.newsfeed.NewsFeedFragment;
import com.example.travelhut.views.main.newsfeed.newsfeed.utils.PostsAdapter;
import com.example.travelhut.model.objects.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment {


    //Instance Variables
    private ImageView profileImage, backArrow;
    private TextView followers, following, displayName, bio, url, username;
    private Button followButton;
    private FirebaseUser firebaseUser;
    private String profileid;
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

        //Get profileId from SharedPreferences
        SharedPreferences prefs = getContext().getSharedPreferences(StringsRepository.PREFS, Context.MODE_PRIVATE);
        profileid = prefs.getString(StringsRepository.PROFILE_ID, "none");

        profileFragmentViewModel = ViewModelProviders.of(this, new ProfileFragmentViewModelFactory(getActivity().getApplication(), profileid)).get(ProfileFragmentViewModel.class);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        initViews(view);
        configViewsAndLayout();
        userInfo();
        getFollowers();
        getProfileFeed();

        if (profileid.equals(firebaseUser.getUid())) {
            followButton.setText("edit profile");
        } else {
            checkFollow();
        }

        //Set OnClickListener for followButton
        followButton.setOnClickListener(v -> {
            //If current user does not follow current profile
            if (followButton.getText().toString().equals(StringsRepository.FOLLOW)) {
                //Follow user and add notification
                profileFragmentViewModel.follow(profileid);
                addNotification();
            } else if (followButton.getText().toString().equals(StringsRepository.FOLLOWING)) {
                //Unfollow user
                profileFragmentViewModel.unFollow(profileid);
            }
        });

        //Set OnClickListener for backArrow
        backArrow.setOnClickListener((View.OnClickListener) v -> {

            //Initialize newsFeedFragment and replace frame layout with newsFeedFragment
            newsFeedFragment = new NewsFeedFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, (Fragment) newsFeedFragment).commit();
        });

        return view;
    }

    //This method initializes views
    private void initViews(View view) {
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
    }

    //This method configures views
    private void configViewsAndLayout() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        postsAdapter = new PostsAdapter(getContext(), postList);
        recyclerView.setAdapter(postsAdapter);
    }

    //This method calls ViewModel to send a notification
    private void addNotification() {
        profileFragmentViewModel.followNotification(profileid);
    }


    //This method calls ViewModel to get selected profile's user info
    private void userInfo() {

        //LiveData object from ViewModel
        LiveData<DataSnapshot> liveData = profileFragmentViewModel.getDataSnapshotLiveData();

        //Observe liveData object
        liveData.observe(getViewLifecycleOwner(), dataSnapshot -> {

            if (dataSnapshot != null) {
                //Get User object from dataSnapshot
                User user = dataSnapshot.getValue(User.class);
                Log.d(TAG, "userInfo: imageuri: " + user.getImageurl());

                //Update views with data
                Glide.with(getContext()).load(user.getImageurl()).into(profileImage);
                displayName.setText(user.getDisplayname());
                username.setText(user.getUsername());
                bio.setText(user.getBio());
                url.setText(user.getUrl());

            }
        });
    }


    //This method checks if current user is following selected profile and sets followButton text accordingly
    private void checkFollow() {

        //Observe isFollowing boolean from ViewModel
        profileFragmentViewModel.getIsFollowing().observe(getViewLifecycleOwner(), isFollowing -> {
            if (isFollowing) {
                followButton.setText(StringsRepository.FOLLOWING);
            } else {
                followButton.setText(StringsRepository.FOLLOW);
            }
        });
    }


    //This method gets the number of followers for selected profile
    private void getFollowers() {

        //Get LiveData object from ViewModel
        LiveData<DataSnapshot> numOfFollowers = profileFragmentViewModel.getFollowersSnapshot();

        //Observe LiveData object
        numOfFollowers.observe(getViewLifecycleOwner(), dataSnapshot -> {

            //if followers > 0 -> setText of view to display number of followers
            if (dataSnapshot != null) {
                followers.setText("" + dataSnapshot.getChildrenCount());
            }
        });

        //Get LiveData object from ViewModel
        LiveData<DataSnapshot> numOfFollowing = profileFragmentViewModel.getFollowingSnapshot();

        //Observe LiveData object
        numOfFollowing.observe(getViewLifecycleOwner(), dataSnapshot -> {

            //if following > 0 -> setText of view to display number of followers
            if (dataSnapshot != null) {
                following.setText("" + dataSnapshot.getChildrenCount());
            }
        });
    }

    //This method gets the posts of the selected user profile
    private void getProfileFeed() {

        //Get LiveData object from ViewModel
        LiveData<DataSnapshot> liveData = profileFragmentViewModel.getPostsLiveData();

        //Observe LiveData object
        liveData.observe(getViewLifecycleOwner(), dataSnapshot -> {
            postList.clear();
            //Iterate through posts
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                //Get Post object
                Post post = snapshot.getValue(Post.class);

                //If post belongs to selected user -> add to list
                if (post.getPublisher().equals(profileid)) {
                    postList.add(post);
                }
            }

            //notify postsAdapter of updated data set
            postsAdapter.notifyDataSetChanged();
        });
    }
}