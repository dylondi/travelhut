package com.example.travelhut.views.main.newsfeed;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelhut.R;
import com.example.travelhut.viewmodel.main.newsfeed.NewsFeedActivityViewModel;
import com.example.travelhut.views.main.newsfeed.toolbar.user_search.ProfileFragment;
import com.example.travelhut.views.authentication.utils.User;
import com.example.travelhut.views.main.newsfeed.newsfeed.AddStoryActivity;
import com.example.travelhut.views.main.newsfeed.newsfeed.PostsAdapter;
import com.example.travelhut.views.main.newsfeed.newsfeed.StoryAdapter;
import com.example.travelhut.views.main.newsfeed.newsfeed.utils.Post;
import com.example.travelhut.views.main.newsfeed.newsfeed.utils.Story;
import com.example.travelhut.views.main.newsfeed.toolbar.user_search.UserSearchAdapter;
import com.google.firebase.auth.FirebaseAuth;
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
    private PostsAdapter postsAdapter;
    private List<Post> postList;
    private NewsFeedActivityViewModel newsFeedActivityViewModel;
    private List<String> followingList;
    private static final String TAG = "NewsFeedFragment";
    private SearchView searchView;
    ProgressBar progressBar;

    private RecyclerView storyRecyclerView;
    private StoryAdapter storyAdapter;
    private List<Story> storyList;
    private LinearLayout linearLayout;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news_feed, container, false);
        setHasOptionsMenu(true);

        Toolbar toolbar = view.findViewById(R.id.toolbar);

        //get toolbar and set title to empty string
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");

        storyList = new ArrayList<>();

        storyAdapter = new StoryAdapter(getContext(), storyList);
        //assign views
        viewFlipper = view.findViewById(R.id.viewflipper);
        recyclerView = view.findViewById(R.id.news_feed_recycler_view);
        storyRecyclerView = view.findViewById(R.id.story_recycler_view);
        linearLayout = view.findViewById(R.id.news_feed_lin_layout);


        progressBar = view.findViewById(R.id.newsfeed_progress_bar);
        newsFeedActivityViewModel = new NewsFeedActivityViewModel();

        //create linearLayoutManager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        //fix recyclerView size and setLayoutManager to linearLayoutManager already initialized
        recyclerView.setHasFixedSize(true);
        storyRecyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        storyRecyclerView.setLayoutManager(linearLayoutManager2);

        storyRecyclerView.setAdapter(storyAdapter);


        postList = new ArrayList<>();
        postsAdapter = new PostsAdapter(getContext(), postList);
        postsAdapter.setHasStableIds(true);

        //set adapter for recyclerView
        recyclerView.setAdapter(postsAdapter);


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

        //assigning recyclerView Search
        recyclerViewSearch = view.findViewById(R.id.recycler_view);
        recyclerViewSearch.setHasFixedSize(true);
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        mUsers = new ArrayList<>();
        readUsers();


        userSearchAdapter = new UserSearchAdapter(getContext(), mUsers);
        recyclerViewSearch.setAdapter(userSearchAdapter);

//        recyclerView.setNestedScrollingEnabled(false);
//        storyRecyclerView.setNestedScrollingEnabled(false);
//        linearLayout.setNestedScrollingEnabled(false);
//        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
//        ViewCompat.setNestedScrollingEnabled(storyRecyclerView, false);
        RecyclerView.RecycledViewPool pool= new RecyclerView.RecycledViewPool();

        pool.setMaxRecycledViews(0, 10);
        recyclerView.setRecycledViewPool(pool);
        ViewCompat.setNestedScrollingEnabled(linearLayout, false);
        return view;

    }


    //create options menu in toolbar
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


        //setOnActionExpandListener for user search item
        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {


            //if search is opened
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                readUsers();
                //show next view in viewflipper
                viewFlipper.showNext();
                return true;
            }

            //if search is closed
            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                searchView.setQuery("", true);
                //show previous view in viewflipper
                viewFlipper.showPrevious();
                //notify userSearchAdapter of data changes
                userSearchAdapter.notifyDataSetChanged();
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);

        //return super.onCreateOptionsMenu(menu);
    }

    //if search is selected
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

            case R.id.camera_icon:
                 Intent intent = new Intent(getContext(), AddStoryActivity.class);
                 getContext().startActivity(intent);
                 return true;
        }

        return false;
    }


    //search for user with string s
    void searchUsers(String s) {

        //get LiveData object from viewmodel for search result
        newsFeedActivityViewModel = new NewsFeedActivityViewModel(s);
        LiveData<DataSnapshot> liveData = newsFeedActivityViewModel.getDataSnapshotLiveData();


        //observe the LiveData object which returns a DataSnapshot object
        liveData.observe(this, dataSnapshot -> {
            mUsers.clear();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                User user = snapshot.getValue(User.class);

                //add each user found in search to list of found users
                mUsers.add(user);
            }

            userSearchAdapter.notifyDataSetChanged();
        });

    }

    //this method gets all possible user in db
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
                progressBar.setVisibility(View.GONE);
            }
        });
    }




    //checks if your following each user from ViewModel
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
            readStory();
            newsFeedActivityViewModel.getUserMutableLiveData().observe(getViewLifecycleOwner(), firebaseUser -> {
                followingList.add(firebaseUser.getUid());
            });

            readPosts();

        });
    }


    //retrieves posts from ViewModel
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
            postsAdapter.notifyDataSetChanged();
        });
    }


    private void readStory() {

        LiveData<DataSnapshot> liveData = newsFeedActivityViewModel.getStoriesLiveData();

        liveData.observe(getViewLifecycleOwner(), dataSnapshot -> {
            long timecurrent = System.currentTimeMillis();
            storyList.clear();
            storyList.add(new Story("", 0, 0, "",
                    FirebaseAuth.getInstance().getCurrentUser().getUid()));


            for (String id : followingList) {
                int countStory = 0;
                Story story = null;
                for (DataSnapshot snapshot : dataSnapshot.child(id).getChildren()) {

                    //String myId = newsFeedActivityViewModel.getUserMutableLiveData().getValue().getUid();

                    if (id != FirebaseAuth.getInstance().getCurrentUser().getUid()) {
                        story = snapshot.getValue(Story.class);
                        if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()) {
                            countStory++;
                        }
                    }
                }
                if (countStory > 0) {
                    storyList.add(story);
                }
            }

            storyAdapter.notifyDataSetChanged();
        });

    }

//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                long timecurrent = System.currentTimeMillis();
//                storyList.clear();
//                storyList.add(new Story("", 0, 0, "",
//                        FirebaseAuth.getInstance().getCurrentUser().getUid()));
//
//
//
//                for (String id : followingList) {
//                    int countStory = 0;
//                    Story story = null;
//                    for (DataSnapshot snapshot : dataSnapshot.child(id).getChildren()) {
//
//                        //String myId = newsFeedActivityViewModel.getUserMutableLiveData().getValue().getUid();
//
//                        if(id!=FirebaseAuth.getInstance().getCurrentUser().getUid()) {
//                            story = snapshot.getValue(Story.class);
//                            if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()) {
//                                countStory++;
//                            }
//                        }
//                    }
//                    if (countStory > 0){
//                        storyList.add(story);
//                    }
//                }
//
//                storyAdapter.notifyDataSetChanged();
 //           }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//});
//    }

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