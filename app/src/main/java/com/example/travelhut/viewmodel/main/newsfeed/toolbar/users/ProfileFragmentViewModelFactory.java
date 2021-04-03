package com.example.travelhut.viewmodel.main.newsfeed.toolbar.users;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ProfileFragmentViewModelFactory implements ViewModelProvider.Factory {

    //Instance Variables
    private Application mApplication;
    private String mProfileId;

    //Constructor
    public ProfileFragmentViewModelFactory(Application application, String profileId) {
        mApplication = application;
        mProfileId = profileId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new ProfileFragmentViewModel(mApplication, mProfileId);
    }
}
