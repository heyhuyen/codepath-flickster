package com.huyentran.flickster.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

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

    long id;
    String originalTitle;
    String overview;
    String posterPath;
    String backdropPath;
    String releaseDate;
    long rating;
    long popularity;
    Type type;

    public Movie(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getLong("id");
        this.posterPath = jsonObject.getString("poster_path");
        this.originalTitle = jsonObject.getString("original_title");
        this.overview = jsonObject.getString("overview");
        this.backdropPath = jsonObject.getString("backdrop_path");
        this.releaseDate = jsonObject.getString("release_date");

        this.rating = jsonObject.getLong("vote_average");
        this.type = typeFromRating(this.rating);
        this.popularity = jsonObject.getLong("popularity");
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

    public static Type typeFromRating(long rating) {
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
