package com.huyentran.flickster.activities;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.huyentran.flickster.R;
import com.huyentran.flickster.adapters.MovieArrayAdapter;
import com.huyentran.flickster.models.Movie;
import com.huyentran.flickster.models.Video;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

import cz.msebera.android.httpclient.Header;

import static com.huyentran.flickster.utils.Constants.NOW_PLAYING_URL;
import static com.huyentran.flickster.utils.Constants.RESULTS_KEY;
import static com.huyentran.flickster.utils.Constants.TOTAL_PAGES_KEY;
import static com.huyentran.flickster.utils.Constants.VIDEOS_TRAILER_URL;
import static com.huyentran.flickster.utils.Constants.YOUTUBE_API_KEY;
import static com.huyentran.flickster.utils.MovieDataUtils.youtubeTrailerSourceFromResults;

/**
 * The main app activity for movies browsing.
 */
public class MovieActivity extends YouTubeBaseActivity
        implements MovieArrayAdapter.DataLoaderInterface {
    private static final int PAGE_ONE = 1;

    ListView lvMovies;
    ArrayList<Movie> movies;
    MovieArrayAdapter movieAdapter;

    private AsyncHttpClient client;
    private int curPage;
    private boolean loadedLastPage;
    private SwipeRefreshLayout swipeContainer;
    private HashSet<Long> movieIdCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        lvMovies = (ListView) findViewById(R.id.lvMovies);

        movies = new ArrayList<>();
        movieAdapter = new MovieArrayAdapter(this, movies);
        lvMovies.setAdapter(movieAdapter);
        client = new AsyncHttpClient();
        curPage = PAGE_ONE;
        movieIdCache = new HashSet<>();
        loadedLastPage = false;
        fetchNowPlayingAsync(true);

        setupSwipeContainer();
        setupListViewListeners();
    }

    public void loadMoreData() {
        fetchNowPlayingAsync(false);
    }

    /**
     * Asynchronous HTTP request to Movie DB API to fetch now playing movies.
     */
    public void fetchNowPlayingAsync(final boolean refresh) {
        if (loadedLastPage) {
            return;
        }
        client.get(String.format(NOW_PLAYING_URL, curPage), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray movieJsonResults;
                try {
                    movieJsonResults = response.getJSONArray(RESULTS_KEY);
                    if (refresh) {
                        // wipe data
                        movieIdCache.clear();
                        movieAdapter.clear();
                        curPage = PAGE_ONE;
                        loadedLastPage = false;
                    }
                    // avoid displaying duplicate movies
                    for (Movie movie : Movie.fromJSONArray(movieJsonResults)) {
                        if (!movieIdCache.contains(movie.getId())) {
                            movieAdapter.add(movie);
                            movieIdCache.add(movie.getId());
                        }
                    }
                    // check if this is the last page of results
                    int numPages = response.getInt(TOTAL_PAGES_KEY);
                    if (curPage >= numPages) {
                        loadedLastPage = true;
                    }
                    curPage++;
                } catch (JSONException e) {
                    Log.d("DEBUG", "Fetch now playing error: " + e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(MovieActivity.this,
                        "Failed to fetch Now Playing. Pull down to try again.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Launches the {@link MovieDetailActivity} for the given movie.
     */
    private void launchMovieDetailView(Movie movie) {
        Intent i = new Intent(this, MovieDetailActivity.class);
        i.putExtra("movie", movie);
        startActivity(i);
    }

    private void onPlayTrailer(long movieId) {
        // TODO: save
        client.get(String.format(VIDEOS_TRAILER_URL, movieId), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray youtubeResults;
                try {
                    youtubeResults = response.getJSONArray("youtube");
                    String trailerSource =
                            youtubeTrailerSourceFromResults(Video.fromJSONArray(youtubeResults));
                    if (!trailerSource.isEmpty()) {
                        Intent intent =
                                YouTubeStandalonePlayer.createVideoIntent(MovieActivity.this,
                                        YOUTUBE_API_KEY, trailerSource, 0, true, false);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    Log.d("DEBUG", "Parse video data error: " + e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d("DEBUG", "Fetch trailer error");
            }
        });
    }

    /**
     * Setup swipe container with listener for refreshing content and color scheme.
     */
    private void setupSwipeContainer() {
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchNowPlayingAsync(true);
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void setupListViewListeners() {
        lvMovies.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
                        Movie movie = movies.get(pos);
                        if (movie.getType() == Movie.Type.POPULAR) {
                            onPlayTrailer(movie.getId());
                        } else {
                            launchMovieDetailView(movies.get(pos));
                        }
                    }
                }
        );
    }

}
