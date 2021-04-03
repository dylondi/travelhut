package com.example.travelhut.views.main.trips.trip_fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelhut.R;
import com.example.travelhut.model.utils.StringsRepository;
import com.example.travelhut.model.objects.Trip;
import com.example.travelhut.views.main.trips.TripActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.ViewHolder> {


    //Instance Variables
    private static final String TAG = "TripsAdapter";
    private Context mContext;
    private List<Trip> trips;
    private PlacesClient placesClient;

    //Constructor
    public TripsAdapter(Context mContext, List<Trip> trips) {
        this.mContext = mContext;
        this.trips = trips;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.trip_view, parent, false);
        return new TripsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripsAdapter.ViewHolder holder, int position) {

        //Get current Trip
        final Trip trip = trips.get(position);

        //placeId and apiKey
        String placeId = trip.getPlaceid();
        String apiKey = mContext.getString(R.string.google_api_key);

        initPlacesAndPlacesClient(apiKey);
        getTripInfo(holder, FirebaseAuth.getInstance().getCurrentUser().getUid(), trip.getTripid());

        final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.PHOTO_METADATAS);
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        //Send request to placesClient
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();

            setPlaceImage(holder, placesClient, place);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                Log.e(TAG, "Place not found: " + exception.getMessage());
            }
        });

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(mContext, TripActivity.class);
            intent.putExtra(StringsRepository.TRIP_ID, trip.getTripid());
            intent.putExtra(StringsRepository.PLACE_ID, placeId);
            mContext.startActivity(intent);
        });
    }

    private void initPlacesAndPlacesClient(String apiKey) {
        if (!Places.isInitialized()) {
            Places.initialize(mContext, apiKey);
        }
        placesClient = Places.createClient(mContext);
    }

    public void setPlaceImage(ViewHolder viewHolder, PlacesClient placesClient, Place place) {
        final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
        if (metadata == null || metadata.isEmpty()) {
            return;
        }
        final PhotoMetadata photoMetadata = metadata.get(0);

        // Create a FetchPhotoRequest.
        final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                .setMaxWidth(500)
                .setMaxHeight(300)
                .build();
        placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
            Bitmap bitmap = fetchPhotoResponse.getBitmap();
            viewHolder.tripImage.setImageBitmap(bitmap);
            viewHolder.tripImage.setAdjustViewBounds(true);
            viewHolder.tripImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                Log.e(TAG, "Place not found: " + exception.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //Instance Variables
        public ImageView tripImage;
        public TextView placeName, placeAddress, date;

        //Constructor
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tripImage = itemView.findViewById(R.id.trip_place_image_view);
            placeName = itemView.findViewById(R.id.trip_item_place_name);
            placeAddress = itemView.findViewById(R.id.trip_item_place_address);
            date = itemView.findViewById(R.id.trip_item_date);
        }
    }

    //This method info of current trip
    private void getTripInfo(final ViewHolder viewHolder, String publisherId, String tripId) {

        //DatabaseReference of current trip
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(StringsRepository.TRIPS_CAP).child(publisherId).child(tripId);

        //SingleValueEventListener for DatabaseReference
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Get Trip object from snapshot
                Trip trip = snapshot.getValue(Trip.class);

                //Set TextViews text with info
                viewHolder.placeName.setText(trip.getPlacename());
                viewHolder.placeAddress.setText(trip.getPlaceaddress());
                viewHolder.date.setText(trip.getDaterange());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
