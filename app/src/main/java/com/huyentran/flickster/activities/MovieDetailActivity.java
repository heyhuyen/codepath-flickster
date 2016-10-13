package com.huyentran.flickster.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.huyentran.flickster.R;
import com.huyentran.flickster.models.Movie;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

import static com.huyentran.flickster.utils.Constants.POSTER_WIDTH;
import static com.huyentran.flickster.utils.Constants.ROUNDED_CORNER_MARGIN;
import static com.huyentran.flickster.utils.Constants.ROUNDED_CORNER_RADIUS;

/**
 * An activity for viewing a single movie in more detail.
 */
public class MovieDetailActivity extends AppCompatActivity {
    ImageView ivPoster;
    TextView tvTitle;
    RatingBar rbRating;
    TextView tvPopularity;
    TextView tvOverview;
    TextView tvReleaseDate;
    TextView tvRuntime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ivPoster = (ImageView) findViewById(R.id.ivPoster);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        rbRating = (RatingBar) findViewById(R.id.rbRating);
        tvPopularity = (TextView) findViewById(R.id.tvPopularity);
        tvOverview = (TextView) findViewById(R.id.tvOverview);
        tvReleaseDate = (TextView) findViewById(R.id.tvReleaseDate);
        tvRuntime = (TextView) findViewById(R.id.tvRuntime);

        Movie movie = (Movie) getIntent().getSerializableExtra("movie");
        tvTitle.setText(movie.getOriginalTitle());

        Picasso.with(this).load(movie.getPosterPath())
                .placeholder(R.drawable.poster_placeholder)
                .error(R.drawable.error)
                .resize(POSTER_WIDTH, 0)
                .transform(new RoundedCornersTransformation(ROUNDED_CORNER_RADIUS,
                        ROUNDED_CORNER_MARGIN))
                .into(ivPoster);

        tvPopularity.setText(String.valueOf(movie.getPopularity()));
        tvOverview.setText(movie.getOverview());
        tvReleaseDate.setText(movie.getReleaseDate());

        rbRating.setRating(movie.getRating()); // rating is out of 10 but there are only 5 stars...
        // tvRuntime
        // genres?
        // production companies?
        // similar movies?
    }
}
