package com.example.travelhut.viewmodel.main.newsfeed.newsfeed;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class CommentActivityViewModelFactory implements ViewModelProvider.Factory{

    //Instance Variables
    private Application mApplication;
    private String mPostId;
    private String mPublisherId;

    //Constructor
    public CommentActivityViewModelFactory(Application application, String postId, String publisherId) {
        mApplication = application;
        mPostId = postId;
        mPublisherId = publisherId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new CommentActivityViewModel(mApplication, mPostId, mPublisherId);
    }
}
