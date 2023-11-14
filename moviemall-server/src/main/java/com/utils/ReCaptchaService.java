package com.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ReCaptchaService {

    private static final String SITE_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    public static boolean verifyRecaptcha(String gRecaptchaResponse) throws Exception {
        if (gRecaptchaResponse == null || gRecaptchaResponse.length() == 0) {
            return false;
        }

        InputStream input = ReCaptchaService.class.getClassLoader().getResourceAsStream("config/config.properties");
        Properties prop = new Properties();
        prop.load(input);
        String SECRET_KEY = prop.getProperty("recaptcha.secret.key");
        URL verifyUrl = new URL(SITE_VERIFY_URL);
        HttpURLConnection conn = (HttpURLConnection) verifyUrl.openConnection();

        // Add request header
        conn.setRequestMethod("POST");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        // Data will be sent to the server.
        String postParams = "secret=" + SECRET_KEY + "&response=" + gRecaptchaResponse;

        // Send post request
        conn.setDoOutput(true);
        try (OutputStream outStream = conn.getOutputStream()) {
            outStream.write(postParams.getBytes(StandardCharsets.UTF_8));
            outStream.flush();
        }

        // Get the InputStream from Connection to read data sent from the server.
        try (InputStream inputStream = conn.getInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             JsonReader jsonReader = new JsonReader(inputStreamReader)) {

            JsonObject jsonObject = new Gson().fromJson(jsonReader, JsonObject.class);
            return jsonObject.get("success").getAsBoolean();

        } catch (IOException e) {
            throw new Exception("recaptcha verification failed", e);
        }
    }
}
