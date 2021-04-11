package com.example.travelhut.model.main.newsfeed;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.travelhut.model.utils.StringsRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;


public class UserSearchRepository extends LiveData<DataSnapshot> {

    //Instance Variables
    private MutableLiveData<FirebaseUser> userMutableLiveData;
    private FirebaseAuth firebaseAuth;

    //Constructor
    public UserSearchRepository() {
        userMutableLiveData = new MutableLiveData<>();
        firebaseAuth = FirebaseAuth.getInstance();

        //If a user is signed in, post the FirebaseUser object to the MutableLiveData object
        if (firebaseAuth.getCurrentUser() != null) {
            userMutableLiveData.postValue(firebaseAuth.getCurrentUser());
        }
    }

    //This method is called when the current user follows another user
    public void follow(String userId) {
        //Updates db -> currentUser -> following -> otherUser -> true
        FirebaseDatabase.getInstance().getReference()
                .child(StringsRepository.FOLLOW_CAP)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(StringsRepository.FOLLOWING)
                .child(userId).setValue(true);

        //Updates db -> otherUser -> followers -> currentUser -> true
        FirebaseDatabase.getInstance().getReference()
                .child(StringsRepository.FOLLOW_CAP)
                .child(userId)
                .child(StringsRepository.FOLLOWERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
    }

    //This method is called when current user unfollows another user
    public void unFollow(String userId) {
        //Updates db -> currentUser -> following -> otherUser -> removes value
        FirebaseDatabase.getInstance().getReference()
                .child(StringsRepository.FOLLOW_CAP)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(StringsRepository.FOLLOWING)
                .child(userId).removeValue();

        //Updates db -> otherUser -> followers -> currentUser -> removes value
        FirebaseDatabase.getInstance().getReference()
                .child(StringsRepository.FOLLOW_CAP)
                .child(userId)
                .child(StringsRepository.FOLLOWERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
    }

    //This method returns a MutableLiveData object containing a FirebaseUser object
    public MutableLiveData<FirebaseUser> getUserMutableLiveData() {
        return userMutableLiveData;
    }
}
