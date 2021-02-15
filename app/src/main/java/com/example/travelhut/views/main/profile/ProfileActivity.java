package com.example.travelhut.views.main.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
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
import com.example.travelhut.model.UniversalImageLoader;
import com.example.travelhut.views.authentication.utils.User;
import com.example.travelhut.utils.BottomNavigationViewHelper;
import com.example.travelhut.viewmodel.main.profile.ProfileActivityViewModel;
import com.example.travelhut.views.main.newsfeed.newsfeed.PostAdapter;
import com.example.travelhut.views.main.newsfeed.newsfeed.utils.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private Context mContext = ProfileActivity.this;
    private static final int ACTIVITY_NUM = 4;
    private static final int NUM_OF_GRID_COLUMNS = 3;
    private TextView username;
    String profileid;
    private FirebaseAuth firebaseAuth;
    private ProgressBar mProgressBar;
    private ImageView profileImage;
    private TextView followers, following;
    private ProfileActivityViewModel profileActivityViewModel;
    private PostAdapter postAdapter;
    private List<Post> postList;
    public RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: started.");
        profileActivityViewModel = new ProfileActivityViewModel();
        username = findViewById(R.id.display_name);
        followers = findViewById(R.id.numFollowers);
        following = findViewById(R.id.numFollowing);
        recyclerView = findViewById(R.id.profile_activity_recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(mContext, postList);
        recyclerView.setAdapter(postAdapter);
        String s = username.getText().toString();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs = getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");

        username.setText(firebaseUser.getDisplayName());
        //Glide.with(mContext).load(firebaseUser.getPhotoUrl()).into(profileImage);


        setupBottomNavigationView();
        setupToolbar();
        setupActivityWidgets();

        userInfo();
        getFollowers();
        getProfileFeed();
//        tempGridSetup();
        //setProfileImage();
        //Glide.with(mContext).load(firebaseUser.getPhotoUrl()).into(profileImage);





//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
//        ref.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String name = dataSnapshot.child("username").getValue().toString();
//                username.setText(name);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
     }

//     private void tempGridSetup(){
//        ArrayList<String> imgURLs = new ArrayList<>();
//        imgURLs.add("https://picsum.photos/id/237/200/300");
//        imgURLs.add("https://picsum.photos/seed/picsum/200/300");
//        imgURLs.add("https://picsum.photos/200/300?grayscale");
//        imgURLs.add("https://picsum.photos/200/300/?blur");
//        imgURLs.add("https://picsum.photos/id/870/200/300?grayscale&blur=2");
//         imgURLs.add("https://picsum.photos/seed/picsum/200/300");
//         imgURLs.add("https://picsum.photos/200/300?grayscale");
//         imgURLs.add("https://picsum.photos/200/300/?blur");
//         imgURLs.add("https://picsum.photos/id/870/200/300?grayscale&blur=2");
//        imgURLs.add("https://i.picsum.photos/id/1003/1181/1772.jpg?hmac=oN9fHMXiqe9Zq2RM6XT-RVZkojgPnECWwyEF1RvvTZk");
//        imgURLs.add("https://i.picsum.photos/id/1010/5184/3456.jpg?hmac=7SE0MNAloXpJXDxio2nvoshUx9roGIJ_5pZej6qdxXs");
//        imgURLs.add("https://i.picsum.photos/id/1015/6000/4000.jpg?hmac=aHjb0fRa1t14DTIEBcoC12c5rAXOSwnVlaA5ujxPQ0I");
//        imgURLs.add("https://i.picsum.photos/id/102/4320/3240.jpg?hmac=ico2KysoswVG8E8r550V_afIWN963F6ygTVrqHeHeRc");
//        imgURLs.add("https://i.picsum.photos/id/102/4320/3240.jpg?hmac=ico2KysoswVG8E8r550V_afIWN963F6ygTVrqHeHeRc");
//        imgURLs.add("https://i.picsum.photos/id/102/4320/3240.jpg?hmac=ico2KysoswVG8E8r550V_afIWN963F6ygTVrqHeHeRc");
//        imgURLs.add("https://i.picsum.photos/id/102/4320/3240.jpg?hmac=ico2KysoswVG8E8r550V_afIWN963F6ygTVrqHeHeRc");
//
//        setupImageGridView(imgURLs);
//     }

     private void setProfileImage(){
        String imgURL = "https://www.sportsfile.com/winshare/w540/Library/SF722/523374.jpg";
         UniversalImageLoader.setImage(imgURL, profileImage, mProgressBar, "");
     }

    private void setupActivityWidgets() {
        mProgressBar = (ProgressBar) findViewById(R.id.profile_progress_bar);
        mProgressBar.setVisibility(View.GONE);
        profileImage = (ImageView) findViewById(R.id.profile_image);
    }

     private void setupToolbar(){
         Toolbar toolbar = (Toolbar) findViewById(R.id.profile_toolbar);
         setSupportActionBar(toolbar);

         ImageView overFlowMenu = findViewById(R.id.overflow_menu);
         overFlowMenu.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Log.i(TAG, "onClick called starting Intent for AccountSettingsActivity.");
                // Intent intent = new Intent(mContext, AccountSettingsActivity.class);
                 startActivity(new Intent(mContext, AccountSettingsActivity.class));
             }
         });


         ImageView addPost = findViewById(R.id.add_post);
         addPost.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Log.i(TAG, "onClick called starting Intent for CreatePostActivity.");
                // Intent intent = new Intent(mContext, AccountSettingsActivity.class);
                 startActivity(new Intent(mContext, CreatePostActivity.class));
             }
         });
     }



//     private void setupImageGridView(ArrayList<String> imgURLs){
//         GridView gridView = findViewById(R.id.grid_view_profile);
//        int gridWidth = getResources().getDisplayMetrics().widthPixels;
//        int imagewidth = gridWidth/NUM_OF_GRID_COLUMNS;
//        gridView.setColumnWidth(imagewidth);
//         GridImageAdapter adapter = new GridImageAdapter(mContext, R.layout.layout_grid_imageview, "", imgURLs);
//         gridView.setAdapter(adapter);
//     }




    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView.");
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


    private void userInfo(){
        
        LiveData<DataSnapshot> liveData = profileActivityViewModel.getDataSnapshotLiveData();

        liveData.observe(this, dataSnapshot -> {

            if(dataSnapshot!=null) {
                User user = dataSnapshot.getValue(User.class);
                Log.d(TAG, "userInfo: imageuri: " + user.getImageurl());
                Glide.with(mContext).load(user.getImageurl()).into(profileImage);
                username.setText(user.getUsername());
//                fullname.setText(user.getFullname());
//                bio.setText(user.get());
            }
        });

    }


    private void getFollowers(){
        LiveData<DataSnapshot> numOfFollowers = profileActivityViewModel.getFollowersSnapshot();

        numOfFollowers.observe(this, dataSnapshot -> {
            if(dataSnapshot!=null) {
                followers.setText(""+dataSnapshot.getChildrenCount());
            }
        });

        LiveData<DataSnapshot> numOfFollowing = profileActivityViewModel.getFollowingSnapshot();

        numOfFollowing.observe(this, dataSnapshot -> {
            if(dataSnapshot!=null) {
                following.setText(""+dataSnapshot.getChildrenCount());
            }
        });
    }

    private void getProfileFeed(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)){
                        postList.add(post);
                    }
                }
                //Collections.reverse(postList);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
