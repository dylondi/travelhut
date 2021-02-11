package com.example.travelhut.viewmodel.main.newsfeed;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class NewsFeedActivityViewModelFactory implements ViewModelProvider.Factory{


    private Application mApplication;
    private String mParam;

    public NewsFeedActivityViewModelFactory(Application application, String param) {
        mApplication = application;
        mParam = param;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return modelClass.cast(new NewsFeedActivityViewModelFactory(mApplication, mParam));
        //return (T) new NewsFeedActivityViewModelFactory(mApplication, mParam);
    }

}
