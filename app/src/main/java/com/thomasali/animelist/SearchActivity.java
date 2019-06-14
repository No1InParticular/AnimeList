package com.thomasali.animelist;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Locale;

public class SearchActivity extends AppCompatActivity implements AnimeFragment.OnListFragmentInteractionListener {


    protected SearchActivity searchActivity;
    private Menu menu;

    static String queryText = "";
    static EditText query;
    static int page = 1;
    Button btnSearch;

    public static String status = "Not Specified";
    public static String rated = "Not Specified";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        searchActivity = this;
        setTitle("Search Anime");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        AnimeFragment animeFragment = (AnimeFragment) getSupportFragmentManager().findFragmentById(R.id.search_results);
        animeFragment.noResults = true;

        int orientation = getResources().getConfiguration().orientation;

        // Both have the buttons and numbers for pages so can do that outside the ifs below
        ImageButton btnLastPage = findViewById(R.id.btnLastPage);
        ImageButton btnNextPage = findViewById(R.id.btnNextPage);
        TextView tvPageNum = findViewById(R.id.tvPageNumber);

        tvPageNum.setText(String.format(Locale.getDefault(),"%d", page));

        btnLastPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(page > 1) {
                    page = page-1;
                    search();
                }
            }
        });

        btnNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimeFragment animeFragment = (AnimeFragment) getSupportFragmentManager().findFragmentById(R.id.search_results);
                if (animeFragment != null) {
                    if(!animeFragment.noResults) {
                        page = page+1;
                        search();
                    }
                }

            }
        });

        if(orientation == Configuration.ORIENTATION_LANDSCAPE) {


            Toolbar searchToolbar = findViewById(R.id.land_toolbar);
            setSupportActionBar(searchToolbar);

            query = findViewById(R.id.search_query);

            final Spinner spStatus = findViewById(R.id.sp_status);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.status_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spStatus.setAdapter(adapter);
            spStatus.setSelection(0);

            final Spinner spRated = findViewById(R.id.sp_rated);
            adapter = ArrayAdapter.createFromResource(this,R.array.rated_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spRated.setAdapter(adapter);
            spRated.setSelection(0);

            if(!queryText.isEmpty()) {
                query.setText(queryText);
            }

            btnSearch = findViewById(R.id.search_button);
            btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    queryText = query.getText().toString();
                    status = spStatus.getSelectedItem().toString();
                    rated = spRated.getSelectedItem().toString();
                    page = 1;
                    search();
                }
            });
        } else {
            // Portrait
            Toolbar searchToolbar = findViewById(R.id.search_toolbar);
            setSupportActionBar(searchToolbar);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        this.menu = menu;

        int orientation = getResources().getConfiguration().orientation;

        if(orientation == Configuration.ORIENTATION_PORTRAIT) {

            getMenuInflater().inflate(R.menu.search_menu, menu);

            MenuItem searchItem = menu.findItem(R.id.action_search);
            final SearchView searchView = (SearchView) searchItem.getActionView();

            if (!queryText.isEmpty()) {
                searchView.setQuery(queryText, false);
            }

            // Configure the search info and add any event listeners...
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    queryText = searchView.getQuery().toString();
                    page = 1;
                    search();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    return false;
                }
            });

            menu.findItem(R.id.action_filter).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    FilterDialog dialog = new FilterDialog(searchActivity);
                    dialog.show();
                    return false;
                }
            });
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {

        int orientation = getResources().getConfiguration().orientation;

        if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
            query = findViewById(R.id.search_query);

            if(!queryText.isEmpty()) {
                query.setText(queryText);
            }
        } else {
            // Portrait
            if(menu != null) {
                MenuItem searchItem = menu.findItem(R.id.action_search);
                final SearchView searchView = (SearchView) searchItem.getActionView();

                if(!queryText.isEmpty()) {
                    searchView.setQuery(queryText, false);
                }
            }

        }

        AnimeFragment animeFragment = (AnimeFragment) getSupportFragmentManager().findFragmentById(R.id.search_results);
        if(MainActivity.currentActivity == "Search")
            search();

        super.onResume();
    }

    @Override
    public void onListFragmentInteraction(AnimeContent.Anime item) {

    }

    public void search() {
        AnimeFragment animeFragment = (AnimeFragment) getSupportFragmentManager().findFragmentById(R.id.search_results);
        if (animeFragment != null) {
            final TextView tvPageNum = findViewById(R.id.tvPageNumber);
            tvPageNum.setText(String.format(Locale.getDefault(),"%d", page));
            animeFragment.search(queryText, status, rated, page);

        }
    }
}
