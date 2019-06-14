package com.thomasali.animelist;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.thomasali.animelist.AnimeFragment.OnListFragmentInteractionListener;
import com.thomasali.animelist.AnimeContent.Anime;

import java.util.List;


public class MyAnimeRecyclerViewAdapter extends RecyclerView.Adapter<MyAnimeRecyclerViewAdapter.ViewHolder> {

    private final List<Anime> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Context context;
    private View view;

    public MyAnimeRecyclerViewAdapter(List<Anime> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public MyAnimeRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_anime, parent, false);
        context = view.getContext();
        return new MyAnimeRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        final Anime current = mValues.get(position);

        Picasso.get().load(current.image_url).into(holder.mImageView);
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Open Web Page?");
                builder.setMessage("Do you want to open the website for this Anime?.");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uriUrl = Uri.parse(current.url);
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        context.startActivity(launchBrowser);
                    }
                });

                builder.setNegativeButton("No",null);

                builder.show();

            }
        });

        holder.mTitleView.setText(current.title);

        if(current.synopsis.length() > 50) {
            holder.mDescriptionView.setText(current.synopsis.substring(0, 47).concat("..."));
        } else {
            holder.mDescriptionView.setText(current.synopsis.replace("[Written by MAL Rewrite]", ""));
        }

        if(MainActivity.isBookmarked(current)) {
            holder.mBookmarkImage.setImageResource(R.drawable.baseline_bookmark_24);
        } else {
            holder.mBookmarkImage.setImageResource(R.drawable.baseline_bookmark_border_24);
        }

        holder.mBookmarkImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.isBookmarked(current)) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Remove bookmark?");
                    builder.setMessage("Are you sure you want to remove the bookmark on \"" + current.title +  "\"? Doing so will clear the current episode counter for this Anime.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(MainActivity.removeBookmarked(current)) {
                                holder.mBookmarkImage.setImageResource(R.drawable.baseline_bookmark_border_24);
                                Toast.makeText(context, "Bookmark removed!", Toast.LENGTH_SHORT).show();
                                if(MainActivity.currentActivity == "Bookmarks") {
                                    RecyclerView rView = ((Activity) context).findViewById(R.id.bookmarked_results);
                                    rView.setAdapter(new MyAnimeRecyclerViewAdapter(MainActivity.getBookmarkedAnime(), mListener));
                                }
                            } else {
                                Toast.makeText(context, "Could not remove bookmark...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    builder.setNegativeButton("No",null);

                    builder.show();

                } else {
                    if(MainActivity.addBookmarked(current)) {
                        holder.mBookmarkImage.setImageResource(R.drawable.baseline_bookmark_24);
                        Toast.makeText(context, "Bookmark added!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Could not add bookmark...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);

                    Intent intent = new Intent(context, AnimeViewActivity.class);
                    intent.putExtra("ANIME_ID", holder.mItem.id);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public final TextView mTitleView;
        public final TextView mDescriptionView;
        public final ImageView mBookmarkImage;
        public Anime mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.item_image);
            mTitleView = (TextView) view.findViewById(R.id.item_title);
            mDescriptionView = (TextView) view.findViewById(R.id.item_description);
            mBookmarkImage = (ImageView) view.findViewById(R.id.bm_image);
        }
    }
}
