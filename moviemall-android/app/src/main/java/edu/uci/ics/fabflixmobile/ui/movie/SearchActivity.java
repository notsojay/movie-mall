package edu.uci.ics.fabflixmobile.ui.movie;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;

import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.databinding.ActivitySearchBinding;

public class SearchActivity extends AppCompatActivity {
    private EditText searchField;
    private Button searchButton;
    //private final String MOVIE_LIST_BASE_URL = "https://movie-mall.com:8443/server/MovieListServlet";
    private final String MOVIE_LIST_BASE_URL = "https://10.0.2.2:8443/server/MovieListServlet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySearchBinding binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        searchField = binding.searchField;
        searchButton = binding.searchButton;
        searchButton.setOnClickListener(view -> searchMovies());
    }

    public void searchMovies() {
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        String title = searchField.getText().toString();
        String url = MOVIE_LIST_BASE_URL + "?requestType=search-movies&title=" + title + "&recordsPerPage=10&currentPage=1";

        JsonArrayRequest searchRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        Intent movieListPage = new Intent(SearchActivity.this, MovieListActivity.class);
                        movieListPage.putExtra("movies", response.toString());
                        movieListPage.putExtra("searchTitle", title);
                        startActivity(movieListPage);
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

        queue.add(searchRequest);
    }
}