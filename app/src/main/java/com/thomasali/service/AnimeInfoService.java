package com.thomasali.service;

import android.util.Log;

import com.thomasali.animelist.AnimeContent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class AnimeInfoService extends AbstractService {

    private int animeID;
    private AnimeContent.Anime result;

    public AnimeInfoService(int animeID) {

        this.animeID = animeID;

    }

    public AnimeContent.Anime getResult() {
        return result;
    }

    @Override
    public void run() {

        URL url;
        boolean error = false;
        HttpURLConnection httpURLConnection = null;
        StringBuilder resultString = new StringBuilder();

        try {
            result = null;

            String urlString = String.format("https://api.jikan.moe/v3/anime/%d", animeID);


            url = new URL(urlString);
            httpURLConnection = (HttpURLConnection)url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

            String line;
            while((line = bufferedReader.readLine()) != null) {
                resultString.append(line);
            }

            JSONObject jsonObject = new JSONObject(resultString.toString());

            AnimeContent.Anime anime = new AnimeContent.Anime(
                    animeID,
                    jsonObject.getString("url"),
                    jsonObject.getString("image_url"),
                    jsonObject.getString("title"),
                    jsonObject.getBoolean("airing"),
                    jsonObject.getString("synopsis"),
                    jsonObject.getString("rating"),
                    0);

            result = anime;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            result = null;
            error = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            result = null;
            error = true;
        } finally {
            if(httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        super.serviceCallComplete(error);
    }
}
