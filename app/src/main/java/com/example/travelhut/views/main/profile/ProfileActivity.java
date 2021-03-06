package com.example.travelhut.views.main.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travelhut.R;
import com.example.travelhut.model.utils.StringsRepository;
import com.example.travelhut.model.objects.User;
import com.example.travelhut.views.utils.BottomNavigationViewHelper;
import com.example.travelhut.viewmodel.main.profile.ProfileActivityViewModel;
import com.example.travelhut.views.main.newsfeed.newsfeed.utils.PostsAdapter;
import com.example.travelhut.model.objects.Post;
import com.example.travelhut.views.main.profile.toolbar.NotificationsActivity;
import com.google.firebase.database.DataSnapshot;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.example.travelhut.views.main.profile.toolbar.CreatePostActivity;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    //Instance Variables
    private static final String TAG = "ProfileActivity";
    private static final int ACTIVITY_NUM = 4;
    private Context mContext = ProfileActivity.this;
    private TextView displayName, username, bio, url, followers, following;
    private String profileid;
    private ProgressBar mProgressBar;
    private ImageView profileImage;
    private ProfileActivityViewModel profileActivityViewModel;
    private PostsAdapter postsAdapter;
    private List<Post> postList;
    public RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: started.");

        SharedPreferences prefs = getSharedPreferences(StringsRepository.PREFS, Context.MODE_PRIVATE);
        profileid = prefs.getString(StringsRepository.PROFILE_ID, "none");
        profileActivityViewModel = new ProfileActivityViewModel(profileid);

        initViews();

        //Create and config LinearLayoutManager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        //Config RecyclerView
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        postsAdapter = new PostsAdapter(mContext, postList);
        recyclerView.setAdapter(postsAdapter);

        setupBottomNavigationView();
        setupToolbar();
        setupActivityWidgets();

        userInfo();
        getFollowers();
        getProfileFeed();

    }

    //This method initializes views
    private void initViews() {
        displayName = findViewById(R.id.display_name);
        followers = findViewById(R.id.numFollowers);
        following = findViewById(R.id.numFollowing);
        username = findViewById(R.id.profileName);
        bio = findViewById(R.id.bio_profile_activity);
        url = findViewById(R.id.url_profile_activity);
        recyclerView = findViewById(R.id.profile_activity_recycler_view);
    }


    private void setupActivityWidgets() {
        mProgressBar = (ProgressBar) findViewById(R.id.profile_progress_bar);
        mProgressBar.setVisibility(View.GONE);
        profileImage = (ImageView) findViewById(R.id.profile_image);
    }


    //This method sets up the toolbar menu and sets OnClickListeners for each button in the toolbar
    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);

        ImageView overFlowMenu = findViewById(R.id.overflow_menu);
        overFlowMenu.setOnClickListener(v -> {
            Log.i(TAG, "onClick called starting Intent for AccountSettingsActivity.");
            startActivity(new Intent(mContext, AccountSettingsActivity.class));
        });


        ImageView addPost = findViewById(R.id.add_post);
        addPost.setOnClickListener(v -> {
            Log.i(TAG, "onClick called starting Intent for CreatePostActivity.");
            startActivity(new Intent(mContext, CreatePostActivity.class));
        });

        ImageView notifications = findViewById(R.id.notifications);
        notifications.setOnClickListener(v -> {
            Log.i(TAG, "onClick called starting Intent for CreatePostActivity.");
            startActivity(new Intent(mContext, NotificationsActivity.class));
        });
    }

    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView.");
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


    //This method gets user info from ViewModel
    private void userInfo() {

        LiveData<DataSnapshot> liveData = profileActivityViewModel.getDataSnapshotLiveData();

        liveData.observe(this, dataSnapshot -> {

            if (dataSnapshot != null) {
                User user = dataSnapshot.getValue(User.class);
                Log.d(TAG, "userInfo: imageuri: " + user.getImageurl());
                Glide.with(mContext).load(user.getImageurl()).into(profileImage);
                displayName.setText(user.getDisplayname());
                username.setText(user.getUsername());
                bio.setText(user.getBio());
                url.setText(user.getUrl());

            }
        });

    }


    //This method gets number of following and following from ViewModel
    private void getFollowers() {
        LiveData<DataSnapshot> numOfFollowers = profileActivityViewModel.getFollowersSnapshot();

        numOfFollowers.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                followers.setText("" + dataSnapshot.getChildrenCount());
            }
        });

        LiveData<DataSnapshot> numOfFollowing = profileActivityViewModel.getFollowingSnapshot();

        numOfFollowing.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                following.setText("" + dataSnapshot.getChildrenCount());
            }
        });
    }


    //This method gets Posts from the ViewModel
    private void getProfileFeed() {
        LiveData<DataSnapshot> liveData = profileActivityViewModel.getPostsLiveData();
        liveData.observe(this, dataSnapshot -> {
            postList.clear();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Post post = snapshot.getValue(Post.class);
                if (post.getPublisher().equals(profileid)) {
                    postList.add(post);
                }
            }
            postsAdapter.notifyDataSetChanged();
        });
    }

}
