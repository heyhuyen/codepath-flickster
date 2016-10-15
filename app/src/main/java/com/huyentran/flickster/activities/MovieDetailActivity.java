package com.huyentran.flickster.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
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
import static com.huyentran.flickster.utils.MovieDataUtils.youtubeTrailerSourceFromResults;

/**
 * An activity for viewing a single movie in more detail.
 */
public class MovieDetailActivity extends YouTubeBaseActivity {
    Movie movie;
    AsyncHttpClient client;
    ArrayList<Video> videos;

    YouTubePlayerView ytTrailer;
    ImageView ivBackdrop;
    ImageView ivPoster;
    TextView tvTitle;
    RatingBar rbRating;
    TextView tvPopularity;
    TextView tvOverview;
    TextView tvReleaseDate;
    TextView tvRuntime;

    YouTubePlayer player;
    boolean trailerLoaded;
    private MyPlayerStateChangeListener playerStateChangeListener;
    private MyPlaybackEventListener playbackEventListener;
    private MyFullScreenListener fullScreenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        findViews();
        movie = (Movie) getIntent().getSerializableExtra("movie");
        trailerLoaded = false;

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
        setupYoutubeListeners();
        fetchVideoData(); // TODO: add video data to Movie model
    }

    private void findViews() {
        ytTrailer = (YouTubePlayerView) findViewById(R.id.ytTrailer);
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

    private void setupYoutubeListeners() {
        playerStateChangeListener = new MyPlayerStateChangeListener();
        playbackEventListener = new MyPlaybackEventListener();
        fullScreenListener = new MyFullScreenListener();
    }

    private void fetchVideoData() {
        client.get(String.format(VIDEOS_TRAILER_URL, movie.getId()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray youtubeResults = null;
                try {
                    youtubeResults = response.getJSONArray("youtube");
                    videos = Video.fromJSONArray(youtubeResults);
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

    private void loadTrailer() {
        // check if video data has been fetched
        if (videos == null) {
            showMessage("LOAD: no video data, fetching now, try again later");
            fetchVideoData();
        }
        final String source = youtubeTrailerSourceFromResults(videos);
        if (source == null) {
            showMessage("LOAD: no trailer found from videos"); // TODO: noTrailer flag?
            return;
        }
        ytTrailer.initialize(YOUTUBE_API_KEY,
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        YouTubePlayer youTubePlayer,
                                                        boolean wasRestored) {

                        player = youTubePlayer;
//                        player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
                        player.setOnFullscreenListener(fullScreenListener);
                        player.setPlayerStateChangeListener(playerStateChangeListener);
                        player.setPlaybackEventListener(playbackEventListener);

                        if (!wasRestored) { // TODO what does this mean?
                            player.cueVideo(source);
                        }
                        trailerLoaded = true;
                        showMessage("init YouTube player success!");
                    }
                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {
                        showMessage("Could not initialize YouTube player");
                    }
                });
    }

    public void onPlayTrailer(View view) {
        // check if video data has been fetched
        if (videos == null) {
            showMessage("PLAY: no video data, fetching now, try again later");
            fetchVideoData();
        }
        else if (videos.isEmpty()) {
            // there are no videos
            showMessage("PLAY: no trailer :(");
        }
        else {
            // check if the youtube player is ready and loaded
            if (!trailerLoaded) {
                showMessage("PLAY: no trailer, fetching now");
                loadTrailer(); // auto play from onLoaded method below
            }
            else {
                showMessage("PLAY: hooray!");
                playTrailer();
            }
        }
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void playTrailer() {
        ytTrailer.setVisibility(View.VISIBLE);
        player.play();
    }

    private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {
        @Override
        public void onPlaying() {
            // Called when playback starts, either due to user action or call to play().
//            showMessage("Playing");
        }

        @Override
        public void onPaused() {
            // Called when playback is paused, either due to user action or call to pause().
//            showMessage("Paused");
        }

        @Override
        public void onStopped() {
            // Called when playback stops for a reason other than being paused.
            showMessage("Stopped");
        }

        @Override
        public void onBuffering(boolean b) {
            // Called when buffering starts or ends.
//            showMessage("buffering start/end");
        }

        @Override
        public void onSeekTo(int i) {
            // Called when a jump in playback position occurs, either
            // due to user scrubbing or call to seekRelativeMillis() or seekToMillis()
        }
    }

    private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

        @Override
        public void onLoading() {
            // Called when the player is loading a video
            // At this point, it's not ready to accept commands affecting playback such as play() or pause()
        }

        @Override
        public void onLoaded(String s) {
            // Called when a video is done loading.
            // Playback methods such as play(), pause() or seekToMillis(int) may be called after this callback.
            showMessage("loaded!");
            // calling player.play from here causes video to enter full screen mode by default.
//            playTrailer();
        }

        @Override
        public void onAdStarted() {
            // Called when playback of an advertisement starts.
        }

        @Override
        public void onVideoStarted() {
            // Called when playback of the video starts.
        }

        @Override
        public void onVideoEnded() {
            // Called when the video reaches its end.
            showMessage("video ended!");
            ytTrailer.setVisibility(View.GONE);
            // TODO: restore non full screen mode
            // restore original orientation
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
            // Called when an error occurs.
            showMessage("error! " + errorReason);
        }
    }

    private final class MyFullScreenListener implements YouTubePlayer.OnFullscreenListener {
        @Override
        public void onFullscreen(boolean b) {
            if (!b) {
                showMessage("LEAVE full screen");
                player.pause();
            }
            else {
                showMessage("ENTER full screen");
            }
        }
    }
}
