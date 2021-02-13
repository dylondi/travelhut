        package com.example.travelhut.model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UsersAppRepository extends LiveData<DataSnapshot> {

    private final  MyValueEventListener listener = new MyValueEventListener();
    private static final String LOG_TAG = "UsersAppRepository";

    private final Query query;
    private String s;
    private DatabaseReference reference;
    private MutableLiveData<Boolean> loggedOutMutableLiveData;
    private FirebaseAuth firebaseAuth;


    //constructor with search by user
    public UsersAppRepository(String s) {
        this.s = s;
        this.query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
                .startAt(s)
                .endAt(s+"\uf8ff");

        firebaseAuth = FirebaseAuth.getInstance();
        loggedOutMutableLiveData = new MutableLiveData<>();
        //reference = FirebaseDatabase.getInstance().getReference("Users");

    }

    //constructor without search by user
    public UsersAppRepository() {

        reference = FirebaseDatabase.getInstance().getReference("Users");
        query = (Query) reference;

    }

    public Query getUserSearchQuery(){
        return query;
    }

    public DatabaseReference getReference() {
        return reference;
    }

    //method called when an observer is active
    @Override
    protected void onActive() {
        Log.d(LOG_TAG, "onActive");
        query.addValueEventListener(listener);
    }

    //method called when an observers lifecycle states has not started or resumed
    @Override
    protected void onInactive() {
        Log.d(LOG_TAG, "onInactive");
        query.removeEventListener(listener);
    }

    //event listener to find changes in data
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

    public MutableLiveData<Boolean> getLoggedOutMutableLiveData() {
        return loggedOutMutableLiveData;
    }

    public void logout(){
        firebaseAuth.signOut();
        loggedOutMutableLiveData.postValue(true);
    }

}
