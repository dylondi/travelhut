package com.example.travelhut.model.main.shared_apis;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.example.travelhut.model.utils.Common;
import com.example.travelhut.model.utils.StringsRepository;
import com.example.travelhut.model.objects.Trip;
import com.example.travelhut.model.objects.CovidStatistics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CovidStatisticsRepository extends LiveData<CovidStatistics> {

    //Instance Variables
    private MutableLiveData<CovidStatistics> mutableCovidStatistics;
    private MutableLiveData<List<DataEntry>> mutableGraphData;

    //Constructors

    //Constructor for Trip page
    public CovidStatisticsRepository(String tripIdOrPlaceName, boolean isTripId) {
        mutableCovidStatistics = new MutableLiveData<>();
        mutableGraphData = new MutableLiveData<>();
        if (isTripId) {
            getPlaceName(tripIdOrPlaceName);
        }
    }
    //Constructor for Map page
    public CovidStatisticsRepository() {
        mutableCovidStatistics = new MutableLiveData<>();
        mutableGraphData = new MutableLiveData<>();
    }

    //This method returns the place from the the tripId param
    private void getPlaceName(String tripId) {

        //Database reference to the current users selected trip
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(StringsRepository.TRIPS_CAP).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(tripId);

        //Single Value Event listener for the database reference
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Get Trip object from snapshot
                Trip trip = snapshot.getValue(Trip.class);
                try {
                    //Call both corona stat methods
                    loadCoronaVirusStats(trip.getPlacename());
                    loadCoronaVirusStatsChart(trip.getPlacename());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //This method retrieves data to advise users of Covid situation in the location searched (
    public void loadCoronaVirusStats(String placeName){

        //Init OkHttpClient
        OkHttpClient client = new OkHttpClient();

        //Request builder with header info and url
        Request request = new Request.Builder()
                .header(StringsRepository.X_ACCESS_TOKEN, Common.COVID_API_KEY)
                .url(StringsRepository.TRAVEL_ADVICE_BASE_URL + placeName)
                .build();

        //Create a new call with the Request object
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                try {
                    //Get response as a String and then create a JSONObject from this
                    String responseData = response.body().string();
                    JSONObject json = new JSONObject(responseData);

                    //Collect all data in Strings
                    String recommendation = json.getString(StringsRepository.RECOMMENDATION);
                    String description = json.getJSONArray(StringsRepository.TRIPS).getJSONObject(0).getJSONObject(StringsRepository.ADVICE).getString(StringsRepository.LEVEL_DESC);
                    String masks = json.getJSONObject(StringsRepository.REQUIREMENTS).getString(StringsRepository.MASKS);
                    String quarantine = json.getJSONObject(StringsRepository.REQUIREMENTS).getString(StringsRepository.QAURANTINE);
                    String tests = json.getJSONObject(StringsRepository.REQUIREMENTS).getString(StringsRepository.TESTS);

                    //Pass String values into a CovidStatistic Object and post this Object to the MutableLiveData Object to be observed in ViewModel
                    mutableCovidStatistics.postValue(new CovidStatistics(recommendation, description, masks, quarantine, tests));

                } catch (JSONException e) {
                }
            }
        });
    }

    //This method queries an airport API with a Name and receive a three letter IATA representation of the airport name. eg Dublin -> DUB or London Heathrow -> LHR
    //It then calls the getDailyCovidInfo method
    public void loadCoronaVirusStatsChart(String placeName) {

        OkHttpClient client = new OkHttpClient();

        //Request builder with header info and url
        Request requestOne = new Request.Builder()
                .header(StringsRepository.APC_AUTH, StringsRepository.APC_AUTH_VALUE)
                .addHeader(StringsRepository.APC_AUTH_SECRET, StringsRepository.APC_AUTH_SECRET_VALUE)
                .addHeader(StringsRepository.CONTENT_TYPE, StringsRepository.APPLICATION_JSON)
                .url(StringsRepository.AIRPORT_CODES_URL + placeName)
                .build();

        //Create a new call with the Request object
        client.newCall(requestOne).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                try {
                    //Get response as a String and then create a JSONObject from this which is passed into the getDailyCovidInfo() method
                    String responseData = response.body().string();
                    String iata = new JSONObject(responseData).getJSONArray(StringsRepository.AIRPORTS).getJSONObject(0).getString(StringsRepository.IATA);
                    getDailyCovidInfo(iata);

                } catch (JSONException e) {

                }
            }
        });
    }


    //This method uses the air_code variable to request daily covid data for a given location
    private void getDailyCovidInfo(String air_code) {

        List<DataEntry> entries = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();

        //Request builder with header info and url
        Request request = new Request.Builder()
                .header(StringsRepository.X_ACCESS_TOKEN, Common.COVID_API_KEY)
                .url(StringsRepository.TRAVEL_ADVICE_STATS_URL + air_code + StringsRepository.TRAVEL_ADVICE_STATS_URL_LIMITER_7)
                .build();
        client.newCall(request).enqueue(new Callback() {


            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                try {

                    //Get response as a String and then create a JSONObject from this
                    String responseData = response.body().string();
                    JSONArray jsonArray = new JSONObject(responseData).getJSONArray(air_code);

                    //For loop to iterate through each day of data
                    for (int i = jsonArray.length() - 1; i >= 0; i--) {
                        //Adds day i data to a ValueDataEntry Object and then adds this object to a list
                        entries.add(new ValueDataEntry(jsonArray.getJSONObject(i).getString(StringsRepository.DATE), jsonArray.getJSONObject(i).getInt(StringsRepository.NEW_CASES)));
                    }
                    //Posts data list to the MutableLiveData Object to be observed from ViewModel
                    mutableGraphData.postValue(entries);
                } catch (JSONException e) {

                }
            }
        });
    }

    //This method returns covid info on selected place
    public MutableLiveData<CovidStatistics> getMutableCovidStats() {
        return mutableCovidStatistics;
    }

    //This method returns covid graph data on selected place
    public MutableLiveData<List<DataEntry>> getMutableCovidGraphStats() {
        return mutableGraphData;
    }
}
