package com.thomasali.animelist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class holding all Anime item stuff
 */
public class AnimeContent {

    public static final List<Anime> ITEMS = new ArrayList<Anime>();

    public static final Map<Integer, Anime> ITEM_MAP = new HashMap<Integer, Anime>();

    public static void addItem(Anime item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static void clearList() {
        ITEMS.clear();
        ITEM_MAP.clear();
    }

    /**
     * The Anime Item itself
     */
    public static class Anime {
        public final int id;
        public final String url;
        public final String image_url;
        public final String title;
        public final boolean airing;
        public final String synopsis;
        public final String age_rating;
        public int episode;


        public Anime(int id, String url, String image_url, String title, boolean airing, String synopsis, String age_rating, int episode) {
            this.id = id;
            this.url = url;
            this.image_url = image_url;
            this.title = title;
            this.airing = airing;
            this.synopsis = synopsis;
            this.age_rating = age_rating;
            this.episode = episode;
        }
    }

    public static class Episode {
        public final int animeID;
        public final int episodeNum;
        public final String title;
        public final String url;

        public Episode(int animeID, int episodeNum, String title, String url) {
            this.animeID = animeID;
            this.episodeNum = episodeNum;
            this.title = title;
            this.url = url;
        }
    }
}
