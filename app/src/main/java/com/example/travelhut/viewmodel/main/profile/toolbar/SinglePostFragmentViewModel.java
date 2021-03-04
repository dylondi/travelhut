package com.example.travelhut.viewmodel.main.profile.toolbar;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.travelhut.model.PostsAppRepository;
import com.example.travelhut.model.SinglePostAppRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public class SinglePostFragmentViewModel extends AndroidViewModel {

    private MutableLiveData<DatabaseReference> postMutableLiveData;

    private SinglePostAppRepository singlePostAppRepository;
    public SinglePostFragmentViewModel(@NonNull Application application, String postId) {
        super(application);
        singlePostAppRepository = new SinglePostAppRepository(postId);
    }

    @NonNull
    public LiveData<DataSnapshot> getPostLiveData() {

        return singlePostAppRepository;
    }


}
