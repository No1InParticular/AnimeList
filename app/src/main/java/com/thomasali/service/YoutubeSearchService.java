package com.thomasali.service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class YoutubeSearchService extends AbstractService {

    private String query;
    private JSONArray results;

    public YoutubeSearchService(String query) {
        try {
            this.query = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }

    public JSONArray getResults() {
        return results;
    }

    @Override
    public void run() {

        URL url;
        boolean error = false;
        HttpURLConnection httpURLConnection = null;
        StringBuilder result = new StringBuilder();

        try {
            results = null;

            String urlString = String.format("https://www.googleapis.com/youtube/v3/search?part=snippet&q=%s Trailer&key=AIzaSyDrtQpoLy-Rhg-4Td-rmsvFjDC0lRcPk3c", query);

            url = new URL(urlString);
            httpURLConnection = (HttpURLConnection)url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

            String line;
            while((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }

            JSONObject jsonObject = new JSONObject(result.toString());

            results = jsonObject.getJSONArray("items");
        } catch (FileNotFoundException ex) {
            results = new JSONArray();
        } catch (Exception ex) {
            ex.printStackTrace();
            results = null;
            error = true;
        } finally {
            if(httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        super.serviceCallComplete(error);
    }
}
