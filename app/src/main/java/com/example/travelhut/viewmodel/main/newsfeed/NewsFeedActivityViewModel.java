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

    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return liveData;
    }

    @NonNull
    public LiveData<DataSnapshot> getPostsLiveData() {
        return postsRepository;
    }

    @NonNull
    public LiveData<DataSnapshot> getFollowingSnapshot() {

        return followingRepository;
    }

    @NonNull
    public LiveData<DataSnapshot> getStoriesLiveData() {

        return storiesRepository;
    }

    @NonNull
    public LiveData<FirebaseUser> getUserMutableLiveData() {
        return authRepository.getUserMutableLiveData();
    }
}
