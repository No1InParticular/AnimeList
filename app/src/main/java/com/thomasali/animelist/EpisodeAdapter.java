package com.thomasali.animelist;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class EpisodeAdapter extends ArrayAdapter<String> {

    public EpisodeAdapter(Context context, int resource, List<String> objects){
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Work around for Android re-using views, need to check if it should be green or white and re-set the colour

        View itemView = super.getView(position, convertView, parent);

        if(convertView != null) {
            itemView = convertView;;
        }

        Context context = getContext();
        if(context instanceof AnimeViewActivity) {

            AnimeViewActivity animeViewActivity = (AnimeViewActivity)context;

            int episodePos = animeViewActivity.selectedAnime.episode-1;

            if(position > episodePos) {
                itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            } else {
                itemView.setBackgroundColor(Color.parseColor("#98FB98"));
            }

        }
        return itemView;
    }
}
