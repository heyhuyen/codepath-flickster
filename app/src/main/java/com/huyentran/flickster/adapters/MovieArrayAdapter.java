package com.huyentran.flickster.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huyentran.flickster.R;
import com.huyentran.flickster.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by huyentran on 10/11/16.
 */
public class MovieArrayAdapter extends ArrayAdapter<Movie> {

    public MovieArrayAdapter(Context context, List<Movie> movies) {
        super(context, android.R.layout.simple_list_item_1, movies);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get the data item for position
        Movie movie = getItem(position);

        // check if the existing view is being reused
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_movie, parent, false);
        }

        // get views
        ImageView ivPoster = (ImageView) convertView.findViewById(R.id.ivPoster);
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        TextView tvOverview = (TextView) convertView.findViewById(R.id.tvOverview);

        // populate data
        ivPoster.setImageResource(0);
        tvTitle.setText(movie.getOriginalTitle());
        tvOverview.setText(movie.getOverview());
        Picasso.with(getContext()).load(movie.getPosterPath()).into(ivPoster);

        // return the view
        return convertView;
    }
}
