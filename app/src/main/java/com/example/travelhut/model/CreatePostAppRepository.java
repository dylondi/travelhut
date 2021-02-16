package com.example.travelhut.model;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.travelhut.views.main.profile.CreatePostActivity;
import com.example.travelhut.views.main.profile.ProfileActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.HashMap;

public class CreatePostAppRepository extends LiveData<DataSnapshot> {

    public CreatePostAppRepository() {
    }


    public void uploadImage(Uri imageUri, String fileExtension, Application application, ProgressDialog progressDialog, String description){



        //Log.d(TAG, "uploadImage: file extension: " + getFileExtension(this, imageUri));
        if(imageUri != null){
           //Log.d(TAG, "uploadImage: imageUri: " + imageUri.toString());
//            StorageReference fileRef = storageReference.child(System.currentTimeMillis()
//            + "." + getFileExtension(imageUri));

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(StringsRepository.POSTS);

            StorageReference fileRef = storageReference.child(System.currentTimeMillis()
                    + "." + fileExtension);




            StorageTask uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isComplete()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String myUrl = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(StringsRepository.POSTS_CAP);

                        String postId = reference.push().getKey();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put(StringsRepository.POST_ID, postId);
                        hashMap.put(StringsRepository.POST_IMAGE, myUrl);
                        hashMap.put(StringsRepository.DESCRIPTION, description);
                        hashMap.put(StringsRepository.PUBLISHER, FirebaseAuth.getInstance().getCurrentUser().getUid());

                        reference.child(postId).setValue(hashMap);

                        progressDialog.dismiss();

                        application.startActivity(new Intent(application, ProfileActivity.class));
                        //application.finish();
                    }else{
                        Toast.makeText(application, StringsRepository.FAILED_CAP, Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(application, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(application, StringsRepository.NO_IMAGE_SELECTED, Toast.LENGTH_SHORT).show();

        }
    }
}
