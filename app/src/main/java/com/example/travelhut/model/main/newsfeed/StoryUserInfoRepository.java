package com.example.travelhut.model.main.newsfeed;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.travelhut.model.utils.StringsRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StoryUserInfoRepository extends LiveData<DataSnapshot> {


    private static final String TAG = "StoryUserInfoRepository";
    private StoryUserInfoEventListener storyUserInfoEventListener = new StoryUserInfoEventListener();
    private DatabaseReference databaseReference;

    public StoryUserInfoRepository(String userId){
        databaseReference = FirebaseDatabase.getInstance().getReference(StringsRepository.USERS_CAP)
                .child(userId);
    }


    //Method called when an observer is active
    @Override
    protected void onActive() {
        Log.d(TAG, StringsRepository.ON_ACTIVE);
        //assign event listener to find changes in posts data
        databaseReference.addValueEventListener(storyUserInfoEventListener);
    }

    //Method called when an observers lifecycle states has not started or resumed
    @Override
    protected void onInactive() {
        Log.d(TAG, StringsRepository.ON_INACTIVE);
        //remove event listener
        databaseReference.removeEventListener(storyUserInfoEventListener);
    }

    //Event listener to find changes in data
    private class StoryUserInfoEventListener implements ValueEventListener {
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
