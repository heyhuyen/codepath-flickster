package com.huyentran.flickster.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Video model.
 */
public class Video implements Serializable {
    private static final long serialVersionUID = 2177222050535318633L;
    private static final String TRAILER_TYPE = "Trailer";

    String name;
    String source;
    String type;

    public Video(JSONObject jsonObject) throws JSONException {
        this.name = jsonObject.getString("name");
        this.source = jsonObject.getString("source");
        this.type = jsonObject.getString("type");
    }

    public static ArrayList<Video> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Video> results = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                results.add(new Video(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    public boolean isTrailer() {
        return type.equals(TRAILER_TYPE);
    }

    public String getName() {
        return name;
    }

    public String getSource() {
        return source;
    }

    public String getType() {
        return type;
    }
}
