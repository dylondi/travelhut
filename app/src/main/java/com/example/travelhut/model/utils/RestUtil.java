package com.example.travelhut.model.utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RestUtil {

    //Static method which takes a url parameter and returns a JSONObject from the request if successful
    public static JSONObject getJSONObject(String _url) throws Exception {

        //Check for empty URL
        if (_url.equals(StringsRepository.EMPTY_STRING))
            throw new Exception(StringsRepository.URL_CANT_BE_EMPTY);

        //Create URL object containing URL string
        URL url = new URL(_url);

        //Create HttpURLConnection object and init with URL object
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        //Config HttpURLConnection object
        urlConnection.setReadTimeout(10000);
        urlConnection.setConnectTimeout(15000);
        urlConnection.setDoInput(true);
        urlConnection.setRequestProperty(StringsRepository.USER_AGENT, StringsRepository.ANDROID);
        urlConnection.setRequestProperty(StringsRepository.ACCEPT_CAP, StringsRepository.APPLICATION_JSON);
        urlConnection.addRequestProperty(StringsRepository.CONTENT_TYPE, StringsRepository.APPLICATION_JSON);

        //Init BufferedReader with an new InputStreamReader object getting the input stream of HHttpURLConnection object
        BufferedReader in = new BufferedReader(
                new InputStreamReader(urlConnection.getInputStream()));


        if (!url.getHost().equals(urlConnection.getURL().getHost())) {
            urlConnection.disconnect();
            return new JSONObject();
        }

        //Create a String and StringBuilder
        String inputLine;
        StringBuilder response = new StringBuilder();

        //While there is data in the inputLine
        while ((inputLine = in.readLine()) != null) {

            //Append data to response JSONObject
            response.append(inputLine);
        }
        in.close();
        urlConnection.disconnect();

        return new JSONObject(response.toString());

    }
}
