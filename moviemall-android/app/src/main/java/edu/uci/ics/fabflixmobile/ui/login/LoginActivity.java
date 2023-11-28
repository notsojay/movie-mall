package edu.uci.ics.fabflixmobile.ui.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.databinding.ActivityLoginBinding;
import edu.uci.ics.fabflixmobile.ui.movie.SearchActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private TextView message;
    private TextView errorMessage;

    //private final String LOGIN_BASE_URL  = "https://movie-mall.com:8443/server/AuthenticationServlet";
    private final String LOGIN_BASE_URL = "https://10.0.2.2:8443/server/AuthenticationServlet";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); // upon creation, inflate and initialize the layout

        username = binding.username;
        password = binding.password;
        message = binding.message;
        errorMessage = binding.errorMessage;

        final Button loginButton = binding.login;
        loginButton.setOnClickListener(view -> login());
    }

    @SuppressLint("SetTextI18n")
    public void login() {
        message.setText("Trying to login");
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        String emailInput = username.getText().toString();
        String passwordInput = password.getText().toString();
        JSONObject loginParams = new JSONObject();

        try {
            loginParams.put("email", emailInput);
            loginParams.put("password", passwordInput);
            loginParams.put("userType", "customer");
            loginParams.put("useRECAPTCHA", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest loginRequest = new JsonObjectRequest(
                Request.Method.POST,
                LOGIN_BASE_URL ,
                loginParams,
                response -> {
                    Log.d("login.success", response.toString());
                    try {
                        boolean isSuccess = response.getString("status").equals("success");
                        String serverMessage = response.getString("message");
                        if (isSuccess) {
                            Intent MoviesSearchPage = new Intent(LoginActivity.this, SearchActivity.class);
                            startActivity(MoviesSearchPage);
                            finish();
                        } else {
                            errorMessage.setText(serverMessage);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        message.setText("JSON parsing error");
                    }
                },
                error -> {
                    Log.d("login.error", error.toString());
                }
        );

        // important: queue.add is where the login request is actually sent
        queue.add(loginRequest);
    }
}