package com.thomasali.animelist;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.thomasali.service.AbstractService;
import com.thomasali.service.AnimeEpisodeService;
import com.thomasali.service.AnimeInfoService;
import com.thomasali.service.ServiceListener;
import com.thomasali.service.YoutubeSearchService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class AnimeViewActivity extends AppCompatActivity implements ServiceListener {

    Context con;

    ProgressDialog progressDialog;
    Thread thread;
    Thread thread2;

    AnimeContent.Anime selectedAnime;
    int animeID;

    ImageView imageView;
    TextView title;
    TextView synopsis;
    TextView rating;
    TextView status;
    TextView episode;

    TextView btnMinusEpisode;
    TextView btnAddEpisode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_view);


        con = this;

        imageView = findViewById(R.id.avImage);
        title = findViewById(R.id.avTitle);
        synopsis = findViewById(R.id.avSynopsis);
        rating = findViewById(R.id.avRating);
        status = findViewById(R.id.avStatus);
        episode = findViewById(R.id.avEpisodeCount);

        btnMinusEpisode = findViewById(R.id.avLowerEpisode);
        btnAddEpisode = findViewById(R.id.avIncreaseEpisode);





        animeID = getIntent().getIntExtra("ANIME_ID", 0);
        if(animeID > 0) {
            if(MainActivity.isBookmarked(animeID)) {
                selectedAnime = MainActivity.getAnime(animeID);
            }
        }

        if(MainActivity.isBookmarked(animeID)) {
            btnMinusEpisode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(selectedAnime.episode > 0) {
                        selectedAnime.episode--;
                        episode.setText(String.format("%d", selectedAnime.episode));
                        performAnimation(selectedAnime.episode+1, true);
                        MainActivity.updateEpisodeNumber(selectedAnime);
                    }
                }
            });
            btnAddEpisode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int episodeCount = MainActivity.getSavedEpisodes(animeID).size();
                    if(selectedAnime.episode < episodeCount) {
                        selectedAnime.episode++;
                        episode.setText(String.format("%d", selectedAnime.episode));
                        performAnimation(selectedAnime.episode, false);
                        MainActivity.updateEpisodeNumber(selectedAnime);
                    }
                }
            });
        } else {
            episode.setVisibility(View.INVISIBLE);
            btnMinusEpisode.setVisibility(View.INVISIBLE);
            btnAddEpisode.setVisibility(View.INVISIBLE);
        }

        if(selectedAnime != null) {
            // It is bookmarked so grab from object
            Picasso.get().load(selectedAnime.image_url).into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(con);
                    builder.setTitle("Open Web Page?");
                    builder.setMessage("Do you want to open the website for this Anime?.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Uri uriUrl = Uri.parse(selectedAnime.url);
                            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                            startActivity(launchBrowser);
                        }
                    });

                    builder.setNegativeButton("No",null);

                    builder.show();
                }
            });

            title.setText(selectedAnime.title);
            synopsis.setText(selectedAnime.synopsis);
            rating.setText(selectedAnime.age_rating);
            if(selectedAnime.airing) {
                status.setText("Airing");
            } else {
                status.setText("Not Airing");
            }
            episode.setText(String.format("%d", selectedAnime.episode));
            for(int i = 1; i<=selectedAnime.episode; i++) {
                performAnimation(i, false);
            }

        } else {
            // Its not so run the Service (needs to be created)
            showProgressDialog();
            AnimeInfoService service = new AnimeInfoService(animeID);
            service.addListener(this);
            thread = new Thread(service);
            thread.start();

        }

        // Episode stuff
        if(selectedAnime != null && MainActivity.getSavedEpisodes(selectedAnime.id) != null && !MainActivity.getSavedEpisodes(selectedAnime.id).isEmpty()) {

            ListView avEpisodes = findViewById(R.id.avEpisodes);

            final List<String> episodeNames = new ArrayList<>();
            int i = 1;
            for (AnimeContent.Episode e : MainActivity.getSavedEpisodes(selectedAnime.id)) {
                episodeNames.add("#" + i + " - " + e.title);
                i++;
            }

            EpisodeAdapter adapter = new EpisodeAdapter(this, android.R.layout.simple_list_item_1, episodeNames);
            avEpisodes.setAdapter(adapter);

        } else {
            Toast.makeText(this, "Loading episodes...", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AnimeEpisodeService service = new AnimeEpisodeService(animeID);
                    service.addListener(AnimeViewActivity.this);
                    thread = new Thread(service);
                    thread.start();
                }
            }, 2000);

        }
        Toolbar searchToolbar = findViewById(R.id.animeViewToolbar);
        setSupportActionBar(searchToolbar);

        if(selectedAnime != null) {
            YoutubeSearchService service = new YoutubeSearchService(selectedAnime.title);
            service.addListener(this);
            thread2 = new Thread(service);
            thread2.start();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.animeview_menu, menu);
        final MenuItem bookmarkButton = menu.findItem(R.id.action_bookmark);


        if(!MainActivity.isBookmarked(animeID)) {
            bookmarkButton.setIcon(R.drawable.baseline_bookmark_border_24);
        }

        bookmarkButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(MainActivity.isBookmarked(animeID)) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(AnimeViewActivity.this);
                    builder.setTitle("Remove bookmark?");
                    builder.setMessage("Are you sure you want to remove the bookmark on \"" + selectedAnime.title +  "\"? Doing so will clear the current episode counter for this Anime.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(MainActivity.removeBookmarked(selectedAnime)) {
                                bookmarkButton.setIcon(R.drawable.baseline_bookmark_border_24);
                                Toast.makeText(getApplicationContext(), "Bookmark removed!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Could not remove bookmark...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    builder.setNegativeButton("No",null);

                    builder.show();

                } else {
                    if(MainActivity.addBookmarked(selectedAnime)) {
                        bookmarkButton.setIcon(R.drawable.baseline_bookmark_24);
                        Toast.makeText(getApplicationContext(), "Bookmark added!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Could not add bookmark...", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void serviceComplete(AbstractService abstractService) {
        hideProgressDialog();
        if(abstractService instanceof AnimeInfoService) {
            if(abstractService.hasError()) {
                Toast.makeText(con, "Could not load anime info, please try again.", Toast.LENGTH_SHORT).show();
                return;
            }
            selectedAnime = ((AnimeInfoService) abstractService).getResult();

            Picasso.get().load(selectedAnime.image_url).into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(con);
                    builder.setTitle("Open Web Page?");
                    builder.setMessage("Do you want to open the website for this Anime?.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Uri uriUrl = Uri.parse(selectedAnime.url);
                            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                            startActivity(launchBrowser);
                        }
                    });

                    builder.setNegativeButton("No",null);

                    builder.show();
                }
            });

            title.setText(selectedAnime.title);
            synopsis.setText(selectedAnime.synopsis);
            rating.setText(selectedAnime.age_rating);
            if(selectedAnime.airing) {
                status.setText("Airing");
            } else {
                status.setText("Not Airing");
            }

            episode.setText(String.format("%d", selectedAnime.episode));

            YoutubeSearchService service = new YoutubeSearchService(selectedAnime.title);
            service.addListener(this);
            thread2 = new Thread(service);
            thread2.start();
        } else if (abstractService instanceof AnimeEpisodeService) {

            if(abstractService.hasError()) {
                Toast.makeText(con, "Could not load episode info, please try again.", Toast.LENGTH_SHORT).show();
                return;
            }

            final List<AnimeContent.Episode> episodes = ((AnimeEpisodeService) abstractService).getResult();

            if(MainActivity.isBookmarked(animeID)) {
                MainActivity.dbHelper.insertEpisodes(episodes);
            }

            final List<String> episodeNames = new ArrayList<>();
            if(episodes != null && !episodes.isEmpty()) {
               int i = 1;
               for (AnimeContent.Episode e : episodes) {
                   episodeNames.add("#" + i + " - " + e.title);
                   i++;
               }
            }

            ListView lvEpisodes = findViewById(R.id.avEpisodes);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, episodeNames);
            lvEpisodes.setAdapter(adapter);

        } else if (abstractService instanceof YoutubeSearchService) {

            if(abstractService.hasError()) {
                Toast.makeText(con, "Could not load trailer information, please try again.", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONArray videos = ((YoutubeSearchService) abstractService).getResults();

            TextView title1 = findViewById(R.id.trailerText1);
            ImageView image1 = findViewById(R.id.trailerImage1);

            try {
                JSONObject video = videos.getJSONObject(0);
                final String id = video.getJSONObject("id").getString("videoId");
                String title = video.getJSONObject("snippet").getString("title");
                String imageUrl = video.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("medium").getString("url");

                title1.setText(title);
                Picasso.get().load(imageUrl).into(image1);

                final AlertDialog.Builder builder1 = new AlertDialog.Builder(AnimeViewActivity.this);
                builder1.setTitle("Open Youtube video?");
                builder1.setMessage("Are you sure you want to open this link? \nNote: The app holds no responsibility for these videos.");
                builder1.setCancelable(false);
                builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openYoutubeIntent(AnimeViewActivity.this, id);
                    }
                });
                builder1.setNegativeButton("No",null);

                View.OnClickListener onClick = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        builder1.show();
                    }
                };

                title1.setOnClickListener(onClick);
                image1.setOnClickListener(onClick);

            } catch (JSONException e) {
                Toast.makeText(con, "An error occurred parsing youtube response for first video", Toast.LENGTH_SHORT).show();
                Log.e("Anime", e.getMessage());
            }


            TextView title2 = findViewById(R.id.trailerText2);
            ImageView image2 = findViewById(R.id.trailerImage2);

            try {
                JSONObject video = videos.getJSONObject(1);
                final String id = video.getJSONObject("id").getString("videoId");
                String title = video.getJSONObject("snippet").getString("title");
                String imageUrl = video.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("medium").getString("url");

                title2.setText(title);
                Picasso.get().load(imageUrl).into(image2);

                final AlertDialog.Builder builder1 = new AlertDialog.Builder(AnimeViewActivity.this);
                builder1.setTitle("Open Youtube video?");
                builder1.setMessage("Are you sure you want to open this link? \nNote: The app holds no responsibility for these videos.");
                builder1.setCancelable(false);
                builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openYoutubeIntent(AnimeViewActivity.this, id);
                    }
                });
                builder1.setNegativeButton("No",null);

                View.OnClickListener onClick = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        builder1.show();
                    }
                };

                title2.setOnClickListener(onClick);
                image2.setOnClickListener(onClick);

            } catch (JSONException e) {
                Toast.makeText(con, "An error occurred parsing youtube response for second video", Toast.LENGTH_SHORT).show();
                Log.e("Anime", e.getMessage());
            }
        }
    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(true);
        }

        progressDialog.show();
    }


    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void openYoutubeIntent(Context context, String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    private void performAnimation(final int episode, boolean reverse) {
        int colorFrom = getResources().getColor(R.color.white);
        int colorTo = getResources().getColor(R.color.green);
        ValueAnimator colorAnimation;
        if(reverse) {
            colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorTo, colorFrom);
        } else {
            colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        }
        colorAnimation.setDuration(300); // milliseconds

        final ListView lvEpisodes = findViewById(R.id.avEpisodes);

        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int first = lvEpisodes.getFirstVisiblePosition();
                int last = lvEpisodes.getLastVisiblePosition();
                int episodePos = episode-1;
                if(first <= episodePos && episodePos <= last)
                    lvEpisodes.getChildAt(episodePos-lvEpisodes.getFirstVisiblePosition()).setBackgroundColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();
    }
}
