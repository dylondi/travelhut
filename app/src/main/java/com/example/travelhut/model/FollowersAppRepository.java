package com.example.travelhut.model;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FollowersAppRepository extends LiveData<DataSnapshot> {

    private static final String TAG = "FollowersAppRepository";
    private FollowersValueEventListener listenerFollowers;
    private DatabaseReference followers;
    private FirebaseUser firebaseUser;

    //Constructor
    public FollowersAppRepository() {
        listenerFollowers = new FollowersValueEventListener();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        followers = FirebaseDatabase.getInstance().getReference(StringsRepository.FOLLOW_CAP).child(firebaseUser.getUid()).child(StringsRepository.FOLLOWERS);
    }



    //method called when an observer is active
    @Override
    protected void onActive() {
        Log.d(TAG, StringsRepository.ON_ACTIVE);
        //assign event listener to find changes in number of followers
        followers.addValueEventListener(listenerFollowers);

    }

    //method called when an observers lifecycle states has not started or resumed
    @Override
    protected void onInactive() {
        Log.d(TAG, StringsRepository.ON_INACTIVE);
        //remove event listener
        followers.removeEventListener(listenerFollowers);
    }


    //event listener to find changes in data
    private class FollowersValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            setValue(dataSnapshot);

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            //Log.e(LOG_TAG, "Can't listen to query " + query, databaseError.toException());
        }
    }
}
