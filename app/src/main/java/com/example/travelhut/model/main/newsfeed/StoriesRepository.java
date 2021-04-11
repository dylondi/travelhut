package com.example.travelhut.model.main.newsfeed;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.travelhut.model.utils.StringsRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StoriesRepository extends LiveData<DataSnapshot> {

    //Instance Variables
    private static final String TAG = "StoriesAppRepository";
    private StoriesEventListener storiesEventListener = new StoriesEventListener();
    private DatabaseReference reference;

    //Constructor
    public StoriesRepository() {
        reference = FirebaseDatabase.getInstance().getReference(StringsRepository.STORY_CAP);
    }

    //Method called when an observer is active
    @Override
    protected void onActive() {
        Log.d(TAG, StringsRepository.ON_ACTIVE);
        //Assign event listener to find changes in posts data
        reference.addValueEventListener(storiesEventListener);
    }

    //Method called when an observers lifecycle states has not started or resumed
    @Override
    protected void onInactive() {
        Log.d(TAG, StringsRepository.ON_INACTIVE);
        //Remove event listener
        reference.removeEventListener(storiesEventListener);
    }

    //Event listener to find changes in data
    private class StoriesEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            setValue(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(TAG, "Can't listen to reference " + reference.toString(), databaseError.toException());
        }
    }
}
