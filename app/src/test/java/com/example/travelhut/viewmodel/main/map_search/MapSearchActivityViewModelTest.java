package com.example.travelhut.viewmodel.main.map_search;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.anychart.chart.common.dataentry.DataEntry;
import com.example.travelhut.model.main.shared_apis.CovidStatisticsRepository;
import com.example.travelhut.model.main.shared_apis.TicketMasterAPIRepository;
import com.example.travelhut.model.objects.CovidStatistics;
import com.example.travelhut.model.objects.Event;
import com.jraska.livedata.TestObserver;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.List;

public class MapSearchActivityViewModelTest {

    //Model objects
    private TicketMasterAPIRepository ticketMasterAPIRepository;
    private CovidStatisticsRepository covidStatisticsRepository;

    //InstantTaskExecutorRule must be used for TestObserver.test() to work
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {

        //Init Model classes
        ticketMasterAPIRepository = new TicketMasterAPIRepository();
        covidStatisticsRepository = new CovidStatisticsRepository();

    }

    //This method tests the ticketmaster api to make sure it receives data
    @Test
    public void testTicketMasterRepository() throws InterruptedException {

        //Send request
        ticketMasterAPIRepository.loadEvents("Dublin");

        //LiveData object containing response
        LiveData<List<Event>> events =  ticketMasterAPIRepository.getMutableEventList();

        //TestObserver for response and make sure response contains data
        TestObserver.test(events)
                .awaitValue()
                .assertHasValue();
    }

    //This method tests the travel advice api to make sure it receives data
    @Test
    public void testCovidRepository() throws InterruptedException {

        //Send request
        covidStatisticsRepository.loadCoronaVirusStats("Dublin");

        //LiveData object containing response
        LiveData<CovidStatistics> events =  covidStatisticsRepository.getMutableCovidStats();

        //TestObserver for response and make sure response contains data
        TestObserver.test(events)
                .awaitValue()
                .assertHasValue();
    }

    //This method tests the travel advice api to make sure it receives graph data
    @Test
    public void testCovidGraphRepository() throws InterruptedException {

        //Send request
        covidStatisticsRepository.loadCoronaVirusStatsChart("Dublin");

        //LiveData object containing response
        LiveData<List<DataEntry>> events =  covidStatisticsRepository.getMutableCovidGraphStats();

        TestObserver.test(events)
                .awaitValue()
                .assertHasValue();
    }

}