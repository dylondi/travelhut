package com.example.travelhut.views.main.newsfeed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travelhut.R;
import com.example.travelhut.model.User;
import com.example.travelhut.utils.BottomNavigationViewHelper;
import com.example.travelhut.viewmodel.newsfeed.NewsFeedActivityViewModel;
import com.example.travelhut.viewmodel.newsfeed.NewsFeedActivityViewModelFactory;
import com.example.travelhut.views.ProfileFragment;
import com.example.travelhut.views.main.newsfeed.toolbar.user_search.UserSearchAdapter;
import com.example.travelhut.views.main.profile.ProfileActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.List;

public class NewsFeedActivity extends AppCompatActivity implements UserSearchAdapter.OnItemClickedListener{

    private final static String TAG = "NewsFeedActivity";
    private Context mContext = NewsFeedActivity.this;
    private static final int ACTIVITY_NUM = 2;

    private RecyclerView recyclerView;
    private UserSearchAdapter userSearchAdapter;
    private List<User> mUsers;
    private SearchView searchView;

    private TextView loggedInUserTextView;
    private Button logoutButton;

    private NewsFeedActivityViewModel newsFeedActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupBottomNavigationView();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setTitle(null);
        getSupportActionBar().setTitle("");


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mUsers = new ArrayList<>();
        readUsers();
        userSearchAdapter = new UserSearchAdapter(this, mUsers, this);
        recyclerView.setAdapter(userSearchAdapter);

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

    private void setupViewPager(){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(this);
        adapter.addFragment(new CameraFragment());
        adapter.addFragment(new SearchFragment());
        adapter.addFragment(new MessagesFragment());
        ViewPager2 viewPager2 = findViewById(R.id.container);
        viewPager2.setAdapter(adapter);

        //TabLayout tabLayout = findViewById(R.id.tabs);
        //tabLayout.setUpw
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

        searchView.setOnCloseListener(() -> {
            recyclerView.setAlpha(0);
            return true;
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
                Toast.makeText(NewsFeedActivity.this, "onMenuItemActionExpand called", Toast.LENGTH_SHORT).show();
                readUsers();
                recyclerView.setAlpha(1);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                //Toast.makeText(NewsFeedActivity.this, "onMenutItemActionCollapse called", Toast.LENGTH_SHORT).show();
                recyclerView.setAlpha(0);
                //searchView.setQuery("", true);

                userSearchAdapter.notifyDataSetChanged();
                return true;
            }


//            @Override
//            public boolean onMenuItemActionExpand(MenuItem item) {
//                Toast.makeText(NewsFeedActivity.this, "onMenuItemActionExpand called", Toast.LENGTH_SHORT).show();
//                readUsers();
//                recyclerView.setAlpha(1);
//                return true;
//            }
//
//            @Override
//            public boolean onMenuItemActionCollapse(MenuItem item) {
//                //Toast.makeText(NewsFeedActivity.this, "onMenutItemActionCollapse called", Toast.LENGTH_SHORT).show();
//                recyclerView.setAlpha(0);
//               searchView.setQuery("", true);
//
//                userSearchAdapter.notifyDataSetChanged();
//                return true;
//            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    private void searchUsers(String s){
        //recyclerView.setAlpha(1);
        //newsFeedActivityViewModel = new NewsFeedActivityViewModel(s);
        //newsFeedActivityViewModel = ViewModelProvider(this, new NewsFeedActivityViewModelFactory(this.getApplication(), "my awesome param")).get(MyViewModel.class);

        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    mUsers.add(user);
                }

                userSearchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void readUsers(){
        recyclerView.setAlpha(0);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((searchView!=null) && (searchView.getQuery().toString().equals(""))){
                    mUsers.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        User user = snapshot.getValue(User.class);
                        mUsers.add(user);
                    }

                    userSearchAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
  }

    @Override
    public void onItemClick(int position) {

        Log.v("Your Filter", "HOWYE HOWYE HOWYE HOWYE HOWYE HOWYE HOWYE HOWYE HOWYE HOWYE HOWYE HOWYE HOWYE HOWYE ");

        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
        editor.putString("profileid", mUsers.get(position).getId());
        editor.apply();

        //mContext.startActivity(new Intent(mContext, ProfileFragment.class));

        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.relLayout2, new ProfileFragment()).commit();
//        Intent intent = new Intent(this, ProfileActivity.class);
//        startActivity(intent);
    }
}