package com.huyentran.flickster.activities;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.huyentran.flickster.R;
import com.huyentran.flickster.adapters.MovieArrayAdapter;
import com.huyentran.flickster.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * The main app activity for movies browsing.
 */
public class MovieActivity extends AppCompatActivity {

    private static final String NOW_PLAYING_URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";

    ArrayList<Movie> movies;
    MovieArrayAdapter movieAdapter;
    ListView lvMovies;
    private SwipeRefreshLayout swipeContainer;
    private AsyncHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        lvMovies = (ListView) findViewById(R.id.lvMovies);
        movies = new ArrayList<>();
        movieAdapter = new MovieArrayAdapter(this, movies);
        lvMovies.setAdapter(movieAdapter);
        client = new AsyncHttpClient();
        fetchNowPlayingAsync();

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchNowPlayingAsync();
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        setupListViewListeners();
    }

    public void fetchNowPlayingAsync() {
        client.get(NOW_PLAYING_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray movieJsonResults = null;
                try {
                    movieAdapter.clear();
                    movieJsonResults = response.getJSONArray("results");
                    movieAdapter.addAll(Movie.fromJSONArray(movieJsonResults));
                } catch (JSONException e) {
                    Log.d("DEBUG", "Fetch now playing error: " + e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    public void launchMovieDetailView(Movie movie) {
        Intent i = new Intent(this, MovieDetailActivity.class);
        i.putExtra("movie", movie);
        startActivity(i);
    }

    /**
     * Adds listeners to the list view: item click and long item clicks for editing and removing.
     */
    private void setupListViewListeners() {
        lvMovies.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
                        launchMovieDetailView(movies.get(pos));
                    }
                }
        );
    }

}
