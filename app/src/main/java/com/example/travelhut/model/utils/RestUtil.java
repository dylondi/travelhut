package com.example.travelhut.model.utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RestUtil {


    public static JSONObject getJSONObject(String _url) throws Exception {
        if (_url.equals(StringsRepository.EMPTY_STRING))
            throw new Exception(StringsRepository.URL_CANT_BE_EMPTY);

        URL url = new URL(_url);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setReadTimeout(10000);
        urlConnection.setConnectTimeout(15000);
        urlConnection.setDoInput(true);
        urlConnection.setRequestProperty(StringsRepository.USER_AGENT, StringsRepository.ANDROID);
        urlConnection.setRequestProperty(StringsRepository.ACCEPT_CAP, StringsRepository.APPLICATION_JSON);
        urlConnection.addRequestProperty(StringsRepository.CONTENT_TYPE, StringsRepository.APPLICATION_JSON);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(urlConnection.getInputStream()));

        if (!url.getHost().equals(urlConnection.getURL().getHost())) {
            urlConnection.disconnect();
            return new JSONObject();
        }
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        urlConnection.disconnect();

        return new JSONObject(response.toString());

    }
}
