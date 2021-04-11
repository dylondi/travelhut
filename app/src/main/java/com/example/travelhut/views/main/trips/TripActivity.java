package com.example.travelhut.views.main.trips;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.example.travelhut.R;
import com.example.travelhut.model.utils.StringsRepository;
import com.example.travelhut.viewmodel.main.trips.TripActivityViewModel;
import com.example.travelhut.viewmodel.main.trips.TripsActivityViewModelFactory;
import com.example.travelhut.model.objects.Event;
import com.example.travelhut.views.main.map_search.utils.EventAdapter;
import com.example.travelhut.model.objects.Trip;
import com.example.travelhut.model.objects.CovidStatistics;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TripActivity extends AppCompatActivity {

    //Instance Variables
    private static final String TAG = "TripActivity";
    private TextView placeName, placeAddress, dateRange, recommendation, description, masks, quarantine, tests;
    private ImageView placeImage, backArrow;
    private Button changeDates;
    private List<Event> eventsList = new ArrayList<>();
    private List<DataEntry> data = new ArrayList<>();
    private EventAdapter eventAdapter;
    private RecyclerView eventsRecyclerView;
    private Cartesian cartesian;
    private AnyChartView anyChartView;
    private TripActivityViewModel tripActivityViewModel;
    private PlacesClient placesClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        String apiKey = getString(R.string.google_api_key);

        //Get string extras from intent
        Bundle extras = getIntent().getExtras();
        String tripId = extras.getString(StringsRepository.TRIP_ID, null);
        String placeId = extras.getString(StringsRepository.PLACE_ID, null);

        tripActivityViewModel = ViewModelProviders.of(this, new TripsActivityViewModelFactory(this.getApplication(), tripId)).get(TripActivityViewModel.class);

        initViews();
        initPlacesAndPlacesClient(apiKey);

        configRecyclerView();

        cartesian = AnyChart.column();

        //Setup MaterialDatePicker
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();

        final MaterialDatePicker materialDatePicker = builder.build();

        //Get LiveData object from ViewModel
        LiveData<DataSnapshot> liveData = tripActivityViewModel.getTripInfo();

        //Observe LiveData object from ViewModel
        liveData.observe(this, dataSnapshot -> {

            //Get Trip object from data from dataSnapshot
            Trip trip = dataSnapshot.getValue(Trip.class);

            //Set text of Views
            placeName.setText(trip.getPlacename());
            placeAddress.setText(trip.getPlaceaddress());
            dateRange.setText(trip.getDaterange());

        });

        //Set OnClickListener for changeDates button
        changeDates.setOnClickListener(v -> materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER"));

        materialDatePicker.addOnPositiveButtonClickListener(selection -> {

            //DatabaseReference to current trip
            DatabaseReference tripsReference = FirebaseDatabase.getInstance().getReference(StringsRepository.TRIPS_CAP).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(tripId);

            //Obtain the startDate & endDate from the range selected
            Pair selectedDates = (Pair) materialDatePicker.getSelection();


            //Create HashMap of updated date values
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(StringsRepository.DATERANGE, materialDatePicker.getHeaderText());
            hashMap.put(StringsRepository.STARTDATE, selectedDates.first);
            hashMap.put(StringsRepository.ENDDATE, selectedDates.second);

            //Update reference with hashMap
            tripsReference.updateChildren(hashMap);

            //Set text of view displaying dates
            dateRange.setText(materialDatePicker.getHeaderText());

        });


        //Set Fields of data to be retrieved from the Google Places API
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.PHOTO_METADATAS);

        //Create a FetchPlaceRequest with placeId and placeFields
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        //Use placesClient to fetch the Place requested with an OnSuccessListener and an OnFailureListener
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {

            //Get Place object
            Place place = response.getPlace();

            //Set placeImage from Place object
            setPlaceImage(placesClient, place);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                Log.e(TAG, "Place not found: " + exception.getMessage());
            }
        });

        //Set OnClickListener for backArrow which finishes the activity
        backArrow.setOnClickListener(v -> finish());

        loadEvents();
        try {
            loadCoronaVirusStats();
            loadCoronaVirusStatsChart();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //This method initializes the Places and PlacesClient Objects
    private void initPlacesAndPlacesClient(String apiKey) {
        if (!Places.isInitialized()) {
            Places.initialize(this, apiKey);
        }
        placesClient = Places.createClient(this);
    }

    private void configRecyclerView() {
        eventAdapter = new EventAdapter(this, eventsList);
        eventsRecyclerView.setHasFixedSize(true);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        eventsRecyclerView.setAdapter(eventAdapter);
    }

    //This method initializes view objects
    private void initViews() {
        placeName = findViewById(R.id.trip_place_name);
        placeAddress = findViewById(R.id.trip_place_address);
        dateRange = findViewById(R.id.trip_date);
        placeImage = findViewById(R.id.trip_image_view);
        backArrow = findViewById(R.id.trip_back_arrow);
        changeDates = findViewById(R.id.change_dates_button);
        recommendation = findViewById(R.id.recommendation_text);
        description = findViewById(R.id.description_text);
        masks = findViewById(R.id.mask_info);
        quarantine = findViewById(R.id.quarantine_info);
        tests = findViewById(R.id.test_info);
        eventsRecyclerView = findViewById(R.id.trip_events_recycler_view);
        anyChartView = findViewById(R.id.any_chart_view);
    }


    public void setPlaceImage(PlacesClient placesClient, Place place) {

        //List of PhotoMetaData from the place selected
        final List<PhotoMetadata> metadata = place.getPhotoMetadatas();

        if (metadata == null || metadata.isEmpty()) {
            return;
        }

        //Get first PhotoMetData object from metadata list
        final PhotoMetadata photoMetadata = metadata.get(0);

        // Create a FetchPhotoRequest using the photoMetaData object
        final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                .setMaxWidth(500)
                .setMaxHeight(300)
                .build();

        //Use placesClient to fetch the photo from the photoRequest
        placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {

            //Get Bitmap from response
            Bitmap bitMap = fetchPhotoResponse.getBitmap();

            //Set bitMap to placeImage View and config placeImage view
            placeImage.setImageBitmap(bitMap);
            placeImage.setAdjustViewBounds(true);
            placeImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                Log.e(TAG, "Place not found: " + exception.getMessage());
            }
        });
    }

    //This method loads events from the ViewModel
    private void loadEvents() {

        //Get LiveDat object from ViewModel
        LiveData<List<Event>> liveData = tripActivityViewModel.getEventsList();

        //Observe liveData object
        liveData.observe(this, events -> {

            //Clear list, add the updated data, and notify the eventAdapter of the updated data set
            eventsList.clear();
            eventsList.addAll(events);
            eventAdapter.notifyDataSetChanged();

        });
    }

    //This method loads covid-19 stats
    public void loadCoronaVirusStats() throws Exception {

        //Get LiveData object from ViewModel
        LiveData<CovidStatistics> liveData = tripActivityViewModel.getCovidStats();

        //Observe liveData object
        liveData.observe(this, covidStatistics -> {

            //Set text of all views with updated covid-19 stats
            recommendation.setText(covidStatistics.getRecommendation());
            description.setText(covidStatistics.getDescription());
            masks.setText(covidStatistics.getMasks());
            quarantine.setText(covidStatistics.getQuarantine());
            tests.setText(covidStatistics.getTests());

        });
    }


    public void loadCoronaVirusStatsChart() {

        //Get LiveData object from ViewModel
        LiveData<List<DataEntry>> liveData = tripActivityViewModel.getCovidGraphStats();

        //Observe liveData object which is a list of DataEntry objects, these objects are used to create the graph
        liveData.observe(this, covidStatistics -> {

            //Clear list
            data.clear();

            //Add all stats the data list
            data.addAll(covidStatistics);

            //Create Column object with our data list
            Column column = cartesian.column(data);

            //Configure column
            column.tooltip()
                    .titleFormat("{%X}")
                    .position(Position.CENTER_BOTTOM)
                    .anchor(Anchor.CENTER_BOTTOM)
                    .offsetX(0d)
                    .offsetY(5d)
                    .format("{%Value}{groupsSeparator: }");

            //Configure cartesian
            cartesian.animation(true);
            cartesian.title("Covid-19 cases in previous 7 days");
            cartesian.yScale().minimum(0d);
            cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");
            cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
            cartesian.interactivity().hoverMode(HoverMode.BY_X);
            cartesian.xAxis(0).title("Date");
            cartesian.yAxis(0).title("New Cases");

            //Set anyChartView to cartesian chart
            anyChartView.setChart(cartesian);
        });
    }
}