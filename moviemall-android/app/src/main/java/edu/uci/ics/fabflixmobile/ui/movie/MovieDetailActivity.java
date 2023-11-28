package edu.uci.ics.fabflixmobile.ui.movie;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.databinding.ActivityMovieDetailBinding;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMovieDetailBinding binding = ActivityMovieDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String movieDetailJson = getIntent().getStringExtra("movie_detail");
        displayMovieDetails(movieDetailJson);
    }

    private void displayMovieDetails(String movieDetailJson) {
        try {
            JSONObject jsonObject = new JSONObject(movieDetailJson);
            String title = jsonObject.getString("title");
            int year = jsonObject.getInt("year");
            String director = jsonObject.getString("director");

            JSONArray genresArray = jsonObject.getJSONArray("genres");
            String genres = convertArrayToString(genresArray);

            JSONArray starsArray = jsonObject.getJSONArray("star_names");
            String stars = convertArrayToString(starsArray);

            TextView titleTextView = findViewById(R.id.movie_title);
           // TextView yearTextView = findViewById(R.id.movie_year);
            TextView directorTextView = findViewById(R.id.movie_director);
            TextView genresTextView = findViewById(R.id.movie_genres);
            TextView starsTextView = findViewById(R.id.movie_stars);

            titleTextView.setText(title + " (" + year + ") ");
            //yearTextView.setText(String.valueOf(year));
            directorTextView.setText("Director: " + director);
            genresTextView.setText("Genres: " + genres);
            starsTextView.setText("Stars: " + stars);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String convertArrayToString(JSONArray jsonArray) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                stringBuilder.append(jsonArray.getString(i));
                if (i < jsonArray.length() - 1) {
                    stringBuilder.append(", ");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
