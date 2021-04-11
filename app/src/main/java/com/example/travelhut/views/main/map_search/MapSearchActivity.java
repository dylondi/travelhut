package com.example.travelhut.views.main.map_search;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.bumptech.glide.Glide;
import com.example.travelhut.R;
import com.example.travelhut.model.utils.StringsRepository;
import com.example.travelhut.model.objects.Event;
import com.example.travelhut.views.main.map_search.utils.EventAdapter;
import com.example.travelhut.views.utils.BottomNavigationViewHelper;
import com.example.travelhut.viewmodel.main.map_search.MapSearchActivityViewModel;
import com.example.travelhut.model.objects.CovidStatistics;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.annotations.NotNull;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapSearchActivity extends AppCompatActivity implements OnMapReadyCallback {

    //Instance Variables
    private static final String TAG = "MapSearchActivity";
    private static final int ACTIVITY_NUM = 0;
    static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 23;
    public static final double OFFSET = 0.007;
    private GoogleMap mMap;
    private SupportMapFragment supportMapFragment;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Marker currentMarker;
    private TextView placeName, placeAddress, recommendation, description, masks, quarantine, tests, tempText, humidityText, timeText;
    private RelativeLayout relativeLayout;
    private ImageView placeImage, weatherIcon;
    private String placeId;
    private List<Event> eventsList = new ArrayList<>();
    private EventAdapter eventAdapter;
    private RecyclerView eventsRecyclerView;
    private List<DataEntry> data = new ArrayList<>();
    private Cartesian cartesian;
    private AnyChartView anyChartView;
    private Button newTripButton;
    private String apiKey;
    private AutocompleteSupportFragment autocompleteFragment;
    private PlacesClient placesClient;
    private MapSearchActivityViewModel mapSearchActivityViewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_search);
        Log.d(TAG, "onCreate: started.");

        //Google API key
        apiKey = getString(R.string.google_api_key);

        //Initialize ViewModel
        mapSearchActivityViewModel = ViewModelProviders.of(this).get(MapSearchActivityViewModel.class);

        initializeViews();
        configViews();
        setupBottomNavigationView();
        checkPermission();
        initPlacesAndPlacesClient();
        configAutoCompleteFragment();


        //Set up a PlaceSelectionListener
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());

                //If a marker exists, remove it
                if (currentMarker != null) {
                    currentMarker.remove();
                }

                //If selected place has name
                if (place.getName() != null || !place.getName().equals("")) {

                    //Init placeId
                    placeId = place.getId();
                    String placeNameString = place.getName();

                    //Move current marker to new location and create LatLng Object for camera animation
                    currentMarker = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName()));
                    LatLng latLng = new LatLng(place.getLatLng().latitude - OFFSET, place.getLatLng().longitude);

                    //Configure place layout
                    relativeLayout.setVisibility(View.VISIBLE);
                    relativeLayout.setClickable(true);
                    placeName.setText(placeNameString);
                    placeAddress.setText(place.getAddress());

                    //Load weather data and update views to display data
                    loadWeather(place.getLatLng().latitude, place.getLatLng().longitude);

                    //Load event data and update views to display data
                    loadEvents(placeNameString);

                    try {

                        //Load covid data and update views to display data
                        loadCoronaVirusStats(placeNameString);
                        loadCoronaVirusStatsChart(placeNameString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //Move camera to new location
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                    //Send PlaceImageRequest to ViewModel
                    mapSearchActivityViewModel.sendImageRequest(placesClient, place);

                    //Observe bitmap Image from ViewModel
                    getPlaceImage();

                }
            }

            @Override
            public void onError(@NotNull Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        //Set OnClickListener for floating action button
        newTripButton.setOnClickListener(v -> {

            //Create Intent to navigate to NewTripActivity
            Intent intent = new Intent(MapSearchActivity.this, NewTripActivity.class);

            //Put placeId as extra data within intent for NewTripActivity to receive
            intent.putExtra(StringsRepository.PLACE_ID, placeId);

            startActivity(intent);
        });
        supportMapFragment.getMapAsync(this);

    }

    //This method initializes all view objects in this activity
    private void initializeViews() {
        placeName = findViewById(R.id.place_name);
        placeAddress = findViewById(R.id.place_address);
        relativeLayout = findViewById(R.id.maps_center_rel_layout);
        placeImage = findViewById(R.id.place_image_view);
        tempText = findViewById(R.id.temp);
        humidityText = findViewById(R.id.humidity);
        timeText = findViewById(R.id.time);
        weatherIcon = findViewById(R.id.weather_icon);
        recommendation = findViewById(R.id.recommendation_text);
        description = findViewById(R.id.description_text);
        masks = findViewById(R.id.mask_info);
        quarantine = findViewById(R.id.quarantine_info);
        tests = findViewById(R.id.test_info);
        newTripButton = findViewById(R.id.floating_button);
        anyChartView = findViewById(R.id.any_chart_view);
        eventsRecyclerView = findViewById(R.id.events_recycler_view);
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
    }

    //This method configures all view object settings
    private void configViews() {

        //Initialize eventAdapter
        eventAdapter = new EventAdapter(this, eventsList);

        //Configure eventsRecyclerView
        eventsRecyclerView.setHasFixedSize(true);
        eventsRecyclerView.setAdapter(eventAdapter);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        //Configure remaining views to be configured
        cartesian = AnyChart.column();
        placeImage.setClipToOutline(true);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
    }

    //This method sets up the bottom navigation bar
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");

        //Define and initialize bottomNavigationViewEx
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);

        //Setup and enable bottomNavigationViewEx with static BottomNavigationViewHelper methods
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(this, bottomNavigationViewEx);

        //Create menu, get current menu item and set that item to be checked as current item
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    //This method checks if permissions weren't granted -> prompts permission, else -> get current location
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            getCurrentLocation();
        }
    }

    //This method initializes the Places and PlacesClient Objects
    private void initPlacesAndPlacesClient() {
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        placesClient = Places.createClient(this);
    }

    //This method configures the autoCompleteFragment
    private void configAutoCompleteFragment() {

        //Set location bias
        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(
                new LatLng(53.337433, -6.284179),
                new LatLng(53.340713, -6.535491)
        ));

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.PHOTO_METADATAS, Place.Field.ADDRESS));
    }


    //This method observes weather info from the MapSearchActivityViewModel class and updates the current weather info views
    private void loadWeather(double lat, double lon) {

        //Call ViewModel to send a weather API request with lat and lon as params
        mapSearchActivityViewModel.sendWeatherRequest(lat, lon);

        //Observe from the weather data being emitted by the ViewModel
        mapSearchActivityViewModel.getWeatherMutableLiveData().observe(MapSearchActivity.this, weather -> {

            //Update view info to display current weather info & image
            //timeText.setText(weather.getTime());
            timeText.setText("3:24PM");
            tempText.setText(weather.getTemp() + "°C");
            humidityText.setText("Humidity: " + weather.getHumidity() + "%");

            Glide.with(getApplicationContext()).load(weather.getImgUrl()).into(weatherIcon);
        });
    }


    //This method observes an events list from the MapSearchActivityViewModel class and updates the current events list
    private void loadEvents(String placeName) {

        //Call ViewModel to send an event data request with place name as param
        mapSearchActivityViewModel.sendEventRequest(placeName);
        //Get LiveData object from MapSearchActivityViewModel
        LiveData<List<Event>> liveData = mapSearchActivityViewModel.getEventsList();

        //Observe liveData object from ViewModel
        liveData.observe(this, events -> {

            //Update eventsList and notify the eventAdapter of the new data set
            eventsList.clear();
            eventsList.addAll(events);
            eventAdapter.notifyDataSetChanged();

        });
    }

    //This method observes a LiveData object from the ViewModel containing a bitmap
    private void getPlaceImage() {

        //Initialize LiveData object
        LiveData<Bitmap> bitmapLiveData = mapSearchActivityViewModel.getBitmapMutableLiveData();

        //Observe LiveData object from ViewModel to get Bitmap and set Bitmap to Place Image View
        bitmapLiveData.observe(this, bitmap -> {
            placeImage.setImageBitmap(bitmap);
            placeImage.setAdjustViewBounds(true);
            placeImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        });
    }

    //This method observes covid info for a certain location from the MapSearchActivityViewModel class and updates the covid info views
    public void loadCoronaVirusStats(String placeName) {

        //Send covid stats request to ViewModel
        mapSearchActivityViewModel.sendCovidStatsRequest(placeName);
        //Get LiveData object from MapSearchActivityViewModel
        LiveData<CovidStatistics> liveData = mapSearchActivityViewModel.getCovidStats();

        //Observe liveData object from ViewModel
        liveData.observe(this, covidStatistics -> {

            updateCovidInfoViews(covidStatistics);
        });
    }

    //This method updates the covid info views with a given CovidStatistics Object
    private void updateCovidInfoViews(CovidStatistics covidStatistics) {
        recommendation.setText(covidStatistics.getRecommendation());
        description.setText(covidStatistics.getDescription());
        masks.setText(covidStatistics.getMasks());
        quarantine.setText(covidStatistics.getQuarantine());
        tests.setText(covidStatistics.getTests());
    }

    //This method observes an daily covid info list for a given location from the MapSearchActivityViewModel class and updates the daily covid info list
    public void loadCoronaVirusStatsChart(String placeName) {

        //Send Covid graph stats request to ViewModel
        mapSearchActivityViewModel.sendCovidGraphStatsRequest(placeName);

        //Get LiveData object from MapSearchActivityViewModel
        LiveData<List<DataEntry>> liveData = mapSearchActivityViewModel.getCovidGraphStats();

        //Observe liveData object from ViewModel
        liveData.observe(this, covidGraphStats -> {

            //Clear list
            data.clear();

            //Add all stats the data list
            data.addAll(covidGraphStats);

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

    //This method gets the last known location and moves the map camera to this location while adding a marker
    private void getCurrentLocation() {
        @SuppressLint("MissingPermission") Task<Location> task = mFusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {

                //OnMapReadyCallBack
                supportMapFragment.getMapAsync(googleMap -> {

                    //Get LatLng of location and create marker
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    MarkerOptions options = new MarkerOptions().position(latLng).title("Current Location");

                    //Move camera to location and add the marker
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    googleMap.addMarker(options);
                });
            }
        });
        return;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If permissions were granted
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    //Get current location of user
                    getCurrentLocation();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }
}
