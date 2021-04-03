package com.example.travelhut.viewmodel.main.trips;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.anychart.chart.common.dataentry.DataEntry;
import com.example.travelhut.model.main.map_search.PlacesImageRepository;
import com.example.travelhut.model.main.shared_apis.CovidStatisticsRepository;
import com.example.travelhut.model.main.shared_apis.TicketMasterAPIRepository;
import com.example.travelhut.model.main.trips.TripInfoRepository;
import com.example.travelhut.model.objects.Event;
import com.example.travelhut.model.objects.CovidStatistics;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.database.DataSnapshot;

import java.util.List;

public class TripActivityViewModel extends AndroidViewModel {

    //Instance Variables
    private TripInfoRepository tripInfoRepository;
    private TicketMasterAPIRepository ticketMasterAPIRepository;
    private CovidStatisticsRepository covidStatisticsRepository;
    private PlacesImageRepository placesImageRepository;

    //Constructor
    public TripActivityViewModel(@NonNull Application application, String tripId) {
        super(application);
        tripInfoRepository = new TripInfoRepository(tripId);
        ticketMasterAPIRepository = new TicketMasterAPIRepository(tripId);
        covidStatisticsRepository = new CovidStatisticsRepository(tripId, true);
        placesImageRepository = new PlacesImageRepository();
    }

    public LiveData<DataSnapshot> getTripInfo(){
        return tripInfoRepository;
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
