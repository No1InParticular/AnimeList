package com.thomasali.animelist;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static List<AnimeContent.Anime> bookmarkedAnime;
    private static HashMap<Integer, List<AnimeContent.Episode>> savedEpisodes;
    public static DBHelper dbHelper;
    static String currentActivity = "Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        dbHelper = new DBHelper(this);

        if(bookmarkedAnime == null) {
            bookmarkedAnime = new ArrayList<AnimeContent.Anime>();
            bookmarkedAnime.addAll(dbHelper.getAllAnime());
        }
        if(savedEpisodes == null) {
            savedEpisodes = new HashMap<Integer, List<AnimeContent.Episode>>();
            for(AnimeContent.Anime anime : bookmarkedAnime) {
                for(AnimeContent.Episode episode : dbHelper.getEpisodes(anime.id)) {
                    if(savedEpisodes.get(anime.id) == null) {
                        savedEpisodes.put(anime.id, new ArrayList<AnimeContent.Episode>());
                    }
                    savedEpisodes.get(anime.id).add(episode);
                }
            }
        }

        findViewById(R.id.btnSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentActivity = "Search";
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btnBookmarked).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentActivity = "Bookmarks";
                Intent intent = new Intent(getApplicationContext(), Bookmarked.class);
                startActivity(intent);
            }
        });
    }

    public static boolean addBookmarked(AnimeContent.Anime anime) {
        boolean alreadyBM = false;
        for(AnimeContent.Anime am : bookmarkedAnime) {
            if(am.id == anime.id) {
                alreadyBM = true;
                break;
            }
        }

        if(!alreadyBM) {
            bookmarkedAnime.add(anime);
            int airing = 0;
            if(anime.airing) {
                airing = 1;
            }
            dbHelper.insertAnime(anime.id, anime.url, anime.image_url, anime.title, airing, anime.synopsis, anime.age_rating, anime.episode);
            return true;
        } else {
            return false;
        }
    }

    public static void updateEpisodeNumber(AnimeContent.Anime anime) {
        dbHelper.updateAnime(anime.id, anime.episode);
    }


    public static boolean removeBookmarked(AnimeContent.Anime anime) {
        Iterator<AnimeContent.Anime> aList = bookmarkedAnime.listIterator();
        while(aList.hasNext()) {
            AnimeContent.Anime am = aList.next();
            if(am.id == anime.id) {
                aList.remove();
                dbHelper.deleteContact(anime.id);
                return true;
            }
        }

        return false;
    }
    public static boolean isBookmarked(AnimeContent.Anime anime) {
        for(AnimeContent.Anime bmAnime : bookmarkedAnime) {
            if(bmAnime.id == anime.id) {
                return true;
            }
        }
        return false;
    }

    public static boolean isBookmarked(int id) {
        for(AnimeContent.Anime bmAnime : bookmarkedAnime) {
            if(bmAnime.id == id) {
                return true;
            }
        }
        return false;
    }

    public static AnimeContent.Anime getAnime(int id) {

        for(AnimeContent.Anime bmAnime : bookmarkedAnime) {
            if(bmAnime.id == id) {
                return bmAnime;
            }
        }
        return null;
    }

    public static List<AnimeContent.Anime> getBookmarkedAnime() {
        return bookmarkedAnime;
    }

    public static List<AnimeContent.Episode> getSavedEpisodes(int animeID) {
        if(savedEpisodes.get(animeID) == null) {
            return new ArrayList<AnimeContent.Episode>();
        } else {
            savedEpisodes.remove(animeID);
        }

        savedEpisodes.put(animeID, dbHelper.getEpisodes(animeID));

        return savedEpisodes.get(animeID);
    }

    public static void setSavedEpisodes(int animeID, List<AnimeContent.Episode> episodes) {
        savedEpisodes.put(animeID, episodes);
    }
}
