package com.example.travelhut.viewmodel.main.map_search;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.lifecycle.AndroidViewModel;

import com.example.travelhut.model.main.map_search.CreateTripRepository;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.database.DatabaseReference;

public class NewTripActivityViewModel extends AndroidViewModel {

    private CreateTripRepository createTripRepository;

    public NewTripActivityViewModel(@NonNull Application application) {
        super(application);
        createTripRepository = new CreateTripRepository();
    }

    public void createTrip(Pair selectedDates, Place place, String startDate){
        createTripRepository.createTrip(selectedDates, place, startDate);
    }
}
