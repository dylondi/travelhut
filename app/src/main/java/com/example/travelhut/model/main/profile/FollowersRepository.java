package com.example.travelhut.model.main.profile;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.travelhut.model.utils.StringsRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FollowersRepository extends LiveData<DataSnapshot> {

    //Instance Variables
    private static final String TAG = "FollowersAppRepository";
    private FollowersValueEventListener listenerFollowers;
    private DatabaseReference followers;

    //Constructor
    public FollowersRepository(String profileId) {
        listenerFollowers = new FollowersValueEventListener();
        followers = FirebaseDatabase.getInstance().getReference(StringsRepository.FOLLOW_CAP).child(profileId).child(StringsRepository.FOLLOWERS);
    }

    //Method called when an observer is active
    @Override
    protected void onActive() {
        Log.d(TAG, StringsRepository.ON_ACTIVE);
        //assign event listener to find changes in number of followers
        followers.addValueEventListener(listenerFollowers);
    }

    //Method called when an observers lifecycle states has not started or resumed
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
