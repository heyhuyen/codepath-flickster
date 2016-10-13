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

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

import static com.huyentran.flickster.R.id.ivPoster;
import static com.huyentran.flickster.R.id.tvOverview;
import static com.huyentran.flickster.R.id.tvTitle;

/**
 * Custom Adapter for {@link Movie} items.
 */
public class MovieArrayAdapter extends ArrayAdapter<Movie> {

    // View lookup cache
    private static class ViewHolder {
        ImageView poster;
        TextView title;
        TextView overview;
        ImageView backdrop;
    }

    private static final int POSTER_WIDTH = 230;
    private static final int BACKDROP_WIDTH = 600;
    private static final int ROUNDED_CORNER_RADIUS = 10;
    private static final int ROUNDED_CORNER_MARGIN = 10;

    public MovieArrayAdapter(Context context, List<Movie> movies) {
        super(context, android.R.layout.simple_list_item_1, movies);
    }

    // Returns the number of types of Views that will be created by getView(int, View, ViewGroup)
    @Override
    public int getViewTypeCount() {
        return Movie.Type.values().length;
    }

    // Get the type of View that will be created by getView(int, View, ViewGroup)
    // for the specified item.
    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType().ordinal();
    }

    // Get a View that displays the data at the specified position in the data set.
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get the data item for position
        Movie movie = getItem(position);

        // check if the existing view is being reused; otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // Get the data item type for this position
            int type = getItemViewType(position);

            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();

            // Inflate XML layout based on the type
            convertView = getInflatedLayoutForType(type, parent);
            viewHolder.poster = (ImageView) convertView.findViewById(ivPoster);
            viewHolder.title = (TextView) convertView.findViewById(tvTitle);
            viewHolder.overview = (TextView) convertView.findViewById(tvOverview);
            viewHolder.backdrop = (ImageView) convertView.findViewById(R.id.ivBackdrop);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // populate data
        if (viewHolder.title != null) {
            viewHolder.title.setText(movie.getOriginalTitle());
        }
        if (viewHolder.overview != null) {
            viewHolder.overview.setText(movie.getOverview());
        }
        String imagePath = movie.getPosterPath();
        ImageView imageView = viewHolder.poster;
        int targetWidth = POSTER_WIDTH;
        int placeholder = R.drawable.poster_placeholder;
        if (viewHolder.poster != null) {
            viewHolder.poster.setImageResource(0);
        } else {
            viewHolder.backdrop.setImageResource(0);
            imagePath = movie.getBackdropPath();
            imageView = viewHolder.backdrop;
            targetWidth = BACKDROP_WIDTH;
            placeholder = R.drawable.backdrop_placeholder;
        }
        Picasso.with(getContext()).load(imagePath)
                .placeholder(placeholder)
                .error(R.drawable.error)
                .resize(targetWidth, 0)
                .transform(new RoundedCornersTransformation(ROUNDED_CORNER_RADIUS,
                        ROUNDED_CORNER_MARGIN))
                .into(imageView);

        // return the view
        return convertView;
    }

    // Given the item type, responsible for returning the correct inflated XML layout file
    private View getInflatedLayoutForType(int type, ViewGroup parent) {
        if (type == Movie.Type.POPULAR.ordinal()) {
            return LayoutInflater.from(getContext()).inflate(R.layout.item_popular, parent, false);
        } else if (type == Movie.Type.AVERAGE.ordinal()) {
            return LayoutInflater.from(getContext()).inflate(R.layout.item_movie, parent, false);
        } else {
            return null;
        }
    }
}
