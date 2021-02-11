package com.example.travelhut.views.main.newsfeed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.travelhut.R;
import com.example.travelhut.model.UniversalImageLoader;
import com.example.travelhut.model.User;
import com.example.travelhut.utils.BottomNavigationViewHelper;
import com.example.travelhut.viewmodel.main.newsfeed.NewsFeedActivityViewModel;
import com.example.travelhut.views.main.newsfeed.toolbar.user_search.UserSearchAdapter;
import com.google.firebase.database.DataSnapshot;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class NewsFeedActivity extends AppCompatActivity implements LifecycleOwner {

    private final static String TAG = "NewsFeedActivity";
    private Context mContext = NewsFeedActivity.this;
    private static final int ACTIVITY_NUM = 2;

    private RecyclerView recyclerView;
    private UserSearchAdapter userSearchAdapter;
    private List<User> mUsers;
    private SearchView searchView;
    private ViewFlipper viewFlipper;

    private TextView loggedInUserTextView;
    private Button logoutButton;

    private NewsFeedActivityViewModel newsFeedActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupBottomNavigationView();
        initImageLoader();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setTitle(null);
        getSupportActionBar().setTitle("");
        viewFlipper = findViewById(R.id.viewflipper);


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mUsers = new ArrayList<>();
        readUsers();
        userSearchAdapter = new UserSearchAdapter(this, mUsers);
        recyclerView.setAdapter(userSearchAdapter);
//        getLifecycle().addObserver(userSearchAdapter);

        //readUsers();


        //getSupportActionBar().setDisplayShowTitleEnabled(false);


//        ViewPager2 viewPager2 = findViewById(R.id.container);
//        viewPager2.setAdapter(new );
//
//        loggedInUserTextView = findViewById(R.id.user);
//        logoutButton = findViewById(R.id.logoutBtn);
//        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
//        mainActivityViewModel.getUserMutableLiveData().observe(this, firebaseUser -> {
//            if(firebaseUser != null){
//                loggedInUserTextView.setText("Logged in user: " + firebaseUser.getEmail());
//            }
//        });
//
//        logoutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mainActivityViewModel.logout();
//            }
//        });
//
//        mainActivityViewModel.getLoggedOutMutableLiveData().observe(this, new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean loggedOut) {
//                if(loggedOut){
//                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
//                }
//            }
//        });

    }

    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_feed_action_bar_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_icon);
        MenuItem menuItemCamera = menu.findItem(R.id.camera_icon);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search Users");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchUsers(newText.toLowerCase());
                return true;
            }
        });


        menuItemCamera.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return false;
            }
        });
        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {


            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                readUsers();
                viewFlipper.showNext();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                searchView.setQuery("", true);
                viewFlipper.showPrevious();
                userSearchAdapter.notifyDataSetChanged();
                return true;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    private void searchUsers(String s){

        Log.v("Your Filter", "CLICKED ROW CLICKED ROW CLICKED ROW CLICKED ROW CLICKED ROW CLICKED ROW CLICKED ROW CLICKED ROW CLICKED ROW " + s);
        newsFeedActivityViewModel = new NewsFeedActivityViewModel(s);
        LiveData<DataSnapshot> liveData = newsFeedActivityViewModel.getDataSnapshotLiveData();

        liveData.observe(this, dataSnapshot -> {
            mUsers.clear();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                User user = snapshot.getValue(User.class);
                mUsers.add(user);
            }

            userSearchAdapter.notifyDataSetChanged();
        });

    }

    private void readUsers() {
        //recyclerView.setAlpha(0);
        //recyclerView.setClickable(false);
        //DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        newsFeedActivityViewModel = new NewsFeedActivityViewModel();
        LiveData<DataSnapshot> liveData = newsFeedActivityViewModel.getDataSnapshotLiveData();

        liveData.observe(this, dataSnapshot -> {
            if ((searchView != null) && (searchView.getQuery().toString().equals(""))) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    mUsers.add(user);
                }

                userSearchAdapter.notifyDataSetChanged();
            }
        });
    }


    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }
}