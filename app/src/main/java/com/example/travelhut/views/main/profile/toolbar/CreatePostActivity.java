package com.example.travelhut.views.main.profile.toolbar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.travelhut.R;
import com.example.travelhut.model.utils.StringsRepository;
import com.example.travelhut.viewmodel.main.profile.CreatePostActivityViewModel;
import com.example.travelhut.views.main.newsfeed.NewsFeedActivity;
import com.example.travelhut.views.main.newsfeed.newsfeed.UploadStoryActivity;
import com.example.travelhut.views.main.profile.ProfileActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class CreatePostActivity extends AppCompatActivity {

    //Instance Variables
    private Uri imageUri;
    private CreatePostActivityViewModel createPostActivityViewModel;
    private ImageView close, imageAdded, post;
    private EditText description;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        initObjects();
        createPostActivityViewModel = ViewModelProviders.of(this).get(CreatePostActivityViewModel.class);

        //Discard post
        close.setOnClickListener(v -> {
            //Go to profile page
            startActivity(new Intent(CreatePostActivity.this, ProfileActivity.class));
        });

        //Confirm post
        post.setOnClickListener(v -> {
            //Upload post
            uploadImage();
        });

        //Sets aspect ratio of post to make sure it is square image
        CropImage.activity()
                .setAspectRatio(1, 1)
                .start(CreatePostActivity.this);



    }

    //Initialize Views
    private void initObjects() {
        close = findViewById(R.id.create_post_back_arrow);
        imageAdded = findViewById(R.id.image_posted);
        post = findViewById(R.id.create_post_check);
        description = findViewById(R.id.description);
    }


    //Gets file extension of Uri
    public static String getFileExtension(Context context, Uri uri) {
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

    //This method calls the upload image in the ViewModel to upload the post
    private void uploadImage(){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(StringsRepository.POSTING_CAP);
        progressDialog.show();

        createPostActivityViewModel.uploadImage(imageUri, getFileExtension(this,imageUri), description.getText().toString());

        //Observe boolean from ViewModel representing if the story was published successfully
        createPostActivityViewModel.getImageUploadedMutableLiveData().observe(this, imageUploaded -> {
            if (imageUploaded) {

                //Dismiss dialog and create new intent to navigate to news feed
                progressDialog.dismiss();

                //Create and start intent to navigate to the ProfileActivity upon successful task completion
                Intent profileIntent = new Intent(this, ProfileActivity.class);
                profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(profileIntent);
            } else {
                //Observe image upload failed message from ViewModel and display in toast if message is not empty
                createPostActivityViewModel.getUploadFailedMessage().observe(this, message -> {
                    if (!message.isEmpty())
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                });
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            imageAdded.setImageURI(imageUri);
        }else{
            Toast.makeText(this, StringsRepository.SOMETHING_GONE_WRONG, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(CreatePostActivity.this, ProfileActivity.class));
            finish();
        }
    }
}
