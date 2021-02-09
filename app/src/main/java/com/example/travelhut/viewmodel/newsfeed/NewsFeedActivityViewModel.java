package com.example.travelhut.viewmodel.newsfeed;


import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.travelhut.model.UsersAppRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;


public class NewsFeedActivityViewModel extends ViewModel {

    private UsersAppRepository liveData;
    private Query userSearchQuery;


    public NewsFeedActivityViewModel(String s) {
        super();
        liveData = new UsersAppRepository(s);
        this.userSearchQuery = liveData.getUserSearchQuery();
    }

    public NewsFeedActivityViewModel() {
        liveData = new UsersAppRepository();
        this.userSearchQuery = liveData.getReference();
    }

    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return liveData;
    }

    public Query getQuery(){
        return userSearchQuery;
    }
}
