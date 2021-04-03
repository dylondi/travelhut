package com.example.travelhut.viewmodel.main.map_search;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.travelhut.viewmodel.main.trips.TripActivityViewModel;

public class MapSearchActivityViewModelFactory implements ViewModelProvider.Factory {
    private Application mApplication;
    private String mPlaceName;


    public MapSearchActivityViewModelFactory(Application application, String placeName) {
        mApplication = application;
        mPlaceName = placeName;
    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new MapSearchActivityViewModel(mApplication);
    }
}
