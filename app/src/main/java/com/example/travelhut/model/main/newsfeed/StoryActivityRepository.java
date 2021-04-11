package com.example.travelhut.model.main.newsfeed;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.travelhut.model.utils.StringsRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StoryActivityRepository extends LiveData<DataSnapshot> {

    //Instance Variables
    private static final String TAG = "StoryActivityRepository";
    private StoryEventListener storyEventListener = new StoryEventListener();
    private DatabaseReference reference;
    private String userId;
    private MutableLiveData<Long> seenNumber;
    private MutableLiveData<Boolean> storyDeleted;
    private Application application;

    //Constructor
    public StoryActivityRepository(String userId, Application application) {
        reference = FirebaseDatabase.getInstance().getReference(StringsRepository.STORY_CAP).child(userId);
        this.userId = userId;
        this.application = application;
        seenNumber = new MutableLiveData<>();
        storyDeleted = new MutableLiveData<>();
    }

    //Method called when an observer is active
    @Override
    protected void onActive() {
        Log.d(TAG, StringsRepository.ON_ACTIVE);
        //Assign event listener to find changes in posts data
        reference.addValueEventListener(storyEventListener);
    }

    //Method called when an observers lifecycle states has not started or resumed
    @Override
    protected void onInactive() {
        Log.d(TAG, StringsRepository.ON_INACTIVE);
        //Remove event listener
        reference.removeEventListener(storyEventListener);
    }

    //Event listener to find changes in data
    private class StoryEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            setValue(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(TAG, "Can't listen to reference " + reference.toString(), databaseError.toException());
        }
    }

    //This method adds a view to story with storyId
    public void addView(String storyId){

        //Story -> user -> story -> viewed by current user -> true
        FirebaseDatabase.getInstance().getReference().child(StringsRepository.STORY_CAP).child(userId)
                .child(storyId).child(StringsRepository.VIEWS).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
    }

    //This method retrieves the number of views on current story and posts number of views to LiveData object seenNumber
    public void seenNumber(String storyId){

        //DatabaseReference to story views
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(StringsRepository.STORY_CAP)
                .child(userId).child(storyId).child(StringsRepository.VIEWS);

        //Add ListenerForSingleValueEvent to DatabaseReference
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Set Value of number of children in dataSnapshot
                seenNumber.setValue(dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void deleteStory(String storyId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(StringsRepository.STORY_CAP)
                .child(userId).child(storyId);
        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(application, "Deleted!", Toast.LENGTH_SHORT).show();
                    storyDeleted.setValue(true);
                }
            }
        });
    }

    public MutableLiveData<Long> getSeenNumber(){
        return seenNumber;
    }


}
