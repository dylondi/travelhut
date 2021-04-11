package com.example.travelhut.model.main.map_search;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.List;

public class PlacesImageRepository extends LiveData<Bitmap> {

    //Instance Variables
    private static final String TAG = "PlacesImageRepository";
    private MutableLiveData<Bitmap> bitmapMutableLiveData;

    //Constructor
    public PlacesImageRepository() {
        bitmapMutableLiveData = new MutableLiveData<>();
    }

    //This method sends an image request to the Google Places API -> posts Bitmap to MutableLiveData
    public void sendImageRequest(PlacesClient placesClient, Place place){
        //Declare and Initialize a list of PhotoMetaData Objects containing the current place's photo metadata info
        final List<PhotoMetadata> metadata = place.getPhotoMetadatas();

        //Check for null or empty list
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

            //Fetch photo request and attach an onSuccessListener and an onFailureListener
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {

            //Get bitmap from response and post it to bitmapMutableLiveData
            bitmapMutableLiveData.postValue(fetchPhotoResponse.getBitmap());

        }).addOnFailureListener((exception) -> {

            //If ApiException -> log place not found
            if (exception instanceof ApiException) {
                Log.e(TAG, "Place not found: " + exception.getMessage());
            }
        });
    }

    //This method returns bitmapMutableLiveData, which is a MutableLiveData Object of type Bitmap
    public MutableLiveData<Bitmap> getBitmapMutableLiveData(){
        return bitmapMutableLiveData;
    }
}
