package com.example.travelhut.views.main.newsfeed.newsfeed;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.travelhut.R;
import com.example.travelhut.model.utils.StringsRepository;
import com.example.travelhut.viewmodel.main.newsfeed.newsfeed.UploadStoryActivityViewModel;
import com.example.travelhut.views.main.newsfeed.NewsFeedActivity;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;

public class UploadStoryActivity extends AppCompatActivity {

    //Instance Variables
    private Uri imageUri;
    private UploadStoryActivityViewModel uploadStoryActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story);

        //Initialize ViewModel
        uploadStoryActivityViewModel = ViewModelProviders.of(this).get(UploadStoryActivityViewModel.class);

        //Start CropImage Activity
        CropImage.activity().setAspectRatio(11, 16).start(this);
    }

    public String getFileExtension(Context context, Uri uri) {
        String extension;
        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
        }
        return extension;
    }

    //This method calls ViewModel's publishStory method
    private void publishStory() {

        //Create ProgressDialog to display while posting
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(StringsRepository.POSTING_CAP);
        progressDialog.show();

        //Call ViewModel to publish story
        uploadStoryActivityViewModel.publishStory(imageUri, getFileExtension(this, imageUri));

        //Observe boolean from ViewModel representing if the story was published successfully
        uploadStoryActivityViewModel.getImageUploadedMutableLiveData().observe(this, imageUploaded -> {
            if (imageUploaded) {

                //Dismiss dialog and create new intent to navigate to news feed
                progressDialog.dismiss();
                Intent newsFeedIntent = new Intent(UploadStoryActivity.this, NewsFeedActivity.class);
                newsFeedIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(newsFeedIntent);
            } else {
                //Observe image upload failed message from ViewModel and display in toast if message is not empty
                uploadStoryActivityViewModel.getUploadFailedMessage().observe(this, message -> {
                    if (!message.isEmpty())
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //If requestCode and resultCode are valid
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            //Get activity result and initialize imageUri
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            //publish story
            publishStory();
        } else {

            //Display toast to user with message informing something has gone wrong, then start newsfeed activity and finish current activity
            Toast.makeText(this, "Something gone wrong", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UploadStoryActivity.this, NewsFeedActivity.class));
            finish();
        }
    }
}