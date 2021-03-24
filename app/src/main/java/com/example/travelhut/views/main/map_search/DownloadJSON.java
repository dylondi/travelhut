package com.example.travelhut.views.main.map_search;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadJSON extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... strings) {

        URL url;
        HttpURLConnection httpURLConnection;
        InputStream inputStream;
        InputStreamReader inputStreamReader;
        String result = "";

        try {
            url = new URL(strings[0]);
            httpURLConnection = (HttpURLConnection) url.openConnection();

            inputStream = httpURLConnection.getInputStream();

            inputStreamReader = new InputStreamReader(inputStream);

            int data = inputStreamReader.read();

            while(data != -1){
                result += (char) data;
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