package com.example.travelhut.model;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UsersAppRepository extends LiveData<DataSnapshot> {

    private final MyValueEventListener listener = new MyValueEventListener();
    private static final String LOG_TAG = "UsersAppRepository";

    private final Query query;

    public UsersAppRepository(Query query) {
        this.query = query;
    }

    public UsersAppRepository(DatabaseReference ref) {
        this.query = ref;
    }


    public Query getUserSearchQuery(String s){
        return FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
                .startAt(s)
                .endAt(s+"\uf8ff");
    }

    @Override
    protected void onActive() {
        Log.d(LOG_TAG, "onActive");
        query.addValueEventListener(listener);
    }

    @Override
    protected void onInactive() {
        Log.d(LOG_TAG, "onInactive");
        query.removeEventListener(listener);
    }

    private class MyValueEventListener implements ValueEventListener {
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
