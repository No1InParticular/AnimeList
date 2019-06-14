package com.thomasali.animelist;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.thomasali.animelist.AnimeContent.Anime;
import com.thomasali.service.AbstractService;
import com.thomasali.service.AnimeSearchService;
import com.thomasali.service.ServiceListener;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for Anime Search
 */
public class AnimeFragment  extends Fragment implements ServiceListener {

    ProgressDialog progressDialog;

    private Thread thread;
    private RecyclerView rView;
    private MyAnimeRecyclerViewAdapter rAdapter;
    public boolean noResults = false;

    private static final String SEARCH_QUERY = "SEARCH_QUERY";
    private static final String SEARCH_STATUS = "SEARCH_STATUS";
    private static final String SEARCH_RATED = "SEARCH_RATED";
    private static final String SEARCH_PAGE = "SEARCH_PAGE";

    private String mSearchQuery = "";
    private String mSearchStatus = "Not Specified";
    private String mSearchRated = "Not Specified";
    private Integer mSearchPage = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment
     */
    public AnimeFragment() {
    }

    public static AnimeFragment newInstance(String query, String status, String rated, int page) {
        AnimeFragment fragment = new AnimeFragment();
        Bundle args = new Bundle();
        args.putString(SEARCH_QUERY, query);
        args.putString(SEARCH_STATUS, status);
        args.putString(SEARCH_RATED, status);
        args.putInt(SEARCH_PAGE, page);
        fragment.setArguments(args);

        return fragment;
    }

    public void search(String query, String status, String rated, int page) {
        if(!query.isEmpty()) {
            AnimeSearchService service = new AnimeSearchService(query, status, rated, page);
            service.addListener(this);
            thread = new Thread(service);
            showProgressDialog();
            thread.start();
        } else {
            // Only show the toast if something is not being searched for
            noResults = true;
            Toast.makeText(getContext(), "Enter a search term to get started", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mSearchQuery = getArguments().getString(SEARCH_QUERY);
            mSearchStatus = getArguments().getString(SEARCH_STATUS);
            mSearchRated = getArguments().getString(SEARCH_RATED);
            mSearchPage = getArguments().getInt(SEARCH_PAGE);
            search(mSearchQuery, mSearchStatus, mSearchRated, mSearchPage);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anime_list, container, false);

        rView = (RecyclerView) view.findViewById(R.id.fragment_anime_list_view);
        rView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rAdapter = new MyAnimeRecyclerViewAdapter(AnimeContent.ITEMS, mListener);
        rView.setAdapter(rAdapter);
        return view;
    }

    public void refreshSearch() {
        rAdapter = new MyAnimeRecyclerViewAdapter(AnimeContent.ITEMS, mListener);
        rView.setAdapter(rAdapter);
    }

    public void setItems(List<Anime> anime) {
        rAdapter = new MyAnimeRecyclerViewAdapter(anime, mListener);
        rView.setAdapter(rAdapter);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Anime item);
    }

    public void serviceComplete(AbstractService abstractService) {
        hideProgressDialog();
        if(!abstractService.hasError()) {

            AnimeContent.clearList();

            AnimeSearchService animeSearchService = (AnimeSearchService)abstractService;

            if(animeSearchService.getResults().length() > 0) {
                noResults = false;
                AnimeContent.Anime[] result = new AnimeContent.Anime[animeSearchService.getResults().length()];

                for(int i = 0; i < animeSearchService.getResults().length(); i++) {
                    try {
                        int id = animeSearchService.getResults().getJSONObject(i).getInt("mal_id");
                        String url = animeSearchService.getResults().getJSONObject(i).getString("url");
                        String image_url = animeSearchService.getResults().getJSONObject(i).getString("image_url");
                        String title = animeSearchService.getResults().getJSONObject(i).getString("title");
                        boolean airing = animeSearchService.getResults().getJSONObject(i).getBoolean("airing");
                        String synopsis = animeSearchService.getResults().getJSONObject(i).getString("synopsis");
                        String age_rating = animeSearchService.getResults().getJSONObject(i).getString("rated");

                        AnimeContent.Anime anime = new AnimeContent.Anime(id, url, image_url, title, airing, synopsis, age_rating, 0);

                        AnimeContent.addItem(anime);
                    } catch (JSONException ex) {
                        result[i] = null;
                    }
                }

                rAdapter = new MyAnimeRecyclerViewAdapter(AnimeContent.ITEMS, mListener);
                rView.setAdapter(rAdapter);
            } else {
                Toast.makeText(getContext(), "Could not find any results.", Toast.LENGTH_SHORT).show();
                rAdapter = new MyAnimeRecyclerViewAdapter(new ArrayList<Anime>(), mListener);
                rView.setAdapter(rAdapter);
                noResults = true;
            }



        } else {
            Toast.makeText(getContext(), "An error occurred, please try again.", Toast.LENGTH_SHORT).show();
            noResults = true;
        }
    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Searching...");
            progressDialog.setIndeterminate(true);
        }

        progressDialog.show();
    }


    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
