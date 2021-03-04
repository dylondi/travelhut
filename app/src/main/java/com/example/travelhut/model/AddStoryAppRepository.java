package com.example.travelhut.model;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import androidx.lifecycle.LiveData;

import com.example.travelhut.views.main.newsfeed.NewsFeedActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.HashMap;

public class AddStoryAppRepository extends LiveData<DataSnapshot> {

    //Variables
    private StorageReference storyStorageReference;
    private StorageTask storageTask;
    private final int dayLength = 86400000;
    private FirebaseUser firebaseUser;

    //Constructor
    public AddStoryAppRepository() {
        storyStorageReference = FirebaseStorage.getInstance().getReference(StringsRepository.STORY);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    //This method will upload the story to firebase
    public void publishStory(Uri imageUri, String fileExtension, Application application, ProgressDialog progressDialog) {

        if (imageUri != null) {


            StorageReference imgRef = storyStorageReference.child(System.currentTimeMillis() + StringsRepository.FULL_STOP + fileExtension);

            storageTask = imgRef.putFile(imageUri);

            //Returns a new Task that will be completed with the result of applying the specified Continuation to this Task
            storageTask.continueWithTask((Continuation) task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return imgRef.getDownloadUrl();
            }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {

                //If Image is uploaded to Firebase Storage successfully
                if (task.isSuccessful()) {

                    ///Get image uri and assign to String variable
                    Uri downloadUri = task.getResult();
                    String myUrl = downloadUri.toString();

                    //Get current user's UUID
                    String myId = firebaseUser.getUid();

                    //Retrieve the database reference to the current users stories
                    DatabaseReference storyDatabaseReference = FirebaseDatabase.getInstance().getReference(StringsRepository.STORY_CAP)
                            .child(myId);

                    //Retrieve story key for story to be uploaded
                    String storyId = storyDatabaseReference.push().getKey();

                    //Calculate the time the story should be removed (24 hours in our case)
                    long timeEnd = System.currentTimeMillis() + dayLength;

                    //Calling uploadStoryToDatabase with our current story values
                    uploadStoryToDatabase(myUrl, myId, storyDatabaseReference, storyId, timeEnd);

                    progressDialog.dismiss();

                    //Create and start intent to navigate to the NewsFeedActivity upon successful task completion
                    Intent newsFeedIntent = new Intent(application, NewsFeedActivity.class);
                    newsFeedIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    application.startActivity(newsFeedIntent);
                }
                //If task is unsuccessful -> display toast
                else {
                    Toast.makeText(application, "Failed", Toast.LENGTH_SHORT).show();
                }

                //If task completion fails -> display toast
            }).addOnFailureListener(e -> Toast.makeText(application, e.getMessage(), Toast.LENGTH_SHORT).show());
        }
        //Else -> display toast informing no image was selected
        else {
            Toast.makeText(application, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    //This method uploads a HashMap object containing a story's details for Firebase Database
    private void uploadStoryToDatabase(String myUrl, String myId, DatabaseReference storyDatabaseReference, String storyId, long timeEnd) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(StringsRepository.IMAGE_URL, myUrl);
        hashMap.put(StringsRepository.STORY_START_TIME, ServerValue.TIMESTAMP);
        hashMap.put(StringsRepository.STORY_END_TIME, timeEnd);
        hashMap.put(StringsRepository.STORY_ID, storyId);
        hashMap.put(StringsRepository.USER_ID, myId);
        storyDatabaseReference.child(storyId).setValue(hashMap);
    }
}
