package edu.uci.ics.fabflixmobile.ui.movie;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.model.Movie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MovieListViewAdapter extends ArrayAdapter<Movie> {
    private final ArrayList<Movie> movies;

    // View lookup cache
    private static class ViewHolder {
        TextView title;
        TextView year;
        TextView director;
        TextView genresView;
        TextView starsView;
    }

    public MovieListViewAdapter(Context context, ArrayList<Movie> movies) {
        super(context, R.layout.movielist_row, movies);
        this.movies = movies;
        int a = 1;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the movie item for this position
        Movie movie = movies.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.movielist_row, parent, false);
            viewHolder.title = convertView.findViewById(R.id.title);
            viewHolder.director = convertView.findViewById(R.id.director);
            viewHolder.genresView = convertView.findViewById(R.id.genres);
            viewHolder.starsView = convertView.findViewById(R.id.stars);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        String year = movie.getYear() == null ? "N/A" : movie.getYear().toString();
        viewHolder.title.setText(movie.getTitle() + " (" + year + ") ");
        viewHolder.director.setText("Director: " + movie.getDirector());
        viewHolder.genresView.setText("Genres: " + convertListToString(movie.getGenres(), 3));
        viewHolder.starsView.setText("Stars: " + convertListToString(movie.getStars(), 3));
        return convertView;
    }

    private String convertListToString(List<String> list, int maxItems) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < list.size() && i < maxItems; i++) {
            stringBuilder.append(list.get(i));
            if (i < maxItems - 1 && i < list.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }
}