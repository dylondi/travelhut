package com.example.travelhut.views.main.newsfeed;

import android.content.Intent;
import android.os.Bundle;
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
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelhut.R;
import com.example.travelhut.viewmodel.main.newsfeed.NewsFeedActivityViewModel;
import com.example.travelhut.model.objects.User;
import com.example.travelhut.views.main.newsfeed.newsfeed.UploadStoryActivity;
import com.example.travelhut.views.main.newsfeed.newsfeed.utils.PostsAdapter;
import com.example.travelhut.views.main.newsfeed.newsfeed.utils.StoryAdapter;
import com.example.travelhut.model.objects.Post;
import com.example.travelhut.model.objects.Story;
import com.example.travelhut.views.main.newsfeed.toolbar.user_search.UserSearchAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NewsFeedFragment extends Fragment {

    //Instance Variables
    public RecyclerView recyclerViewSearch, recyclerView, storyRecyclerView;
    private UserSearchAdapter userSearchAdapter;
    private PostsAdapter postsAdapter;
    private StoryAdapter storyAdapter;
    private List<User> mUsers;
    private List<Post> postList;
    private List<Story> storyList;
    private List<String> followingList;
    public ViewFlipper viewFlipper;
    private NewsFeedActivityViewModel newsFeedActivityViewModel;
    private SearchView searchView;
    private ProgressBar progressBarUsers, progressBarPosts;
    private LinearLayout linearLayout;
    private Toolbar toolbar;
    private View viewSplitter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_feed, container, false);
        setHasOptionsMenu(true);
        newsFeedActivityViewModel = new NewsFeedActivityViewModel();

        initViews(view);

        //Get toolbar and set title to empty string
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");

        //Create linearLayoutManager's
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        //Config linearLayoutManager
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        //Initialize lists, adapters, and set size of RecyclerViews
        initializeLists();
        initializeAdapters();
        setHasFixedSizeOfRecyclerViews();

        //SetLayoutManager to linearLayoutManager of all RecyclerViews
        setLayoutManagers(linearLayoutManager, linearLayoutManager2);
        postsAdapter.setHasStableIds(true);

        //Set adapters to RecyclerViews
        setAdapters();

        //Config recyclerView to have 10 max recycled views
        RecyclerView.RecycledViewPool pool = new RecyclerView.RecycledViewPool();
        pool.setMaxRecycledViews(0, 10);
        recyclerView.setRecycledViewPool(pool);

        ViewCompat.setNestedScrollingEnabled(linearLayout, false);

        getListOfFollowing();
        readUsers();
        return view;

    }

    //Create options menu in toolbar
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.news_feed_action_bar_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_icon);
        searchView = (SearchView) menuItem.getActionView();

        //SetOnActionExpandListener for user search item
        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {


            //If search is opened
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                readUsers();
                //Show next view in viewflipper
                viewFlipper.showNext();
                return true;
            }

            //If search is closed
            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                searchView.setQuery("", true);
                //Show previous view in viewflipper
                viewFlipper.showPrevious();
                //Notify userSearchAdapter of data changes
                userSearchAdapter.notifyDataSetChanged();
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
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
                Intent intent = new Intent(getContext(), UploadStoryActivity.class);
                getContext().startActivity(intent);
                return true;
        }

        return false;
    }


    //Search for user with string s
    void searchUsers(String s) {

        //Get LiveData object from ViewModel for search result
        newsFeedActivityViewModel = new NewsFeedActivityViewModel(s);
        LiveData<DataSnapshot> liveData = newsFeedActivityViewModel.getDataSnapshotLiveData();


        //Observe the LiveData object which returns a DataSnapshot object
        liveData.observe(this, dataSnapshot -> {
            mUsers.clear();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                User user = snapshot.getValue(User.class);

                //Add each user found in search to list of found users
                mUsers.add(user);
            }

            //Notify adapter of updated data set
            userSearchAdapter.notifyDataSetChanged();
        });

    }

    //This method gets all possible user in db
    void readUsers() {

        //Get LiveData object from viewmodel for users
        newsFeedActivityViewModel = new NewsFeedActivityViewModel();
        LiveData<DataSnapshot> liveData = newsFeedActivityViewModel.getDataSnapshotLiveData();

        //Observe the LiveData object which returns a DataSnapshot object
        liveData.observe(getViewLifecycleOwner(), dataSnapshot -> {
            if ((searchView != null) && (searchView.getQuery().toString().equals(""))) {
                mUsers.clear();

                //Iterate through users
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    //Get User object
                    User user = snapshot.getValue(User.class);
                    mUsers.add(user);
                }

                userSearchAdapter.notifyDataSetChanged();
                progressBarUsers.setVisibility(View.GONE);
            }
        });
    }


    //Checks if your following each user from ViewModel
    private void getListOfFollowing() {

        //Init List
        followingList = new ArrayList<>();

        //Init ViewModel
        newsFeedActivityViewModel = new NewsFeedActivityViewModel();

        //Get LiveData object from ViewModel
        LiveData<DataSnapshot> liveData = newsFeedActivityViewModel.getFollowingSnapshot();

        //Observe the LiveData object which returns a DataSnapshot object
        liveData.observe(getViewLifecycleOwner(), snapshot -> {
            followingList.clear();
            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                followingList.add(dataSnapshot.getKey());
            }
            readStory();
            newsFeedActivityViewModel.getUserMutableLiveData().observe(getViewLifecycleOwner(), firebaseUser -> {
                followingList.add(firebaseUser.getUid());
            });

            getPostsAndUpdatePostList();
        });
    }


    //Retrieves posts from ViewModel
    private void getPostsAndUpdatePostList() {

    //Get LiveData object from ViewModel
        LiveData<DataSnapshot> liveData = newsFeedActivityViewModel.getPostsLiveData();

        //Observe the LiveData object which returns a DataSnapshot object
        liveData.observe(getViewLifecycleOwner(), dataSnapshot -> {
            postList.clear();

            //Iterate through posts
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                //Get Post object from snapshot
                Post post = snapshot.getValue(Post.class);

                for (String id : followingList) {
                    if (post.getPublisher().equals(id)) {
                        postList.add(post);
                    }
                }
            }
            progressBarPosts.setVisibility(View.GONE);
            viewSplitter.setVisibility(View.VISIBLE);
            postsAdapter.notifyDataSetChanged();

        });
    }

    private void readStory() {

        //Get LiveData object from ViewModel
        LiveData<DataSnapshot> liveData = newsFeedActivityViewModel.getStoriesLiveData();

        //Observe liveData object from ViewModel
        liveData.observe(getViewLifecycleOwner(), dataSnapshot -> {
            long currentTime = System.currentTimeMillis();
            storyList.clear();

            //Add current users story
            storyList.add(new Story("", 0, 0, "",
                    FirebaseAuth.getInstance().getCurrentUser().getUid()));

            //Iterate through list of following users
            for (String id : followingList) {
                int countStory = 0;
                Story story = null;

                //Iterate through stories posted by that user
                for (DataSnapshot snapshot : dataSnapshot.child(id).getChildren()) {

                    //Check story does not belong to current user
                    if (id != FirebaseAuth.getInstance().getCurrentUser().getUid()) {

                        //Get Story object
                        story = snapshot.getValue(Story.class);

                        //If story is active
                        if (currentTime > story.getTimestart() && currentTime < story.getTimeend()) {
                            //Increment story counter
                            countStory++;
                        }
                    }
                }

                //If story is active
                if (countStory > 0) {

                    //Add story to active story list
                    storyList.add(story);
                }
            }

            //Notify adapter of updated data set
            storyAdapter.notifyDataSetChanged();
        });

    }

    //This method sets adapter's for RecyclerViews
    private void setAdapters() {
        storyRecyclerView.setAdapter(storyAdapter);
        recyclerView.setAdapter(postsAdapter);
        recyclerViewSearch.setAdapter(userSearchAdapter);
    }

    //This method sets layoutManagers's for RecyclerViews
    private void setLayoutManagers(LinearLayoutManager linearLayoutManager, LinearLayoutManager linearLayoutManager2) {
        recyclerView.setLayoutManager(linearLayoutManager);
        storyRecyclerView.setLayoutManager(linearLayoutManager2);
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
    }

    //This method fixes RecyclerView's size
    private void setHasFixedSizeOfRecyclerViews() {
        recyclerView.setHasFixedSize(true);
        storyRecyclerView.setHasFixedSize(true);
        recyclerViewSearch.setHasFixedSize(true);
    }

    //This method initialize's class Lists
    private void initializeLists() {
        storyList = new ArrayList<>();
        postList = new ArrayList<>();
        mUsers = new ArrayList<>();
    }

    //This method initialize's class Adapters
    private void initializeAdapters() {
        storyAdapter = new StoryAdapter(getContext(), storyList);
        postsAdapter = new PostsAdapter(getContext(), postList);
        userSearchAdapter = new UserSearchAdapter(getContext(), mUsers);
    }

    //This method initializes views
    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        viewFlipper = view.findViewById(R.id.viewflipper);
        recyclerView = view.findViewById(R.id.news_feed_recycler_view);
        storyRecyclerView = view.findViewById(R.id.story_recycler_view);
        linearLayout = view.findViewById(R.id.news_feed_lin_layout);
        recyclerViewSearch = view.findViewById(R.id.recycler_view);
        progressBarUsers = view.findViewById(R.id.newsfeed_progress_bar);
        progressBarPosts = view.findViewById(R.id.posts_progress_bar);
        viewSplitter = view.findViewById(R.id.view_splitter);
    }
}