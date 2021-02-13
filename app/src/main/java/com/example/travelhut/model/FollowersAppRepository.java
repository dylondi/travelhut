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
    private FollowersValueEventListener listenerFollowers = new FollowersValueEventListener();
    private DatabaseReference followers;
    private FirebaseUser firebaseUser;

    public FollowersAppRepository() {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        followers = FirebaseDatabase.getInstance().getReference("Follow").child(firebaseUser.getUid()).child("followers");
    }


    @Override
    protected void onActive() {
        Log.d(TAG, "onActive");
        followers.addValueEventListener(listenerFollowers);

    }

    @Override
    protected void onInactive() {
        Log.d(TAG, "onInactive");
        followers.removeEventListener(listenerFollowers);
    }

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
