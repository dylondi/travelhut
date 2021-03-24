package com.example.travelhut.views;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.travelhut.R;
import com.example.travelhut.common.Common;
import com.example.travelhut.views.main.map_search.Event;
import com.example.travelhut.views.main.map_search.EventAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class EventActivity extends AppCompatActivity {

    private ImageView backArrow, eventImage;
    private TextView eventName, eventVenue, eventPriceRange, eventLink;
    private String eventid;
    private Handler mainHandler = new Handler(Looper.getMainLooper());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        String eventLinkString = "";
        backArrow = findViewById(R.id.event_back_arrow);
        eventImage = findViewById(R.id.event_activity_image);
        eventName = findViewById(R.id.event_activity_event_name);
        eventVenue = findViewById(R.id.event_activity_event_venue);
        eventPriceRange = findViewById(R.id.event_activity_event_price);
        eventLink = findViewById(R.id.event_activity_event_link);



        eventid = getIntent().getStringExtra("eventid");
        getEventInfo();


        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        eventLink.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(eventLink.getText().toString()));
                startActivity(browserIntent);
            }
        });


    }

    private void getEventInfo() {
        String eventApi = Common.EVENT_API;
        String url = "http://app.ticketmaster.com/discovery/v2/events/" + eventid +".json?apikey=" + eventApi;


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = getJSONObject(url);
                    double minPrice = jsonObject.getJSONArray("priceRanges").getJSONObject(1).getDouble("min");
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            eventPriceRange.setText("Minimum Price including fees: €" + minPrice);
                            try {
                                eventName.setText(jsonObject.getString("name"));
                                eventVenue.setText("Venue: " + jsonObject.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name"));
                                String imageUrl = jsonObject.getJSONArray("images").getJSONObject(0).getString("url");
                                String eventLinkString = jsonObject.getString("url");

                                eventLink.setText(eventLinkString);
//                                Pattern pattern = Pattern.compile(eventLinkString);
//                                Linkify.addLinks(eventLink, pattern, "https://");



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


                        }});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }}).start();
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