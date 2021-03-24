package com.example.travelhut.views.main.map_search;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.travelhut.R;
import com.example.travelhut.common.Common;
import com.example.travelhut.utils.BottomNavigationViewHelper;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.util.IOUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.gson.JsonObject;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MapSearchActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MapSearchActivity";
    private Context mContext = MapSearchActivity.this;
    private static final int ACTIVITY_NUM = 0;
    private GoogleMap mMap;
    private SupportMapFragment supportMapFragment;
    static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 23;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Marker currentMarker;
    private TextView placeName;
    private TextView placeAddress;
    private RelativeLayout relativeLayout;
    private ImageView placeImage;
    private String placeId;
    private TextView tempText, humidityText, timeText;
    private ImageView weatherIcon;
    String nameIcon;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private List<Event> eventsList = new ArrayList<>();
    private EventAdapter eventAdapter;
    private LinearLayout eventsLinearLayout;
    private RecyclerView eventsRecyclerView;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_search);
        Log.d(TAG, "onCreate: started.");

        placeName = findViewById(R.id.place_name);
        placeAddress = findViewById(R.id.place_address);
        relativeLayout = findViewById(R.id.maps_center_rel_layout);
        placeImage = findViewById(R.id.place_image_view);
        tempText = findViewById(R.id.temp);
        humidityText = findViewById(R.id.humidity);
        timeText = findViewById(R.id.time);
        weatherIcon = findViewById(R.id.weather_icon);
        eventsLinearLayout = findViewById(R.id.events_lin_layout);

        eventAdapter = new EventAdapter(this, eventsList);

        eventsRecyclerView = findViewById(R.id.events_recycler_view);
        eventsRecyclerView.setHasFixedSize(true);
        eventsRecyclerView.setAdapter(eventAdapter);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        placeImage.setClipToOutline(true);
        Button button = findViewById(R.id.floating_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapSearchActivity.this, NewTripActivity.class);
                intent.putExtra("placeid", placeId);
                startActivity(intent);            }
        });

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        setupBottomNavigationView();
        checkPermission();

        String apiKey = getString(R.string.google_api_key);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(
                new LatLng(53.337433, -6.284179),
                new LatLng(53.340713, -6.535491)
        ));

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.PHOTO_METADATAS, Place.Field.ADDRESS));
//        autocompleteFragment.setText("Celbridge");

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());

                if (currentMarker != null) {
                    currentMarker.remove();
                }


                if (place.getName() != null || !place.getName().equals("")) {
                    Geocoder geocoder = new Geocoder(MapSearchActivity.this);
//                    try{
//                        addressList = geocoder.getFromLocationName(place.getName(), 1);
//                    }catch(IOException e){
//                        e.printStackTrace();
//                    }

                    //Address address = addressList.get(0);
                    placeId = place.getId();
                    currentMarker = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName()));

                    LatLng latLng = new LatLng(place.getLatLng().latitude - 0.007 , place.getLatLng().longitude);

                    relativeLayout.setVisibility(View.VISIBLE);
                    relativeLayout.setClickable(true);
                    //button.setImageResource(R.drawable.ic_new_trip);
                    placeName.setText(place.getName());
                    placeAddress.setText(place.getAddress());

                    loading(place.getLatLng().latitude, place.getLatLng().longitude);

                    loadEvents(place.getName());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    Log.i(TAG, "getName(): " + place.getName());
                    Log.i(TAG, "getAddress: " + place.getAddress());
                    Log.i(TAG, "getAddressComponents: " + place.getAddressComponents());




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
                        placeImage.setImageBitmap(bitmap);
                        placeImage.setAdjustViewBounds(true);
                        placeImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
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
            }


            @Override
            public void onError(@NotNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        supportMapFragment.getMapAsync(this);
//        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
//            if (location != null) {
//                wayLatitude = location.getLatitude();
//                wayLongitude = location.getLongitude();
//                //txtLocation.setText(String.format(Locale.US, "%s -- %s", wayLatitude, wayLongitude));
//            }
//        });


//        locationRequest = LocationRequest.create();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(20 * 1000);
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                if (locationResult == null) {
//                    return;
//                }
//                for (Location location : locationResult.getLocations()) {
//                    if (location != null) {
//                        wayLatitude = location.getLatitude();
//                        wayLongitude = location.getLongitude();
//                        //txtLocation.setText(String.format(Locale.US, "%s -- %s", wayLatitude, wayLongitude));
//                    }
//                }
//            }
//        };
    }


    private static JSONObject getJSONObject(String _url) throws Exception {
        if (_url.equals(""))
            throw new Exception("URL can't be empty");

        URL url = new URL(_url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setDoInput(true);
        conn.setRequestProperty("User-Agent", "android");
        conn.setRequestProperty("Accept", "application/json");
        conn.addRequestProperty("Content-Type", "application/json");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));

        if (!url.getHost().equals(conn.getURL().getHost())) {
            conn.disconnect();
            return new JSONObject();
        }
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        conn.disconnect();

        return new JSONObject(response.toString());

    }

    private void loadEvents(String eventPlaceName) {


        String eventApi = Common.EVENT_API;
        String url = "http://app.ticketmaster.com/discovery/v2/events.json?apikey=" + eventApi + "&city=" + eventPlaceName;
        eventsList.clear();
        eventAdapter.notifyDataSetChanged();

        new Thread(new Runnable() {
            @Override
            public void run(){
        try {

            JSONObject jsonObject = getJSONObject(url);
            JSONArray main = jsonObject.getJSONObject("_embedded").getJSONArray("events");


            for (int i = 0; i < main.length(); i++) {

                String eventId = main.getJSONObject(i).getString("id");
                String eventName = main.getJSONObject(i).getString("name");
                String eventDate = main.getJSONObject(i).getJSONObject("dates").getJSONObject("start").getString("localDate");
                String eventPlace = main.getJSONObject(i).getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name");
                String eventImageUrl = main.getJSONObject(i).getJSONArray("images").getJSONObject(0).getString("url");

                System.out.println("run: eventName:" + eventName + ", eventDate: " + eventDate);
                eventsList.add(new Event(eventName, eventPlace, eventDate, eventId, eventImageUrl));
            }

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    eventAdapter.notifyDataSetChanged();
                }
            });
            return;

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
            }}).start();
    }

    private void getCurrentLocation() {


            @SuppressLint("MissingPermission") Task<Location> task = mFusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location!=null){
                        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                MarkerOptions options = new MarkerOptions().position(latLng).title("Current Location");

                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                                googleMap.addMarker(options);
                            }
                        });
                    }
                }
            });
            return;



    }








    public void loading(double lat, double lon){


        new Thread(new Runnable() {
            @Override
            public void run() {
        String weatherAPI = Common.WEATHER_API;
        String url = "https://api.openweathermap.org/data/2.5/onecall?lat=" + lat + "&lon=" + lon + "&exclude={part}&appid=" + weatherAPI;
        DownloadJSON downloadJSON = new DownloadJSON();
        String sTime;
        try {
            String result = "abc";

            result = downloadJSON.execute(url).get();

            JSONObject jsonObject = new JSONObject(result);
            JSONObject main = jsonObject.getJSONObject("current");
            String temp = main.getString("temp");

            String humidity = main.getString("humidity");

            Long time = main.getLong("dt");
            Long timeShift = jsonObject.getLong("timezone_offset");


            sTime = new SimpleDateFormat("hh:mm", Locale.ENGLISH)
                    .format(new Date((time + timeShift)*1000));

            Long kelvToCel = 273L;
            long l = (new Double(Double.parseDouble(temp))).longValue(); //129
            nameIcon = main.getJSONArray("weather").getJSONObject(0).getString("icon");


                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {

            timeText.setText(sTime);
            tempText.setText(l - kelvToCel + "°C");
            humidityText.setText("Humidity: " + humidity + "%");



            String urlIcon = "http://openweathermap.org/img/wn/" + nameIcon + "@2x.png";
            Glide.with(getApplicationContext()).load(urlIcon).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    //progressBar.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    //progressBar.setVisibility(View.GONE);

                    return false;
                }
            }).dontAnimate().into(weatherIcon);

                        }
                    });
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
            }}).start();
    }

//
//    private void getLastKnownLocation() {
//        Log.d(TAG, "getLastKnownLocation: called.");
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
//            @Override
//            public void onComplete(@NonNull Task<Location> task) {
//                if (task.isSuccessful()) {
//                    Location location = task.getResult();
//                }
//            }
//        });
//    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            //getCurrentLocation();
        } else {
//            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//            mapFragment.getMapAsync(this);
            getCurrentLocation();
        }

    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//                    mapFragment.getMapAsync(this);
//                } else {
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }
//                return;
//            }
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    getCurrentLocation();
                    mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
                        if (location != null) {
//                            wayLatitude = location.getLatitude();
//                            wayLongitude = location.getLongitude();
                            //txtLocation.setText(String.format(Locale.US, "%s -- %s", wayLatitude, wayLongitude));
                        }
                    });
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
//                mLastLocation = location;
//                if (mCurrLocationMarker != null) {
//                    mCurrLocationMarker.remove();
//                }
//                //Place current location marker
//                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//                MarkerOptions markerOptions = new MarkerOptions();
//                markerOptions.position(latLng);
//                markerOptions.title("Current Position");
//                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//                mCurrLocationMarker = mMap.addMarker(markerOptions);
//
//                //move map camera
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
//
//                //stop location updates
//                if (mGoogleApiClient != null) {
//                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//                }


//                LatLng latlng=new LatLng(location.getLatitude(),location.getLongitude());
//                MarkerOptions markerOptions = new MarkerOptions();
//                markerOptions.position(latlng);
//
//                markerOptions.title("My Marker");
//                mMap.clear();
//                CameraUpdate cameraUpdate= CameraUpdateFactory.newLatLngZoom(
//                        latlng, 15);
//                mMap.animateCamera(cameraUpdate);
//                mMap.addMarker(markerOptions);

            }
        });

    }
}
