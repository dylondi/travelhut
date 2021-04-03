package com.example.travelhut.model.authentication;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.travelhut.model.utils.StringsRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UsersRepository extends LiveData<DataSnapshot> {

    //Instance Variables
    private final UserEventListener userEventListener = new UserEventListener();
    private static final String LOG_TAG = "UsersAppRepository";
    private final Query query;
    private DatabaseReference reference;
    private MutableLiveData<Boolean> loggedOutMutableLiveData;
    private FirebaseAuth firebaseAuth;


    //Constructor with search by user
    public UsersRepository(String s) {
        this.query = FirebaseDatabase.getInstance().getReference(StringsRepository.USERS_CAP).orderByChild(StringsRepository.USERNAME)
                .startAt(s)
                .endAt(s + "\uf8ff");

        firebaseAuth = FirebaseAuth.getInstance();
        loggedOutMutableLiveData = new MutableLiveData<>();
    }

    //Constructor without search by user
    public UsersRepository() {
        reference = FirebaseDatabase.getInstance().getReference(StringsRepository.USERS_CAP);
        query = (Query) reference;
    }

    public Query getUserSearchQuery() {
        return query;
    }

    public DatabaseReference getReference() {
        return reference;
    }

    //Method called when an observer is active
    @Override
    protected void onActive() {
        Log.d(LOG_TAG, StringsRepository.ON_ACTIVE);
        query.addValueEventListener(userEventListener);
    }

    //Method called when an observers lifecycle states has not started or resumed
    @Override
    protected void onInactive() {
        Log.d(LOG_TAG, StringsRepository.ON_INACTIVE);
        query.removeEventListener(userEventListener);
    }

    //Event listener to find changes in data
    private class UserEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            setValue(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            //Log.e(LOG_TAG, "Can't listen to query " + query, databaseError.toException());
        }
    }

    //This method returns a boolean MutableLiveData object indicating if the current user is logged in or out
    public MutableLiveData<Boolean> getLoggedOutMutableLiveData() {
        return loggedOutMutableLiveData;
    }

    //This method will logout the current user
    public void logout() {
        firebaseAuth.signOut();
        loggedOutMutableLiveData.postValue(true);
    }

}
