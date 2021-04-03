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

    private WeatherAPIRepository weatherAPIRepository;
    private TicketMasterAPIRepository ticketMasterAPIRepository;
    private CovidStatisticsRepository covidStatisticsRepository;
    private PlacesImageRepository placesImageRepository;

    public MapSearchActivityViewModel(@NonNull Application application) {
        super(application);
        weatherAPIRepository = new WeatherAPIRepository();
        ticketMasterAPIRepository = new TicketMasterAPIRepository();
        covidStatisticsRepository = new CovidStatisticsRepository();
        placesImageRepository = new PlacesImageRepository();
    }


    @NonNull
    public MutableLiveData<Weather> getWeatherMutableLiveData(){
        return weatherAPIRepository.getWeatherMutableLiveData();
    }

    public void sendWeatherRequest(double lat, double lon){
        weatherAPIRepository.loadWeatherData(lat, lon);
    }

    public void sendEventRequest(String placeName){
        ticketMasterAPIRepository.loadEvents(placeName);
    }

    public void sendCovidStatsRequest(String placeName){
        covidStatisticsRepository.loadCoronaVirusStats(placeName);
    }

    public void sendCovidGraphStatsRequest(String placeName){
        covidStatisticsRepository.loadCoronaVirusStatsChart(placeName);
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

    public void sendImageRequest(PlacesClient placesClient, Place place){
        placesImageRepository.sendImageRequest(placesClient, place);
    }

    public MutableLiveData<Bitmap> getBitmapMutableLiveData(){
        return placesImageRepository.getBitmapMutableLiveData();
    }
}
