package com.example.travelhut.viewmodel.main.newsfeed;


import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.travelhut.model.FollowingAppRepository;
import com.example.travelhut.model.PostsAppRepository;
import com.example.travelhut.model.UsersAppRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;


public class NewsFeedActivityViewModel extends ViewModel {

    private UsersAppRepository liveData;
    private Query userSearchQuery;
    private PostsAppRepository postsAppRepository;
    private FollowingAppRepository followingAppRepository;




    public NewsFeedActivityViewModel(String s) {
        super();
        liveData = new UsersAppRepository(s);
        followingAppRepository = new FollowingAppRepository();
        postsAppRepository = new PostsAppRepository();
        this.userSearchQuery = liveData.getUserSearchQuery();

    }

    public NewsFeedActivityViewModel() {
        liveData = new UsersAppRepository();
        postsAppRepository = new PostsAppRepository();
        followingAppRepository = new FollowingAppRepository();
        this.userSearchQuery = liveData.getReference();
    }

    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return liveData;
    }

    @NonNull
    public LiveData<DataSnapshot> getPostsLiveData() {
        return postsAppRepository;
    }

    public Query getQuery(){
        return userSearchQuery;
    }

    @NonNull
    public LiveData<DataSnapshot> getFollowingSnapshot() {

        return followingAppRepository;
    }
}
