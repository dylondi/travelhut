package com.example.travelhut.viewmodel.main.newsfeed;


import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.travelhut.model.authentication.AuthRepository;
import com.example.travelhut.model.main.profile.FollowingRepository;
import com.example.travelhut.model.main.posts.PostsRepository;
import com.example.travelhut.model.main.newsfeed.StoriesRepository;
import com.example.travelhut.model.authentication.UsersRepository;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;


public class NewsFeedActivityViewModel extends ViewModel {


    //Instance Variables
    private UsersRepository liveData;
    private PostsRepository postsRepository;
    private FollowingRepository followingRepository;
    private AuthRepository authRepository;
    private StoriesRepository storiesRepository;

    //Constructors
    public NewsFeedActivityViewModel(String s) {
        super();
        liveData = new UsersRepository(s);
        followingRepository = new FollowingRepository();
        postsRepository = new PostsRepository();
        authRepository = new AuthRepository();
        storiesRepository = new StoriesRepository();

    }

    public NewsFeedActivityViewModel() {
        liveData = new UsersRepository();
        postsRepository = new PostsRepository();
        followingRepository = new FollowingRepository();
        authRepository = new AuthRepository();
        storiesRepository = new StoriesRepository();
    }

    //This method returns a LiveData object containing a firebase DataSnapshot
    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return liveData;
    }

    //This method returns a LiveData object containing a firebase DataSnapshot containing a list of posts
    @NonNull
    public LiveData<DataSnapshot> getPostsLiveData() {
        return postsRepository;
    }

    //This method returns a LiveData object containing a firebase DataSnapshot containing a list of users which current user follows
    @NonNull
    public LiveData<DataSnapshot> getFollowingSnapshot() {
        return followingRepository;
    }

    //This method returns a LiveData object containing a firebase DataSnapshot containing a list of stories
    @NonNull
    public LiveData<DataSnapshot> getStoriesLiveData() {
        return storiesRepository;
    }

    //This method returns a LiveData object containing the current FirebaseUser
    @NonNull
    public LiveData<FirebaseUser> getUserMutableLiveData() {
        return authRepository.getUserMutableLiveData();
    }
}
