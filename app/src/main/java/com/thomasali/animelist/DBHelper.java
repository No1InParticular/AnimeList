package com.thomasali.animelist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "AnimeList";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table if not exists Anime (id integer, url text, imageurl text, title text, airing integer, synopsis text, agerating text, episode integer, primary key (id))"
        );
        db.execSQL(
                "create table if not exists Episode (id integer primary key autoincrement, animeID integer, episodeNum integer, title text, url text, foreign key (animeID) REFERENCES Anime(id))"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS Anime");
        db.execSQL("DROP TABLE IF EXISTS Episode");
        onCreate(db);
    }

    public boolean insertAnime (int id, String url, String image_url, String title, int airing, String synopsis, String age_rating, int episode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        contentValues.put("url", url);
        contentValues.put("imageurl", image_url);
        contentValues.put("title", title);
        contentValues.put("airing", airing);
        contentValues.put("synopsis", synopsis);
        contentValues.put("agerating", age_rating);
        contentValues.put("episode", episode);
        db.insert("Anime", null, contentValues);

        return true;
    }

    public void updateAnime(int id, int episode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("episode", episode);
        db.update("Anime", contentValues, "ID=?", new String[] {Integer.toString(id)});
    }

    public void insertEpisodes(List<AnimeContent.Episode> episodes) {
        SQLiteDatabase db = this.getWritableDatabase();

        if(episodes != null && !episodes.isEmpty()) {
            for (AnimeContent.Episode episode : episodes) {
                ContentValues contentValues1 = new ContentValues();
                contentValues1.put("animeID", episode.animeID);
                contentValues1.put("episodeNum", episode.episodeNum);
                contentValues1.put("title", episode.title);
                contentValues1.put("url", episode.url);
                db.insert("episode", null, contentValues1);
            }
            MainActivity.setSavedEpisodes(episodes.get(0).animeID, episodes);
        }
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from ANIME where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, "Anime");
        return numRows;
    }

    public Integer deleteContact (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("EPISODE", "animeID = ?", new String[] { Integer.toString(id) });
        return db.delete("ANIME","id = ? ", new String[] { Integer.toString(id) });
    }

    public ArrayList<AnimeContent.Anime> getAllAnime() {
        ArrayList<AnimeContent.Anime> anime_list = new ArrayList<AnimeContent.Anime>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from ANIME", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){

            int id = res.getInt(res.getColumnIndex("id"));
            String url = res.getString(res.getColumnIndex("url"));
            String image_url = res.getString(res.getColumnIndex("imageurl"));
            String title = res.getString(res.getColumnIndex("title"));
            int airing = res.getInt(res.getColumnIndex("airing"));
            boolean boolAiring = false;
            if(airing == 1) { boolAiring = true; }
            String synopsis = res.getString(res.getColumnIndex("synopsis"));
            String age_rating = res.getString(res.getColumnIndex("agerating"));
            int episode = res.getInt(res.getColumnIndex("episode"));

            AnimeContent.Anime anime = new AnimeContent.Anime(id, url, image_url, title, boolAiring, synopsis, age_rating, episode);
            anime_list.add(anime);
            res.moveToNext();
        }
        return anime_list;
    }

    public List<AnimeContent.Episode> getEpisodes(int animeID) {
        ArrayList<AnimeContent.Episode> episode_list = new ArrayList<AnimeContent.Episode>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from EPISODE WHERE animeID = ?", new String[]{animeID+""});
        res.moveToFirst();

        while(res.isAfterLast() == false){

            int animeID2 = res.getInt(res.getColumnIndex("animeID"));
            int episodeID = res.getInt(res.getColumnIndex("episodeNum"));
            String title = res.getString(res.getColumnIndex("title"));
            String url = res.getString(res.getColumnIndex("url"));

            AnimeContent.Episode episode = new AnimeContent.Episode(animeID2, episodeID, title, url);
            episode_list.add(episode);
            res.moveToNext();
        }
        return episode_list;
    }
}

