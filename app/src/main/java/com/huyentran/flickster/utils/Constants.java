package com.huyentran.flickster.utils;

/**
 * Shared constants.
 */
public class Constants {
    public static final String RESULTS_KEY = "results";
    public static final String TOTAL_PAGES_KEY = "total_pages";

    public static final String ID_KEY = "id";
    public static final String POSTER_KEY = "poster_path";
    public static final String TITLE_KEY = "original_title";
    public static final String OVERVIEW_KEY = "overview";
    public static final String BACKDROP_KEY = "backdrop_path";
    public static final String RELEASE_DATE_KEY = "release_date";
    public static final String RATING_KEY = "vote_average";
    public static final String POPULARITY_KEY = "popularity";

    public static final int BACKDROP_WIDTH = 600;
    public static final int ROUNDED_CORNER_RADIUS = 10;
    public static final int ROUNDED_CORNER_MARGIN = 10;

    public static final String NOW_PLAYING_URL = "https://api.themoviedb.org/3/movie/now_playing?page=%d&api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    public static final String MOVIE_DETAILS_URL = "https://api.themoviedb.org/3/movie/%s?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    public static final String VIDEOS_TRAILER_URL = "https://api.themoviedb.org/3/movie/%s/trailers?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";

    public static final String YOUTUBE_API_KEY = "AIzaSyCmAXBg9S3EJjEZPfXZ57mJYNxJw8fDX2E";
}
