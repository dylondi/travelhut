package com.example.travelhut.views.main.newsfeed;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelhut.R;
import com.example.travelhut.viewmodel.main.newsfeed.NewsFeedActivityViewModel;
import com.example.travelhut.views.ProfileFragment;
import com.example.travelhut.views.authentication.utils.User;
import com.example.travelhut.views.main.newsfeed.newsfeed.PostAdapter;
import com.example.travelhut.views.main.newsfeed.newsfeed.utils.Post;
import com.example.travelhut.views.main.newsfeed.toolbar.user_search.UserSearchAdapter;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class NewsFeedFragment extends Fragment {

    public RecyclerView recyclerViewSearch;
    private UserSearchAdapter userSearchAdapter;
    private List<User> mUsers;
    public ViewFlipper viewFlipper;
    public RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private NewsFeedActivityViewModel newsFeedActivityViewModel;
    private List<String> followingList;
    private static final String TAG = "NewsFeedFragment";
    private SearchView searchView;


//        public Fragment newInstance() {
//            Fragment mFrgment = new NewsFeedFragment();
//            return mFrgment;
//        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news_feed, container, false);
        setHasOptionsMenu(true);

        Toolbar toolbar = view.findViewById(R.id.toolbar);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");
        viewFlipper = view.findViewById(R.id.viewflipper);
        recyclerView = view.findViewById(R.id.news_feed_recycler_view);
        recyclerView.setHasFixedSize(true);
        newsFeedActivityViewModel = new NewsFeedActivityViewModel();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);
        checkFollowing();

        Bundle intent = getActivity().getIntent().getExtras();
        if(intent != null){
            String publisher = intent.getString("publisherid");

            SharedPreferences.Editor editor = getActivity().getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profileid", publisher);
            editor.apply();

            ProfileFragment profileFragment = new ProfileFragment();
            ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, profileFragment).commit();
        }

        recyclerViewSearch = view.findViewById(R.id.recycler_view);
        recyclerViewSearch.setHasFixedSize(true);
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        mUsers = new ArrayList<>();
        readUsers();

        userSearchAdapter = new UserSearchAdapter(getContext(), mUsers);
        recyclerViewSearch.setAdapter(userSearchAdapter);


        return view;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.news_feed_action_bar_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_icon);
        MenuItem menuItemCamera = menu.findItem(R.id.camera_icon);
        searchView = (SearchView) menuItem.getActionView();

//        menuItemCamera.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
//            @Override
//            public boolean onMenuItemActionExpand(MenuItem item) {
//                return false;
//            }
//
//            @Override
//            public boolean onMenuItemActionCollapse(MenuItem item) {
//                return false;
//            }
//        });
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

        super.onCreateOptionsMenu(menu, inflater);

        //return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.search_icon:
                searchView.setQueryHint(NewsFeedStrings.SEARCH_USERS);
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
                return true;
        }

        return false;
    }


    void searchUsers(String s) {

        Log.v("Your Filter", "CLICKED ROW CLICKED ROW CLICKED ROW CLICKED ROW CLICKED ROW CLICKED ROW CLICKED ROW CLICKED ROW CLICKED ROW " + s);
        newsFeedActivityViewModel = new NewsFeedActivityViewModel(s);
        LiveData<DataSnapshot> liveData = newsFeedActivityViewModel.getDataSnapshotLiveData();

        liveData.observe(this, dataSnapshot -> {
            mUsers.clear();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                User user = snapshot.getValue(User.class);
                mUsers.add(user);
            }

            userSearchAdapter.notifyDataSetChanged();
        });

    }

    void readUsers() {
        newsFeedActivityViewModel = new NewsFeedActivityViewModel();
        LiveData<DataSnapshot> liveData = newsFeedActivityViewModel.getDataSnapshotLiveData();

        liveData.observe(getViewLifecycleOwner(), dataSnapshot -> {
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




    private void checkFollowing() {
        followingList = new ArrayList<>();

        newsFeedActivityViewModel = new NewsFeedActivityViewModel();

        LiveData<DataSnapshot> liveData = newsFeedActivityViewModel.getFollowingSnapshot();

        liveData.observe(getViewLifecycleOwner(), dataSnapshot -> {
            followingList.clear();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                followingList.add(snapshot.getKey());
            }
            //followingList.add(newsFeedActivityViewModel.getUserMutableLiveData());

            newsFeedActivityViewModel.getUserMutableLiveData().observe(getViewLifecycleOwner(), firebaseUser -> {
                followingList.add(firebaseUser.getUid());
            });

            readPosts();
        });
    }


    private void readPosts() {

        LiveData<DataSnapshot> liveData = newsFeedActivityViewModel.getPostsLiveData();

        liveData.observe(getViewLifecycleOwner(), dataSnapshot -> {
            postList.clear();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                Post post = snapshot.getValue(Post.class);
                Log.d(TAG, "onDataChange: POST IMAGE: " + post.getPostimage());
                for (String id : followingList) {
                    if (post.getPublisher().equals(id)) {
                        postList.add(post);
                    }
                }
            }
            postAdapter.notifyDataSetChanged();
        });
    }


    public void nextView(){
        viewFlipper.showNext();
    }
    public void previousView(){
        viewFlipper.showPrevious();
        userSearchAdapter.notifyDataSetChanged();

    }


    public ViewFlipper getViewFlipper() {
        return viewFlipper;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }
}