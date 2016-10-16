package com.huyentran.flickster.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.huyentran.flickster.R;
import com.huyentran.flickster.models.Movie;
import com.huyentran.flickster.models.Video;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

import static com.huyentran.flickster.utils.Constants.BACKDROP_WIDTH;
import static com.huyentran.flickster.utils.Constants.ROUNDED_CORNER_MARGIN;
import static com.huyentran.flickster.utils.Constants.ROUNDED_CORNER_RADIUS;
import static com.huyentran.flickster.utils.Constants.VIDEOS_TRAILER_URL;
import static com.huyentran.flickster.utils.Constants.YOUTUBE_API_KEY;
import static com.huyentran.flickster.utils.Constants.YOUTUBE_VIDEOS_KEY;
import static com.huyentran.flickster.utils.MovieDataUtils.youtubeTrailerSourceFromResults;

/**
 * An activity for viewing a single movie in more detail.
 */
public class MovieDetailActivity extends YouTubeBaseActivity {
    Movie movie;
    AsyncHttpClient client;

    YouTubePlayerView ytTrailer;
    ScrollView scrollView;
    ImageView ivBackdrop;
    ImageView ivPoster;
    TextView tvTitle;
    RatingBar rbRating;
    TextView tvPopularity;
    TextView tvOverview;
    TextView tvReleaseDate;
    TextView tvRuntime;

    ArrayList<Video> videos;
    private String trailerSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        findViews();
        movie = (Movie) getIntent().getSerializableExtra("movie");

        // populate view data
        loadImages();
        tvTitle.setText(movie.getOriginalTitle());
        tvPopularity.setText(String.valueOf(movie.getPopularity()));
        tvOverview.setText(movie.getOverview());
        tvReleaseDate.setText(movie.getReleaseDate());
        rbRating.setRating(movie.getRating());
        // tvRuntime
        // genres?
        // production companies?
        // similar movies?

        // fetch videos
        client = new AsyncHttpClient();
        fetchTrailerSource();
    }

    private void findViews() {
        ytTrailer = (YouTubePlayerView) findViewById(R.id.ytTrailer);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        ivBackdrop = (ImageView) findViewById(R.id.ivBackdrop);
        ivPoster = (ImageView) findViewById(R.id.ivPoster);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        rbRating = (RatingBar) findViewById(R.id.rbRating);
        tvPopularity = (TextView) findViewById(R.id.tvPopularity);
        tvOverview = (TextView) findViewById(R.id.tvOverview);
        tvReleaseDate = (TextView) findViewById(R.id.tvReleaseDate);
        tvRuntime = (TextView) findViewById(R.id.tvRuntime);
    }

    private void loadImages() {
        Picasso.with(this).load(movie.getBackdropPath())
                .placeholder(R.drawable.backdrop_placeholder)
                .error(R.drawable.error)
                .resize(BACKDROP_WIDTH, 0)
                .transform(new RoundedCornersTransformation(ROUNDED_CORNER_RADIUS,
                        ROUNDED_CORNER_MARGIN))
                .into(ivBackdrop);

        Picasso.with(this).load(movie.getPosterPath())
                .placeholder(R.drawable.poster_placeholder)
                .error(R.drawable.error)
                .fit()
                .centerInside()
                .transform(new RoundedCornersTransformation(ROUNDED_CORNER_RADIUS,
                        ROUNDED_CORNER_MARGIN))
                .into(ivPoster);
    }

    private void fetchTrailerSource() {
        client.get(String.format(VIDEOS_TRAILER_URL, movie.getId()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray youtubeResults;
                try {
                    youtubeResults = response.getJSONArray(YOUTUBE_VIDEOS_KEY);
                    videos = Video.fromJSONArray(youtubeResults);
                    trailerSource = youtubeTrailerSourceFromResults(videos);
                    if (trailerSource.isEmpty()) {
                        showMessage("movie has no trailer");
                        // disable trailer click
                        ivBackdrop.setEnabled(false);
                    } else {
                        showMessage("movie trailer found");
                        // enable trailer click
                        ivBackdrop.setEnabled(true);
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

    public void onPlayTrailer(View view) {
        Intent intent = YouTubeStandalonePlayer.createVideoIntent(this, YOUTUBE_API_KEY,
                trailerSource, 0, true, true);
        startActivity(intent);
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
