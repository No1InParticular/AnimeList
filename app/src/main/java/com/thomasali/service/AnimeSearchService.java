package com.thomasali.service;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class AnimeSearchService extends AbstractService {

    private String query;
    private String status;
    private String rated;
    private int page;
    private JSONArray results;

    public AnimeSearchService(String query, String status, String rated, int page) {
        try {
            this.query = URLEncoder.encode(query, "UTF-8");
            this.status = URLEncoder.encode(status, "UTF-8");
            this.rated = URLEncoder.encode(rated, "UTF-8");
            this.page = page;
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

            String urlString = String.format("https://api.jikan.moe/v3/search/anime?q=%s&limit=10&page=%d", query, page);

            if(status != "Not Specified") {
                urlString += "&status=" + status;
            }
            if(rated != "Not Specified") {
                urlString += "&rated=" + rated;
            }


            url = new URL(urlString);
            httpURLConnection = (HttpURLConnection)url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

            String line;
            while((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }

            JSONObject jsonObject = new JSONObject(result.toString());

            results = jsonObject.getJSONArray("results");
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
