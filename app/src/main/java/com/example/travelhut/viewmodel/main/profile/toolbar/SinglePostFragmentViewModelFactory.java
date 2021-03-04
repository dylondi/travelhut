package com.example.travelhut.viewmodel.main.profile.toolbar;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class SinglePostFragmentViewModelFactory implements ViewModelProvider.Factory{
    private Application mApplication;
    private String mPostId;


    public SinglePostFragmentViewModelFactory(Application application, String postId) {
        mApplication = application;
        mPostId = postId;
    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new SinglePostFragmentViewModel(mApplication, mPostId);
    }
}
