package com.example.travelhut.model;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UserSearchAppRepository extends LiveData<DataSnapshot> {

    private MutableLiveData<Boolean> isFollowing;
    private MutableLiveData<FirebaseUser> userMutableLiveData;
    private MutableLiveData<DatabaseReference> referenceMutableLiveData;
    private FirebaseAuth firebaseAuth;

    //constructor
    public UserSearchAppRepository() {
        isFollowing = new MutableLiveData<>();
        userMutableLiveData = new MutableLiveData<>();
        referenceMutableLiveData = new MutableLiveData<>();
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            userMutableLiveData.postValue(firebaseAuth.getCurrentUser());
            referenceMutableLiveData.postValue(FirebaseDatabase.getInstance().getReference()
                    .child(StringsRepository.FOLLOW_CAP).child(firebaseAuth.getCurrentUser().getUid()).child(StringsRepository.FOLLOWING));
        }
        //if(FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseAuth.getCurrentUser().getUid()).child("following").child())


    }

    //method is called when current user follows another user
    public void follow(String userId) {
        //updates db -> currentUser -> following -> otherUser -> true
        FirebaseDatabase.getInstance().getReference()
                .child(StringsRepository.FOLLOW_CAP)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(StringsRepository.FOLLOWING)
                .child(userId).setValue(true);

        //updates db -> otherUser -> followers -> currentUser -> true
        FirebaseDatabase.getInstance().getReference()
                .child(StringsRepository.FOLLOW_CAP)
                .child(userId)
                .child(StringsRepository.FOLLOWERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
    }

    //method is called when current user unfollows another user
    public void unFollow(String userId) {
        //updates db -> currentUser -> following -> otherUser -> removes value
        FirebaseDatabase.getInstance().getReference()
                .child(StringsRepository.FOLLOW_CAP)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(StringsRepository.FOLLOWING)
                .child(userId).removeValue();

        //updates db -> otherUser -> followers -> currentUser -> removes value
        FirebaseDatabase.getInstance().getReference()
                .child(StringsRepository.FOLLOW_CAP)
                .child(userId)
                .child(StringsRepository.FOLLOWERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
    }

    public MutableLiveData<FirebaseUser> getUserMutableLiveData() {
        return userMutableLiveData;
    }
    public MutableLiveData<DatabaseReference> getReferenceMutableLiveData() {
        return referenceMutableLiveData;
    }





//can remove method?
    public MutableLiveData<Boolean> getIsFollowing(String userid){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                  .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userid).exists()) {


                    isFollowing.setValue(true);
                    Log.d("logging", "change text to following : " + userid);
                } else {
                    isFollowing.setValue(false);
                    Log.d("logging", "change text to follow : " + userid);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return isFollowing;
    }
}
