package com.example.travelhut.viewmodel.main.map_search;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.anychart.chart.common.dataentry.DataEntry;
import com.example.travelhut.model.main.map_search.PlacesImageRepository;
import com.example.travelhut.model.main.shared_apis.CovidStatisticsRepository;
import com.example.travelhut.model.main.shared_apis.TicketMasterAPIRepository;
import com.example.travelhut.model.main.map_search.WeatherAPIRepository;
import com.example.travelhut.model.objects.Event;
import com.example.travelhut.model.objects.Weather;
import com.example.travelhut.model.objects.CovidStatistics;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.List;

public class MapSearchActivityViewModel extends AndroidViewModel {


    //Instance Variables
    private WeatherAPIRepository weatherAPIRepository;
    private TicketMasterAPIRepository ticketMasterAPIRepository;
    private CovidStatisticsRepository covidStatisticsRepository;
    private PlacesImageRepository placesImageRepository;

    //Constructor
    public MapSearchActivityViewModel(@NonNull Application application) {
        super(application);
        weatherAPIRepository = new WeatherAPIRepository();
        ticketMasterAPIRepository = new TicketMasterAPIRepository();
        covidStatisticsRepository = new CovidStatisticsRepository();
        placesImageRepository = new PlacesImageRepository();
    }

    //This method notifies weatherAPIRepository to send a weather request with lat lon passed as params
    public void sendWeatherRequest(double lat, double lon){
        weatherAPIRepository.loadWeatherData(lat, lon);
    }

    //This method notifies ticketMasterAPIRepository to send a event request with placeName as param
    public void sendEventRequest(String placeName){
        ticketMasterAPIRepository.loadEvents(placeName);
    }

    //This method notifies placesImageRepository to send a places image request with PlacesClient and Place
    public void sendImageRequest(PlacesClient placesClient, Place place){
        placesImageRepository.sendImageRequest(placesClient, place);
    }

    //This method notifies covidStatisticsRepository to send a covid stats request with placesName
    public void sendCovidStatsRequest(String placeName){
        covidStatisticsRepository.loadCoronaVirusStats(placeName);
    }

    //This method notifies covidStatisticsRepository to send a covid graph stats request with placesName
    public void sendCovidGraphStatsRequest(String placeName){
        covidStatisticsRepository.loadCoronaVirusStatsChart(placeName);
    }


    @NonNull
    public MutableLiveData<Weather> getWeatherMutableLiveData(){
        return weatherAPIRepository.getWeatherMutableLiveData();
    }

    @NonNull
    public MutableLiveData<List<Event>> getEventsList(){
        return ticketMasterAPIRepository.getMutableEventList();
    }

    @NonNull
    public MutableLiveData<CovidStatistics> getCovidStats(){
        return covidStatisticsRepository.getMutableCovidStats();
    }

    @NonNull
    public MutableLiveData<List<DataEntry>> getCovidGraphStats(){
        return covidStatisticsRepository.getMutableCovidGraphStats();
    }
    @NonNull
    public MutableLiveData<Bitmap> getBitmapMutableLiveData(){
        return placesImageRepository.getBitmapMutableLiveData();
    }
}
