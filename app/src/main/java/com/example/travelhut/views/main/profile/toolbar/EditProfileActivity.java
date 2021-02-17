package com.example.travelhut.views.main.profile.toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.travelhut.R;
import com.example.travelhut.viewmodel.main.profile.EditProfileActivityViewModel;
import com.example.travelhut.views.authentication.utils.User;
import com.example.travelhut.views.main.profile.ProfileActivity;
import com.google.firebase.database.DataSnapshot;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

public class EditProfileActivity extends AppCompatActivity {
    private ImageView editProfileImage, save;
    private TextView changeProfilePic;
    MaterialEditText displayName, username, bio, url;
    private Uri imageUri;
    private EditProfileActivityViewModel editProfileActivityViewModel;

    private static final String TAG = "EditProfileActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //assigning views
        editProfileActivityViewModel = ViewModelProviders.of(this).get(EditProfileActivityViewModel.class);
        editProfileImage = findViewById(R.id.edit_profile_image);
        save = findViewById(R.id.check);
        changeProfilePic = findViewById(R.id.change_profile_image);
        displayName = findViewById(R.id.display_name);
        username = findViewById(R.id.username_edit_profile);
        bio = findViewById(R.id.bio_edit_profile);
        url = findViewById(R.id.url_edit_profile);

        userInfo();


        //OnClickListener for change profile text
        changeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(EditProfileActivity.this);
            }
        });

        //save profile changes OnClickListener
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(displayName.getText().toString(),
                        username.getText().toString(),
                        bio.getText().toString(),
                        url.getText().toString());

                startActivity(new Intent(EditProfileActivity.this, ProfileActivity.class));
            }
        });

        //edit profile image OnClickListener
        editProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(EditProfileActivity.this);
            }
        });




        //assign back arrow view and set OnClickListener
        ImageView backArrow = findViewById(R.id.edit_profile_back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: intent back to ProfileActivity.");
                finish();
            }
        });
    }

    //updates profile
    private void updateProfile(String displayName, String username, String bio, String url){
        editProfileActivityViewModel.updateProfile(displayName, username, bio, url);
    }

    //gets file extension from Uri
    public String getFileExtension(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }




//uploads image to ViewModel
    private void uploadImage(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading");
        progressDialog.show();

        editProfileActivityViewModel.uploadImage(imageUri,getFileExtension(this, imageUri),progressDialog);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            uploadImage();
        }else{
            Toast.makeText(this, "Something gone wrong", Toast.LENGTH_SHORT).show();
        }
    }

    //retrieves current user info and sets all views to display current data
    private void userInfo(){
        LiveData<DataSnapshot> liveData = editProfileActivityViewModel.getDataSnapshotLiveData();

        liveData.observe(this, dataSnapshot -> {

            if(dataSnapshot!=null) {
                User user = dataSnapshot.getValue(User.class);
                Log.d(TAG, "userInfo: imageuri: " + user.getImageurl());
                Glide.with(getApplicationContext()).load(user.getImageurl()).into(editProfileImage);
                displayName.setText(user.getDisplayname());
                username.setText(user.getUsername());
                bio.setText(user.getBio());
                url.setText(user.getUrl());


            }
        });

    }
}