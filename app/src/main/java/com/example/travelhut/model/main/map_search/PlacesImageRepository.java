package com.example.travelhut.model.main.map_search;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.List;

public class PlacesImageRepository extends LiveData<Bitmap> {

    private MutableLiveData<Bitmap> bitmapMutableLiveData;
    private static final String TAG = "PlacesImageRepository";

    public PlacesImageRepository() {
        bitmapMutableLiveData = new MutableLiveData<>();
    }

    public void sendImageRequest(PlacesClient placesClient, Place place){
        //Declare and Initialize a list of PhotoMetaData Objects containing the current place's photo metadata info
        final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
        if (metadata == null || metadata.isEmpty()) {
            return;
        }

        //Get first object of PhotoMetaData List
        final PhotoMetadata photoMetadata = metadata.get(0);

        // Create a FetchPhotoRequest
        final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                .setMaxWidth(500)
                .setMaxHeight(300)
                .build();
        placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {

            //Get bitmap from response and set ImageView to this bitmap and configure viewBounds & scaleType
            bitmapMutableLiveData.postValue(fetchPhotoResponse.getBitmap());


        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                Log.e(TAG, "Place not found: " + exception.getMessage());
            }
        });
    }

    public MutableLiveData<Bitmap> getBitmapMutableLiveData(){
        return bitmapMutableLiveData;
    }
}
