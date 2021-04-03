package com.example.travelhut.model.main.shared_apis;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.travelhut.model.utils.Common;
import com.example.travelhut.model.utils.RestUtil;
import com.example.travelhut.model.utils.StringsRepository;
import com.example.travelhut.model.objects.Event;
import com.example.travelhut.model.objects.Trip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TicketMasterAPIRepository extends LiveData<List<Event>> {

    //Instance Variable
    private MutableLiveData<List<Event>> mutableEventList;

    //Constructor
    public TicketMasterAPIRepository(String tripId) {
        mutableEventList = new MutableLiveData<>();
        getPlaceName(tripId);
    }
    public TicketMasterAPIRepository() {
        mutableEventList = new MutableLiveData<>();
    }

    //This method retrieves the name of the place by searching the database with the trip ID and then passes this name into the loadEvents() method
    private void getPlaceName(String tripId) {

        //Database reference to the current users selected trip
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(StringsRepository.TRIPS_CAP).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(tripId);

        //Single Value Event listener for the database reference
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Trip trip = snapshot.getValue(Trip.class);
                loadEvents(trip.getPlacename());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //This method loads events from the ticketmaster events api for given location
    public void loadEvents(String eventPlaceName) {

        //Strings containing event api key and request url
        String eventApi = Common.EVENT_API_KEY;
        String url = StringsRepository.TICKETMASTER_API_BASE_URL + eventApi + "&city=" + eventPlaceName;

        //Initialize event list
        List<Event> eventsList = new ArrayList<>();

        //Start a background thread which retrieves the data from the url, parses the data into a JSONObject and then adds it to the eventList
        new Thread(() -> {
            try {
                //Retrieve data from url and parse into JSONObject
                JSONObject jsonObject = RestUtil.getJSONObject(url);
                JSONArray main = jsonObject.getJSONObject(StringsRepository._EMBEDDED).getJSONArray(StringsRepository.EVENTS);

                //Iterate through events retrieved
                for (int i = 0; i < main.length(); i++) {
                    addEventToEventList(eventsList, main, i);
                }

                //Post eventsList List to the MutableLiveData object mutableEventList
                mutableEventList.postValue(eventsList);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    //This method adds the current event to the eventsList list
    private void addEventToEventList(List<Event> eventsList, JSONArray main, int i) throws JSONException {
        String eventId = main.getJSONObject(i).getString(StringsRepository.ID);
        String eventName = main.getJSONObject(i).getString(StringsRepository.NAME);
        String eventDate = main.getJSONObject(i).getJSONObject(StringsRepository.DATES).getJSONObject(StringsRepository.START).getString(StringsRepository.LOCAL_DATE);
        String eventPlace = main.getJSONObject(i).getJSONObject(StringsRepository._EMBEDDED).getJSONArray(StringsRepository.VENUES).getJSONObject(0).getString(StringsRepository.NAME);
        String eventImageUrl = main.getJSONObject(i).getJSONArray(StringsRepository.IMAGES).getJSONObject(0).getString(StringsRepository.URL);

        eventsList.add(new Event(eventName, eventPlace, eventDate, eventId, eventImageUrl));
    }


    public MutableLiveData<List<Event>> getMutableEventList() {
        return mutableEventList;
    }
}
