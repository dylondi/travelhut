package com.example.travelhut.views.main.trips.trip_fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travelhut.R;
import com.example.travelhut.viewmodel.main.profile.toolbar.NotificationAdapterViewModel;
import com.example.travelhut.views.main.newsfeed.newsfeed.utils.Post;
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

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.ViewHolder>{


    private Context mContext;
    private List<Trip> trips;
    private NotificationAdapterViewModel notificationAdapterViewModel;
    private Place[] places = new Place[1];
    private static final String TAG = "TripsAdapter";

    public TripsAdapter(Context mContext, List<Trip> trips) {
        this.mContext = mContext;
        this.trips = trips;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.trip_item, parent, false);
        return new TripsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripsAdapter.ViewHolder holder, int position) {

        final Trip trip = trips.get(position);
        //holder.placeName.setText(trip.getPlacename());

        getTripInfo(holder, FirebaseAuth.getInstance().getCurrentUser().getUid(), trip.getTripid());
        String placeid = trip.getPlaceid();

        String apiKey = mContext.getString(R.string.google_api_key);

        final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.PHOTO_METADATAS);

        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeid, placeFields);
        if (!Places.isInitialized()) {
            Places.initialize(mContext, apiKey);
        }
        PlacesClient placesClient = Places.createClient(mContext);
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();

            setPlaceImage(holder, placesClient, place);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + exception.getMessage());
                final int statusCode = apiException.getStatusCode();
                // TODO: Handle error with given status code.
            }
        });

//        if (places[0].getPhotoMetadatas() != null) {
//            final List<PhotoMetadata> metadata = places[0].getPhotoMetadatas();
//            if (metadata == null || metadata.isEmpty()) {
//                Log.w(TAG, "No photo metadata.");
//                return;
//            }
//            final PhotoMetadata photoMetadata = metadata.get(0);
//
//            // Get the attribution text.
//            final String attributions = photoMetadata.getAttributions();
//
//            // Create a FetchPhotoRequest.
//            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
//                    .setMaxWidth(500) // Optional.
//                    .setMaxHeight(300) // Optional.
//                    .build();
//            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
//                Bitmap bitmap = fetchPhotoResponse.getBitmap();
//                holder.tripImage.setImageBitmap(bitmap);
//                holder.tripImage.setAdjustViewBounds(true);
//                holder.tripImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                //placeImage.setScaleType(ImageView.ScaleType.FIT_XY);
//
//            }).addOnFailureListener((exception) -> {
//                if (exception instanceof ApiException) {
//                    final ApiException apiException = (ApiException) exception;
//                    Log.e(TAG, "Place not found: " + exception.getMessage());
//                    final int statusCode = apiException.getStatusCode();
//                    // TODO: Handle error with given status code.
//                }
//            });
//        }
    }

    public void setPlaceImage(ViewHolder viewHolder, PlacesClient placesClient, Place place){
        final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
        if (metadata == null || metadata.isEmpty()) {
            Log.w(TAG, "No photo metadata.");
            return;
        }
        final PhotoMetadata photoMetadata = metadata.get(0);

        // Get the attribution text.
        final String attributions = photoMetadata.getAttributions();

        // Create a FetchPhotoRequest.
        final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                .setMaxWidth(500) // Optional.
                .setMaxHeight(300) // Optional.
                .build();
        placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
            Bitmap bitmap = fetchPhotoResponse.getBitmap();
            viewHolder.tripImage.setImageBitmap(bitmap);
            viewHolder.tripImage.setAdjustViewBounds(true);
            viewHolder.tripImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //placeImage.setScaleType(ImageView.ScaleType.FIT_XY);

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + exception.getMessage());
                final int statusCode = apiException.getStatusCode();
                // TODO: Handle error with given status code.
            }
        });
    }
//        if(notification.isIspost()){
//            holder.tripImage.setVisibility(View.VISIBLE);
//            getPostImage(holder.tripImage, notification.getPostid());
//        }else{
//            holder.tripImage.setVisibility(View.GONE);
//        }


//        holder.tripImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
//                editor.putString("postid", notification.getPostid());
//                editor.apply();
//
//                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.notifications_frame_layout, new SinglePostFragment()).commit();
//            }
//        });

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(notification.isIspost()){
//                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
//                    editor.putString("postid", notification.getPostid());
//                    editor.apply();
//
//                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fr)
//                }
//            }
//        });
//    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        public ImageView tripImage;
        public TextView placeName, placeAddress, date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            tripImage = itemView.findViewById(R.id.trip_place_image_view);
            placeName = itemView.findViewById(R.id.trip_place_name);
            placeAddress = itemView.findViewById(R.id.trip_place_address);
            date = itemView.findViewById(R.id.trip_date);
        }
    }

    private void getTripInfo(final ViewHolder viewHolder, String publisherid, String tripId){

//        notificationAdapterViewModel = new NotificationAdapterViewModel(publisherid);
//
//        LiveData<DataSnapshot> liveData = notificationAdapterViewModel.getFollowingSnapshot();
//
//        liveData.observe(, dataSnapshot -> {});
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Trips").child(publisherid).child(tripId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Trip trip = snapshot.getValue(Trip.class);
                //Glide.with(mContext).load(user.getImageurl()).into(imageView);
                viewHolder.placeName.setText(trip.getPlacename());
                viewHolder.placeAddress.setText(trip.getPlaceaddress());
                viewHolder.date.setText(trip.getDaterange());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getPostImage(final ImageView imagePost, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                Glide.with(mContext).load(post.getPostimage()).into(imagePost);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
