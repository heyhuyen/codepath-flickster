package com.huyentran.flickster.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huyentran.flickster.R;
import com.huyentran.flickster.activities.MovieActivity;
import com.huyentran.flickster.models.Movie;
import com.huyentran.flickster.utils.Constants;
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

    public interface DataLoaderInterface {
        void loadMoreData();
    }

    private static class ViewHolder {
        ImageView poster;
        TextView title;
        TextView overview;
    }

    private static class PopularViewHolder {
        ImageView popular;
    }

    private DataLoaderInterface dataLoader;

    public MovieArrayAdapter(Context context, List<Movie> movies) {
        super(context, android.R.layout.simple_list_item_1, movies);
        this.dataLoader = (MovieActivity) context;
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
        // check if position is last item
        if (position == getCount() - 1) {
            dataLoader.loadMoreData();
        }

        // get the data item for position
        Movie movie = getItem(position);

        // get data item type for this position
        int viewType = this.getItemViewType(position);

        if (viewType == Movie.Type.REGULAR.ordinal()) {
            ViewHolder viewHolder;
            if (convertView == null) {
                // If there's no view to re-use, inflate a brand new view for row
                viewHolder = new ViewHolder();

                // Inflate XML layout based on the type
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_movie,
                        parent, false);
                viewHolder.poster = (ImageView) convertView.findViewById(ivPoster);
                viewHolder.title = (TextView) convertView.findViewById(tvTitle);
                viewHolder.overview = (TextView) convertView.findViewById(tvOverview);
                // Cache the viewHolder object inside the fresh view
                convertView.setTag(viewHolder);
            } else {
                // View is being recycled, retrieve the viewHolder object from tag
                viewHolder = (ViewHolder) convertView.getTag();
            }

            // populate data
            viewHolder.title.setText(movie.getOriginalTitle());
            viewHolder.overview.setText(movie.getOverview());
            viewHolder.poster.setImageResource(0);
            // check portrait or landscape
            if (getContext().getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE) {
                loadImage(movie.getBackdropPath(), R.drawable.backdrop_placeholder,
                        viewHolder.poster);
            }
            else {
                loadImage(movie.getPosterPath(), R.drawable.poster_placeholder,
                        viewHolder.poster);
            }

            return convertView;
        }
        else if (viewType == Movie.Type.POPULAR.ordinal()) {
            PopularViewHolder popularViewHolder;
            if (convertView == null) {
                popularViewHolder = new PopularViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_popular,
                        parent, false);
                popularViewHolder.popular =
                        (ImageView) convertView.findViewById(R.id.ivPopular);
                convertView.setTag(popularViewHolder);
            } else {
                popularViewHolder = (PopularViewHolder) convertView.getTag();
            }

            // populate data
            popularViewHolder.popular.setImageResource(0);
            loadImage(movie.getBackdropPath(), R.drawable.backdrop_placeholder,
                    popularViewHolder.popular);

            return convertView;
        }
        else {
            throw new RuntimeException("Unknown row item type");
        }
    }

    private void loadImage(String imagePath, int placeholder, ImageView imageView) {
        Picasso.with(getContext()).load(imagePath)
                .fit()
                .centerInside()
                .placeholder(placeholder)
                .error(R.drawable.error)
                .transform(new RoundedCornersTransformation(Constants.ROUNDED_CORNER_RADIUS,
                        Constants.ROUNDED_CORNER_MARGIN))
                .into(imageView);
    }
}
