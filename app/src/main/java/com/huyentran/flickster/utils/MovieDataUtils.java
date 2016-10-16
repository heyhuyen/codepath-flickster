package com.huyentran.flickster.utils;

import com.huyentran.flickster.models.Video;

import java.util.List;

/**
 * Shared utilities for dealing with data from the movie database API.
 */
public final class MovieDataUtils {

    public static String youtubeTrailerSourceFromResults(List<Video> videos) {
        for (Video video : videos) {
            if (video.isTrailer()) {
                return video.getSource();
            }
        }
        return "";
    }
}
