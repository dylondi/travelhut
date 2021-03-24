package com.example.travelhut.views.main.trips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.travelhut.R;
import com.example.travelhut.common.Common;
import com.example.travelhut.views.main.map_search.Event;
import com.example.travelhut.views.main.map_search.EventAdapter;
import com.example.travelhut.views.main.trips.trip_fragments.Trip;
import com.example.travelhut.views.main.trips.trip_fragments.TripsAdapter;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TripActivity extends AppCompatActivity {

    private TextView placeName, placeAddress, dateRange;
    private ImageView placeImage, backArrow;
    private Button changeDates;
    private static final String TAG = "TripActivity";
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private List<Event> eventsList = new ArrayList<>();
    private EventAdapter eventAdapter;
    private LinearLayout eventsLinearLayout;
    private RecyclerView eventsRecyclerView;
    private TextView recommendation, description, masks, quarantine, tests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        placeName = findViewById(R.id.trip_place_name);
        placeAddress = findViewById(R.id.trip_place_address);
        dateRange = findViewById(R.id.trip_date);
        placeImage = findViewById(R.id.trip_image_view);
        backArrow = findViewById(R.id.trip_back_arrow);
        changeDates = findViewById(R.id.change_dates_button);
        eventsLinearLayout = findViewById(R.id.trip_events_lin_layout);
        recommendation = findViewById(R.id.recommendation_text);
        description = findViewById(R.id.description_text);
        masks = findViewById(R.id.mask_info);
        quarantine = findViewById(R.id.quarantine_info);
        tests = findViewById(R.id.test_info);
        eventAdapter = new EventAdapter(this, eventsList);

        eventsRecyclerView = findViewById(R.id.trip_events_recycler_view);
        eventsRecyclerView.setHasFixedSize(true);
        eventsRecyclerView.setAdapter(eventAdapter);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        String apiKey = getString(R.string.google_api_key);


        Bundle extras = getIntent().getExtras();
        String tripId = extras.getString("tripid", null);
        String placeId = extras.getString("placeid", null);

        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();

        final MaterialDatePicker materialDatePicker = builder.build();




        changeDates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {

                DatabaseReference  tripsReference = FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(tripId);
                //Retrieve the database reference to the current users stories


                //Retrieve story key for story to be uploaded



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
                hashMap.put("daterange", materialDatePicker.getHeaderText());
                hashMap.put("startdate", (Long) selectedDates.first);
                hashMap.put("enddate", (Long) selectedDates.second);

                tripsReference.updateChildren(hashMap);
                dateRange.setText(materialDatePicker.getHeaderText());

            }
        });

        final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.PHOTO_METADATAS);

        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
        if (!Places.isInitialized()) {
            Places.initialize(this, apiKey);
        }
        PlacesClient placesClient = Places.createClient(this);
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            setPlaceImage(placesClient, place);
            loadEvents(place.getName());
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + exception.getMessage());
                final int statusCode = apiException.getStatusCode();
                // TODO: Handle error with given status code.
            }
        });


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(tripId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Trip trip = snapshot.getValue(Trip.class);
                //Glide.with(mContext).load(user.getImageurl()).into(imageView);
                placeName.setText(trip.getPlacename());
                placeAddress.setText(trip.getPlaceaddress());
                dateRange.setText(trip.getDaterange());
                try {
                    loadCoronaVirusStats(trip.getPlacename());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    public void setPlaceImage(PlacesClient placesClient, Place place){
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

    public void loadCoronaVirusStats(String placeName) throws Exception {

// Host url
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .header("X-Access-Token", " a7ab64d2-f9eb-40b1-9634-62caffdc7732")
                .url("https://api.traveladviceapi.com/search/" + placeName)
                .build();

        client.newCall(request).enqueue(new Callback() {


            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    JSONObject json = new JSONObject(responseData);
                    final String owner = json.getString("name");
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                recommendation.setText(json.getString("recommendation"));
                                description.setText(json.getJSONArray("trips").getJSONObject(0).getJSONObject("advice").getString("level_desc"));
                                String mask = json.getJSONObject("requirements").getString("masks");
                                masks.setText(mask);
                                quarantine.setText(json.getJSONObject("requirements").getString("quarantine"));
                                tests.setText(json.getJSONObject("requirements").getString("tests"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }});
                } catch (JSONException e) {

                }
            }
        });

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
}