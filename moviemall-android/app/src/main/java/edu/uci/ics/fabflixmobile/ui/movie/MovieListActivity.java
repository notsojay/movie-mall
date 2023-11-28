package edu.uci.ics.fabflixmobile.ui.movie;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.databinding.ActivityMovielistBinding;

public class MovieListActivity extends AppCompatActivity {

    private final String MOVIE_DETAIL_BASE_URL = "https://10.0.2.2:8443/server/MovieDetailServlet";
    private final String MOVIE_LIST_BASE_URL = "https://10.0.2.2:8443/server/MovieListServlet";
    private ArrayList<Movie> movies;
    private MovieListViewAdapter adapter;
    private int currentPage = 1;
    private final int pageSize = 10;
    String movieListQuery;
    int totalPages = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMovielistBinding binding = ActivityMovielistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String moviesJson = getIntent().getStringExtra("movies");
        this.movieListQuery = getIntent().getStringExtra("searchTitle");

        Log.d("MovieListActivity", "Received JSON: " + moviesJson);

        movies = parseMoviesJson(getIntent().getStringExtra("movies"));
        setupListView();
        setupPaginationButtons();
    }

    private ArrayList<Movie> parseMoviesJson(String moviesJsonData) {
        ArrayList<Movie> movies = new ArrayList<>();
        try {
            JSONArray moviesArray = new JSONArray(moviesJsonData);

            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieJson = moviesArray.getJSONObject(i);
                String title = movieJson.getString("title");
                String movieID = movieJson.getString("movie_id");
                int year = movieJson.getInt("year");
                String director = movieJson.getString("director");
                totalPages = movieJson.getInt("total_records") / 10;

                List<String> genres = new ArrayList<>();
                JSONArray genresArray = movieJson.getJSONArray("genres");
                for (int j = 0; j < genresArray.length(); j++) {
                    genres.add(genresArray.getString(j));
                }

                List<String> stars = new ArrayList<>();
                JSONArray starsArray = movieJson.getJSONArray("star_names");
                for (int k = 0; k < starsArray.length(); k++) {
                    stars.add(starsArray.getString(k));
                }

                movies.add(new Movie(title, movieID, (short) year, director, genres, stars));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movies;
    }

    private void setupListView() {
        adapter = new MovieListViewAdapter(this, movies);
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Movie movie = movies.get(position);
            String movieDetailQuery = movie.getMovieID();
            @SuppressLint("DefaultLocale") String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getTitle(), movie.getYear());
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            fetchMovieDetail(movieDetailQuery);
        });
    }

    private void setupPaginationButtons() {
        Button previousPageButton = findViewById(R.id.previousPageButton);
        Button nextPageButton = findViewById(R.id.nextPageButton);

        previousPageButton.setOnClickListener(v -> changePage(-1));
        nextPageButton.setOnClickListener(v -> changePage(1));
    }

    private void changePage(int pageDelta) {
        currentPage += pageDelta;
        if (currentPage > 0 && currentPage <= totalPages) {
            fetchMovieList(currentPage);
        } else {
            currentPage -= pageDelta;
        }
    }

    private void fetchMovieList(int page) {
        String url = MOVIE_LIST_BASE_URL + "?requestType=search-movies&title=" + movieListQuery + "&recordsPerPage=10&currentPage=" + page;

        JsonArrayRequest searchRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        ArrayList<Movie> newMovies = parseMoviesJson(response.toString());
                        updateListView(newMovies);
                    } catch (Exception e) {
                        Log.e("search.error", "JSON parsing error: " + e.getMessage());
                    }
                },
                error -> {
                    Log.d("search.error", "Volley error: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.d("search.error", "Status Code: " + error.networkResponse.statusCode);
                    }
                }
        );

        NetworkManager.sharedManager(this).queue.add(searchRequest);
    }

    private void fetchMovieDetail(String movieDetailQuery) {
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        String url = MOVIE_DETAIL_BASE_URL + "?query=" + movieDetailQuery;

        JsonObjectRequest searchRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        Intent movieDetailPage = new Intent(MovieListActivity.this, MovieDetailActivity.class);
                        movieDetailPage.putExtra("movie_detail", response.toString());
                        startActivity(movieDetailPage);
                    } catch (Exception e) {
                        Log.e("movie_detail.error", "JSON parsing error: " + e.getMessage());
                    }
                },
                error -> {
                    Log.d("movie_detail.error", "Volley error: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.d("search.error", "Status Code: " + error.networkResponse.statusCode);
                    }
                }
        );

        queue.add(searchRequest);
    }

    private void updateListView(ArrayList<Movie> newMovies) {
        movies.clear();
        movies.addAll(newMovies);
        adapter.notifyDataSetChanged();
    }
}
