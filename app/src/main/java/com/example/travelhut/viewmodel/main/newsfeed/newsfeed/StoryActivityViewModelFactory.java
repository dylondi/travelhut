package com.example.travelhut.viewmodel.main.newsfeed.newsfeed;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class StoryActivityViewModelFactory  implements ViewModelProvider.Factory{

   //Instance Variables
    private Application mApplication;
    private String mUserId;

    //Constructor
    public StoryActivityViewModelFactory(Application application, String userId) {
        mApplication = application;
        mUserId = userId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new StoryActivityViewModel(mApplication, mUserId);
    }
}

