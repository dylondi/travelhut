package com.example.travelhut.model.main.newsfeed;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.travelhut.model.utils.StringsRepository;
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

public class UploadStoryRepository extends LiveData<DataSnapshot> {

    //Instance Variables
    private final int dayLength = 86400000;
    private StorageReference storyStorageReference;
    private StorageTask storageTask;
    private FirebaseUser firebaseUser;
    private MutableLiveData<Boolean> imageUploadedMutableLiveData;
    private MutableLiveData<String> uploadFailedMessage;

    //Constructor
    public UploadStoryRepository() {
        storyStorageReference = FirebaseStorage.getInstance().getReference(StringsRepository.STORY);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        imageUploadedMutableLiveData = new MutableLiveData<>();
        uploadFailedMessage = new MutableLiveData<>();
    }

    //This method will upload the story to firebase
    public void uploadStory(Uri imageUri, String fileExtension) {

        //Check for null imageUri
        if (imageUri != null) {

            //Unique name(using timestamp) assigned for the image reference stored in our database
            StorageReference imgRef = storyStorageReference.child(System.currentTimeMillis() + StringsRepository.FULL_STOP + fileExtension);

            //Initialize storageTask by setting the image Uri Object to the storage reference
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
                    String imageUrl = downloadUri.toString();

                    //Get current user's UUID
                    String currentUserId = firebaseUser.getUid();

                    //Retrieve the database reference to the current users stories
                    DatabaseReference storyDatabaseReference = FirebaseDatabase.getInstance().getReference(StringsRepository.STORY_CAP)
                            .child(currentUserId);

                    //Retrieve story key for story to be uploaded
                    String storyId = storyDatabaseReference.push().getKey();

                    //Calculate the time the story should be removed (24 hours in our case)
                    long timeEnd = System.currentTimeMillis() + dayLength;

                    //Calling uploadStoryToDatabase with our current story values
                    uploadStoryToDatabase(imageUrl, currentUserId, storyDatabaseReference, storyId, timeEnd);

                    //Post true too imageUploadedMutableLiveData containing boolean
                    imageUploadedMutableLiveData.postValue(true);
                }
                //If task is unsuccessful -> post false value to imageUploadedMutableLiveData and post failed string message
                else {
                    imageUploadedMutableLiveData.postValue(false);
                    uploadFailedMessage.postValue(StringsRepository.FAILED_CAP);
                }

                //If task completion fails -> post false value to imageUploadedMutableLiveData and post failed string message
            }).addOnFailureListener(e -> {
                imageUploadedMutableLiveData.postValue(false);
                uploadFailedMessage.postValue(e.getMessage());
            });
        }

        //Else -> post false value to imageUploadedMutableLiveData and post failed string message
        else {
            imageUploadedMutableLiveData.postValue(false);
            uploadFailedMessage.postValue(StringsRepository.NO_IMAGE_SELECTED);
        }
    }

    //This method uploads a HashMap object containing a story's details for Firebase Database
    private void uploadStoryToDatabase(String myUrl, String myId, DatabaseReference storyDatabaseReference, String storyId, long timeEnd) {

        //Create HashMap
        HashMap<String, Object> hashMap = new HashMap<>();

        //Put all story data into HashMap
        hashMap.put(StringsRepository.IMAGE_URL, myUrl);
        hashMap.put(StringsRepository.STORY_START_TIME, ServerValue.TIMESTAMP);
        hashMap.put(StringsRepository.STORY_END_TIME, timeEnd);
        hashMap.put(StringsRepository.STORY_ID, storyId);
        hashMap.put(StringsRepository.USER_ID, myId);

        //Set the value of the HashMap to the DatabaseReference referencing an empty reference
        storyDatabaseReference.child(storyId).setValue(hashMap);
    }

    //This method returns the boolean valued MutableLiveData object called imageUploadedMutableLiveData
    public MutableLiveData<Boolean> getImageUploadedMutableLiveData(){
        return imageUploadedMutableLiveData;
    }

    //This method returns the String valued MutableLiveData object called imageUploadedMutableLiveData
    public MutableLiveData<String> getUploadFailedMessage(){
        return uploadFailedMessage;
    }
}
