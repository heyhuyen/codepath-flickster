package com.huyentran.flickster.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Movie model.
 */
public class Movie {
    private static final String POSTER_PATH_FORMAT = "https://image.tmdb.org/t/p/w342/%s";
    private static final String BACKDROP_PATH_FORMAT = "https://image.tmdb.org/t/p/w780/%s";
    private static final long POPULAR_MIN_RATING = 5L;
    public enum Type {
        POPULAR, AVERAGE
    }

    String posterPath;
    String originalTitle;
    String overview;
    String backdropPath;
    Type type;

    public Movie(JSONObject jsonObject) throws JSONException {
        this.posterPath = jsonObject.getString("poster_path");
        this.originalTitle = jsonObject.getString("original_title");
        this.overview = jsonObject.getString("overview");
        this.backdropPath = jsonObject.getString("backdrop_path");
        this.type = typeFromRating(jsonObject.getLong("vote_average"));
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

    static Type typeFromRating(long rating) {
        if (rating > POPULAR_MIN_RATING) {
            return Type.POPULAR;
        }
        return Type.AVERAGE;
    }

    public String getPosterPath() {
        return String.format(POSTER_PATH_FORMAT, posterPath);
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public String getBackdropPath() {
        return String.format(BACKDROP_PATH_FORMAT, backdropPath);
    }

    public Type getType() {
        return type;
    }

}
