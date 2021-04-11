package com.example.travelhut.viewmodel.main.map_search;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.lifecycle.AndroidViewModel;

import com.example.travelhut.model.main.map_search.CreateTripRepository;
import com.google.android.libraries.places.api.model.Place;

public class NewTripActivityViewModel extends AndroidViewModel {

    //Instance Variable
    private CreateTripRepository createTripRepository;

    //Constructor
    public NewTripActivityViewModel(@NonNull Application application) {
        super(application);
        createTripRepository = new CreateTripRepository();
    }

    //This method notifies createTripRepository that a trip needs to be pushed to the database
    public void createTrip(Pair selectedDates, Place place, String startDate){
        createTripRepository.createTrip(selectedDates, place, startDate);
    }
}
