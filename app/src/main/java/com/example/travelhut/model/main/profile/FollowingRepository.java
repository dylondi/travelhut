package com.example.travelhut.model.main.profile;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.travelhut.model.utils.StringsRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FollowingRepository extends LiveData<DataSnapshot> {

    //Variables
    private static final String TAG = "FollowingAppRepository";
    private FollowingValueEventListener listenerFollowing = new FollowingValueEventListener();
    private DatabaseReference following;
    private FirebaseUser firebaseUser;

    //Constructors
    public FollowingRepository() {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        following = FirebaseDatabase.getInstance().getReference(StringsRepository.FOLLOW_CAP).child(firebaseUser.getUid()).child(StringsRepository.FOLLOWING);
    }
    public FollowingRepository(String profileId) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        following = FirebaseDatabase.getInstance().getReference(StringsRepository.FOLLOW_CAP).child(profileId).child(StringsRepository.FOLLOWING);
    }


    //method called when an observer is active
    @Override
    protected void onActive() {
        Log.d(TAG, StringsRepository.ON_ACTIVE);
        //assign event listener to find changes in number of following
        following.addValueEventListener(listenerFollowing);

    }

    //method called when an observers lifecycle states has not started or resumed
    @Override
    protected void onInactive() {
        Log.d(TAG, StringsRepository.ON_INACTIVE);
        //remove event listener
        following.removeEventListener(listenerFollowing);
    }

    //event listener to find changes in data
    private class FollowingValueEventListener implements ValueEventListener {
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
