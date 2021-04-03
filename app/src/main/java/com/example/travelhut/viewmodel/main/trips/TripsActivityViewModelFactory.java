package com.example.travelhut.viewmodel.main.trips;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.travelhut.viewmodel.main.newsfeed.newsfeed.CommentActivityViewModel;

public class TripsActivityViewModelFactory implements ViewModelProvider.Factory {

    //Instance Variables
    private Application mApplication;
    private String mTripId;

    //Constructor
    public TripsActivityViewModelFactory(Application application, String tripId) {
        mApplication = application;
        mTripId = tripId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new TripActivityViewModel(mApplication, mTripId);
    }
}
