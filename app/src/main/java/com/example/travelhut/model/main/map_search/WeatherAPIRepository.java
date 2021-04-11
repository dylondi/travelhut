package com.example.travelhut.model.main.map_search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.travelhut.model.utils.Common;
import com.example.travelhut.model.utils.StringsRepository;
import com.example.travelhut.views.main.map_search.utils.DownloadStringFromURL;
import com.example.travelhut.model.objects.Weather;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class WeatherAPIRepository extends LiveData<Weather> {

    //Instance Variable
    private MutableLiveData<Weather> weatherMutableLiveData;
    private final Long kelvToCel = 273L;

    //Constructor
    public WeatherAPIRepository() {
        weatherMutableLiveData = new MutableLiveData<>();
    }

    //This method starts a new thread on which it sends a request to a weather API and posts the resulting data to weatherMutableLiveData
    public void loadWeatherData(double lat, double lon) {

        //Create thread
        new Thread(() -> {

            //Get weather API key
            String weatherAPI = Common.WEATHER_API_KEY;

            //Create string containing complete url for sending request
            String url = StringsRepository.WEATHER_API_URL_BASE + lat + "&lon=" + lon + "&exclude={part}&appid=" + weatherAPI;

            //Create and initialize a DownloadStringFromURL object
            DownloadStringFromURL downloadStringFromURL = new DownloadStringFromURL();

            String localTime;
            try {
                String result = "";

                //Get result from executing the request
                result = downloadStringFromURL.execute(url).get();

                //Create a JSONObject from this result
                JSONObject jsonObject = new JSONObject(result);

                //Store relevant data from response -> temperature (convert from kelvin to celsius), humidity, time, weather icon
                JSONObject main = jsonObject.getJSONObject(StringsRepository.CURRENT);
                String temp = main.getString(StringsRepository.TEMP);
                String humidity = main.getString(StringsRepository.HUMIDITY);
                Long time = main.getLong(StringsRepository.DT);
                Long timeShift = jsonObject.getLong(StringsRepository.TIMEZONE_OFFSET);
                Long kelvin = (Double.valueOf(Double.parseDouble(temp))).longValue();

                //String containing the time in the selected location in hh:mm format
                localTime = new SimpleDateFormat("hh:mm", Locale.ENGLISH)
                        .format(new Date((time + timeShift) * 1000));

                //Get weather icon info from response in a string
                String nameIcon = main.getJSONArray(StringsRepository.WEATHER).getJSONObject(0).getString(StringsRepository.ICON);

                String urlIcon = StringsRepository.WEATHER_API_URL_ICON + nameIcon + "@2x.png";

                //Post a Weather object containing data from response to the weatherMutableLiveData object
                weatherMutableLiveData.postValue(new Weather(localTime, kelvin - kelvToCel, humidity, urlIcon));

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //This method returns weatherMutableLiveData object containing a Weather object
    public MutableLiveData<Weather> getWeatherMutableLiveData() {
        return weatherMutableLiveData;
    }
}
