package com.example.travelhut.views.main.map_search;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadStringFromURL extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... strings) {

        //Method Variables
        URL url;
        HttpURLConnection httpURLConnection;
        InputStream inputStream;
        InputStreamReader inputStreamReader;
        String result = "";

        try {
            //Get first URL from array of Strings
            url = new URL(strings[0]);

            //Initialize HttpURLConnection, InputStream, and InputStreamReader objects
            httpURLConnection = (HttpURLConnection) url.openConnection();
            inputStream = httpURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);

            //data integer set to read from the inputStreamReader
            int data = inputStreamReader.read();

            //While end of stream has not been reached
            while(data != -1){
                //Append the next char of data to result String
                result += (char) data;

                //data equal to remainder of response
                data = inputStreamReader.read();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}