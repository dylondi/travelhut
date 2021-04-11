package com.example.travelhut.viewmodel.main.profile.toolbar;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.travelhut.model.main.profile.SinglePostRepository;
import com.google.firebase.database.DataSnapshot;

public class SinglePostFragmentViewModel extends AndroidViewModel {

    //Instance Variable
    private SinglePostRepository singlePostRepository;

    //Constructor
    public SinglePostFragmentViewModel(@NonNull Application application, String postId) {
        super(application);
        singlePostRepository = new SinglePostRepository(postId);
    }

    //This method returns a LiveData object containing a DataSnapshot of a post
    @NonNull
    public LiveData<DataSnapshot> getPostLiveData() {
        return singlePostRepository;
    }
}
