package com.thomasali.animelist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class Bookmarked extends AppCompatActivity  implements AnimeFragment.OnListFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarked);

        AnimeFragment animeFragment = (AnimeFragment) getSupportFragmentManager().findFragmentById(R.id.bookmarked_results);
        if (animeFragment != null) {
            animeFragment.setItems(MainActivity.getBookmarkedAnime());
        }
        setTitle("My Bookmarked");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Just to refresh the list when coming back from the view
        AnimeFragment animeFragment = (AnimeFragment) getSupportFragmentManager().findFragmentById(R.id.bookmarked_results);
        if (animeFragment != null) {
            animeFragment.setItems(MainActivity.getBookmarkedAnime());
        }
    }

    @Override
    public void onListFragmentInteraction(AnimeContent.Anime item) {

    }
}
