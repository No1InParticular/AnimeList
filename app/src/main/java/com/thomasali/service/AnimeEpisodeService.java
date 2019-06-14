package com.thomasali.service;

import android.util.Log;

import com.thomasali.animelist.AnimeContent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AnimeEpisodeService extends AbstractService {

    private int animeID;
    private List<AnimeContent.Episode> results;

    public AnimeEpisodeService(int animeID) {

        this.animeID = animeID;

    }

    public List<AnimeContent.Episode> getResult() {
        return results;
    }

    @Override
    public void run() {

        URL url;
        boolean error = false;
        HttpURLConnection httpURLConnection = null;
        StringBuilder resultString = new StringBuilder();

        try {
            results = null;

            String urlString = String.format("https://api.jikan.moe/v3/anime/%d/episodes", animeID);


            url = new URL(urlString);
            httpURLConnection = (HttpURLConnection)url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

            String line;
            while((line = bufferedReader.readLine()) != null) {
                resultString.append(line);
            }

            JSONObject jsonObject = new JSONObject(resultString.toString());
            JSONArray episodeList = jsonObject.getJSONArray("episodes");

            results = new ArrayList<AnimeContent.Episode>();

            for(int i = 0; i < episodeList.length(); i++) {
                JSONObject episodeInfo = episodeList.getJSONObject(i);
                if(episodeInfo != null) {
                    AnimeContent.Episode episode = new AnimeContent.Episode(animeID, episodeInfo.getInt("episode_id"), episodeInfo.getString("title"), episodeInfo.getString("video_url"));
                    results.add(episode);
                } else {
                    break;
                }
            }

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            results = null;
            error = true;
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
