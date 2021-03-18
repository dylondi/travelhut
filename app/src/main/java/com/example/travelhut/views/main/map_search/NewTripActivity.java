package com.example.travelhut.views.main.map_search;

import android.graphics.Bitmap;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.example.travelhut.R;
import com.example.travelhut.model.StringsRepository;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class NewTripActivity extends AppCompatActivity {

    private String placeid;
    private static final String TAG = "NewTripActivity";
    private Button chooseDatesButton;
    private TextView startDate,endDate;
    private ImageView newTripBackButton, newTripCheckButton;
    final Place[] place2 = new Place[1];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);

        chooseDatesButton = findViewById(R.id.choose_dates_button);
        startDate = findViewById(R.id.start_date);
        newTripBackButton = findViewById(R.id.new_trip_back_arrow);
        newTripCheckButton = findViewById(R.id.new_trip_check);


        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();

        final MaterialDatePicker materialDatePicker = builder.build();

        chooseDatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
               startDate.setText(materialDatePicker.getHeaderText());
            }
        });

        newTripBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




        placeid = getIntent().getStringExtra("placeid");

        String apiKey = getString(R.string.google_api_key);
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);

        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeid, placeFields);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);


        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(
                new LatLng(-33.880490, 151.184363),
                new LatLng(-33.858754, 151.229596)
        ));

        PlacesClient placesClient = Places.createClient(this);
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            place2[0] = place;
            autocompleteFragment.setText(place.getName());
            Log.i(TAG, "Place found: " + place.getName());
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + exception.getMessage());
                final int statusCode = apiException.getStatusCode();
                // TODO: Handle error with given status code.
            }
        });
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        newTripCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference  tripsReference = FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                //Retrieve the database reference to the current users stories


                //Retrieve story key for story to be uploaded
                String tripId = tripsReference.push().getKey();


                Pair selectedDates = (Pair) materialDatePicker.getSelection();
//              then obtain the startDate & endDate from the range
                final Pair<Date, Date> rangeDate = new Pair<>(new Date((Long) selectedDates.first), new Date((Long) selectedDates.second));
//              assigned variables
                Date startDate1 = rangeDate.first;
                Date endDate = rangeDate.second;
                Log.i(TAG, "onClick: startDate: " + startDate1 + " , endDate: " + endDate   );
//              Format the dates in ur desired display mode
                SimpleDateFormat simpleFormat = new SimpleDateFormat("dd MMM yyyy");
//              Display it by setText
                //datedisplay.setText("SELECTED DATE : " +  simpleFormat.format(startDate) + " Second : " + simpleFormat.format(endDate));

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("tripid", tripId);
                hashMap.put("placeid", place2[0].getId());
                hashMap.put("placename", place2[0].getName());
                hashMap.put("placeaddress", place2[0].getAddress());
                hashMap.put("daterange", startDate.getText().toString());
                hashMap.put("startdate", (Long) selectedDates.first);
                hashMap.put("enddate", (Long) selectedDates.second);

                tripsReference.child(tripId).setValue(hashMap);
                finish();
            }
        });


        // Initialize the AutocompleteSupportFragment.

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.PHOTO_METADATAS, Place.Field.ADDRESS));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                // TODO: Get info about the selected place.



                if (place.getName() != null || !place.getName().equals("")) {


                }
            }


            @Override
            public void onError(@NotNull Status status) {
                // TODO: Handle the error.
               // Log.i(TAG, "An error occurred: " + status);
            }
        });

    }
}