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

public class FollowingAppRepository extends LiveData<DataSnapshot> {

    private static final String TAG = "FollowingAppRepository";
    private FollowingValueEventListener listenerFollowing = new FollowingValueEventListener();
    private DatabaseReference following;
    private FirebaseUser firebaseUser;

    public FollowingAppRepository() {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        following = FirebaseDatabase.getInstance().getReference("Follow").child(firebaseUser.getUid()).child("following");
    }


    @Override
    protected void onActive() {
        Log.d(TAG, "onActive");
        following.addValueEventListener(listenerFollowing);

    }

    @Override
    protected void onInactive() {
        Log.d(TAG, "onInactive");
        following.removeEventListener(listenerFollowing);
    }

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
