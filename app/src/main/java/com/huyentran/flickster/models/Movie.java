package com.huyentran.flickster.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import static com.huyentran.flickster.utils.Constants.BACKDROP_KEY;
import static com.huyentran.flickster.utils.Constants.ID_KEY;
import static com.huyentran.flickster.utils.Constants.OVERVIEW_KEY;
import static com.huyentran.flickster.utils.Constants.POPULARITY_KEY;
import static com.huyentran.flickster.utils.Constants.POSTER_KEY;
import static com.huyentran.flickster.utils.Constants.RATING_KEY;
import static com.huyentran.flickster.utils.Constants.RELEASE_DATE_KEY;
import static com.huyentran.flickster.utils.Constants.TITLE_KEY;

/**
 * Movie model.
 */
public class Movie implements Serializable {
    private static final long serialVersionUID = 1177222050535318633L;
    private static final String POSTER_PATH_FORMAT = "https://image.tmdb.org/t/p/w342/%s";
    private static final String BACKDROP_PATH_FORMAT = "https://image.tmdb.org/t/p/w780/%s";
    private static final long POPULAR_MIN_RATING = 5L;

    public enum Type {
        POPULAR, REGULAR
    }

    private long id;
    private String originalTitle;
    private String overview;
    private String posterPath;
    private String backdropPath;
    private String releaseDate;
    private long rating;
    private long popularity;
    private Type type;

    public Movie(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getLong(ID_KEY);
        this.posterPath = jsonObject.getString(POSTER_KEY);
        this.originalTitle = jsonObject.getString(TITLE_KEY);
        this.overview = jsonObject.getString(OVERVIEW_KEY);
        this.backdropPath = jsonObject.getString(BACKDROP_KEY);
        this.releaseDate = jsonObject.getString(RELEASE_DATE_KEY);

        this.rating = jsonObject.getLong(RATING_KEY);
        this.type = typeFromRating(this.rating);
        this.popularity = jsonObject.getLong(POPULARITY_KEY);
    }

    public static ArrayList<Movie> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Movie> results = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                results.add(new Movie(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    private Type typeFromRating(long rating) {
        if (rating > POPULAR_MIN_RATING) {
            return Type.POPULAR;
        }
        return Type.REGULAR;
    }

    public long getId() {
        return id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return String.format(POSTER_PATH_FORMAT, posterPath);
    }

    public String getBackdropPath() {
        return String.format(BACKDROP_PATH_FORMAT, backdropPath);
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public long getRating() {
        return rating;
    }

    public long getPopularity() {
        return popularity;
    }

    public Type getType() {
        return type;
    }

}
