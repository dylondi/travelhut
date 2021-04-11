package com.example.travelhut.views.main.map_search;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProviders;

import com.example.travelhut.R;
import com.example.travelhut.model.utils.StringsRepository;
import com.example.travelhut.viewmodel.main.map_search.NewTripActivityViewModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class NewTripActivity extends AppCompatActivity {

    //Instance Variables
    private static final String TAG = "NewTripActivity";
    private final Place[] placeArray = new Place[1];
    private String placeid, apiKey;
    private Button chooseDatesButton;
    private TextView startDate;
    private ImageView newTripBackButton, newTripCheckButton;
    private MaterialDatePicker.Builder<Pair<Long, Long>> builder;
    private AutocompleteSupportFragment autocompleteFragment;
    private PlacesClient placesClient;
    private NewTripActivityViewModel newTripActivityViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);

        newTripActivityViewModel = ViewModelProviders.of(this).get(NewTripActivityViewModel.class);

        initVariables();
        initPlacesAndPlacesClient();
        configAutoCompleteFragment();

        final MaterialDatePicker materialDatePicker = builder.build();
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);

        if(getIntent().getStringExtra(StringsRepository.PLACE_ID)!=null) {
            placeid = getIntent().getStringExtra(StringsRepository.PLACE_ID);
            final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeid, placeFields);

            //Fetch place with PlacesClient and given request
            placesClient.fetchPlace(request).addOnSuccessListener((response) -> {

                //Get place object from response
                Place place = response.getPlace();
                placeArray[0] = place;

                //Set text of autocompleteFragment to selected place's name
                autocompleteFragment.setText(place.getName());
                Log.i(TAG, "Place found: " + place.getName());
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    Log.e(TAG, "Place not found: " + exception.getMessage());
                }
            });
        }
        //Set OnClickListener to newTripCheckButton
        newTripCheckButton.setOnClickListener(v -> {

            //Get selected dates and store in a Pair object
            Pair selectedDates = (Pair) materialDatePicker.getSelection();

            //Call ViewModel to create new trip in the database
            newTripActivityViewModel.createTrip(selectedDates, placeArray[0], startDate.getText().toString());

            //Finish activity
            finish();
        });

        //Set OnClickListener to chooseDatesButton
        chooseDatesButton.setOnClickListener(v -> materialDatePicker.show(getSupportFragmentManager(), StringsRepository.DATE_PICKER));

        materialDatePicker.addOnPositiveButtonClickListener(selection -> startDate.setText(materialDatePicker.getHeaderText()));

        //Set OnClickListener to newTripBackButton
        newTripBackButton.setOnClickListener(v -> finish());

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                if (place.getName() != null || !place.getName().equals(StringsRepository.EMPTY_STRING)) {
                    placeArray[0] = place;
                }
            }

            @Override
            public void onError(@NotNull Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    private void configAutoCompleteFragment() {
        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.PHOTO_METADATAS, Place.Field.ADDRESS));

        //Set location bias
        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(
                new LatLng(-33.880490, 151.184363),
                new LatLng(-33.858754, 151.229596)
        ));
    }

    //This method initializes objects
    private void initVariables() {
        chooseDatesButton = findViewById(R.id.choose_dates_button);
        startDate = findViewById(R.id.start_date);
        newTripBackButton = findViewById(R.id.new_trip_back_arrow);
        newTripCheckButton = findViewById(R.id.new_trip_check);
        apiKey = getString(R.string.google_api_key);
        builder = MaterialDatePicker.Builder.dateRangePicker();
        autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
    }

    //This method initializes the Places and PlacesClient Objects
    private void initPlacesAndPlacesClient() {
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        placesClient = Places.createClient(this);
    }
}