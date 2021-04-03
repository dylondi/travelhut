package com.example.travelhut.model.main.map_search;

import androidx.core.util.Pair;

import com.example.travelhut.model.utils.StringsRepository;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CreateTripRepository{

    //Constructor
    public CreateTripRepository() {
    }

    //This method creates Trip reference in the real-time database with inputted data
    public void createTrip(Pair selectedDates, Place place, String dateRange) {

        //Retrieve the database reference to the current users trips
        DatabaseReference tripsReference = FirebaseDatabase.getInstance().getReference(StringsRepository.TRIPS_CAP).child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        //Retrieve story key for story to be uploaded
        String tripId = tripsReference.push().getKey();

        //Create HashMap with Trip data
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(StringsRepository.TRIP_ID, tripId);
        hashMap.put(StringsRepository.PLACE_ID, place.getId());
        hashMap.put(StringsRepository.PLACENAME, place.getName());
        hashMap.put(StringsRepository.PLACEADDRESS, place.getAddress());
        hashMap.put(StringsRepository.DATERANGE, dateRange);
        hashMap.put(StringsRepository.STARTDATE, selectedDates.first);
        hashMap.put(StringsRepository.ENDDATE, selectedDates.second);

        //SetValue of reference to be equal to the HashMap data
        tripsReference.child(tripId).setValue(hashMap);
    }
}
