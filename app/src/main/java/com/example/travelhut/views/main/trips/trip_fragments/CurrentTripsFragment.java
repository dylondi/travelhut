package com.example.travelhut.views.main.trips.trip_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelhut.R;
import com.example.travelhut.model.utils.StringsRepository;
import com.example.travelhut.model.objects.Trip;
import com.example.travelhut.views.main.trips.utils.TripsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CurrentTripsFragment extends Fragment {

    //Instance Variables
    private RecyclerView recyclerView;
    private TripsAdapter tripsAdapter;
    private List<Trip> currentTrips;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_trips, container,false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        //Initialize objects
        currentTrips = new ArrayList<>();
        tripsAdapter = new TripsAdapter(getContext(), currentTrips);

        //Initialize and config recyclerView
        recyclerView = view.findViewById(R.id.current_trips_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(tripsAdapter);

        //DatabaseReference to the current user's trips
        databaseReference = FirebaseDatabase.getInstance().getReference(StringsRepository.TRIPS_CAP).child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        long currentTime = System.currentTimeMillis();

        //SingleValueEventListener for databaseReference
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentTrips.clear();

                //Iterate through user's trips
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    //Get Trip object from dataSnapshot
                    Trip trip = snapshot.getValue(Trip.class);

                    //If current time is withing trip dates
                    if(trip.getStartdate()<currentTime && trip.getEnddate()+86400000>currentTime){
                        //Add to list of current trips
                        currentTrips.add(trip);
                    }
                }

                //Notify tripsAdapter of updated data set
                tripsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}
