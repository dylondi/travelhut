package com.example.travelhut.views;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.travelhut.R;
import com.example.travelhut.model.utils.Common;
import com.example.travelhut.model.utils.RestUtil;
import com.example.travelhut.model.utils.StringsRepository;

import org.json.JSONException;
import org.json.JSONObject;

public class EventActivity extends AppCompatActivity {


    //Instance Variables
    private ImageView backArrow, eventImage;
    private TextView eventName, eventVenue, eventPriceRange, eventLink;
    private String eventId;
    private Handler mainHandler = new Handler(Looper.getMainLooper());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        //Initialize view objects
        initViews();

        //Get eventId data from intent
        eventId = getIntent().getStringExtra(StringsRepository.EVENT_ID);
        getEventInfo();

        //Set OnClickListener for backArrow
        backArrow.setOnClickListener(v -> finish());

        //Set OnClickListener for event link to open browser to ticketmaster website with event
        eventLink.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(eventLink.getText().toString()));
            startActivity(browserIntent);
        });


    }

    private void initViews() {
        backArrow = findViewById(R.id.event_back_arrow);
        eventImage = findViewById(R.id.event_activity_image);
        eventName = findViewById(R.id.event_activity_event_name);
        eventVenue = findViewById(R.id.event_activity_event_venue);
        eventPriceRange = findViewById(R.id.event_activity_event_price);
        eventLink = findViewById(R.id.event_activity_event_link);
    }

    private void getEventInfo() {
        String eventApi = Common.EVENT_API_KEY;
        String url = "http://app.ticketmaster.com/discovery/v2/events/" + eventId +".json?apikey=" + eventApi;


        new Thread(() -> {
            try {
                JSONObject jsonObject = RestUtil.getJSONObject(url);
                double minPrice = jsonObject.getJSONArray("priceRanges").getJSONObject(1).getDouble("min");
                mainHandler.post(() -> {
                    eventPriceRange.setText("Minimum Price including fees: €" + minPrice);
                    try {

                        //Set text of views with data from event
                        eventName.setText(jsonObject.getString(StringsRepository.NAME));
                        eventVenue.setText("Venue: " + jsonObject.getJSONObject(StringsRepository._EMBEDDED).getJSONArray(StringsRepository.VENUES).getJSONObject(0).getString(StringsRepository.NAME));
                        eventLink.setText(jsonObject.getString(StringsRepository.URL));

                        //Get imageUri of event
                        String imageUrl = jsonObject.getJSONArray(StringsRepository.IMAGES).getJSONObject(0).getString(StringsRepository.URL);

                        //Load image into view
                        Glide.with(getApplicationContext()).load(imageUrl).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                //progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                //holder.progressBar.setVisibility(View.GONE);

                                return false;
                            }
                        }).dontAnimate().into(eventImage);
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}