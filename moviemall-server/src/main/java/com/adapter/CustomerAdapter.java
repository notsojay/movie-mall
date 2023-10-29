package com.adapter;

import org.json.JSONObject;

import java.sql.ResultSet;

import static com.utils.DatabaseManager.getSafeColumnValue;

public class CustomerAdapter {
    public static String extractPasswordFromDbResultSet(ResultSet rs) {
        return getSafeColumnValue(rs, "password", ResultSet::getString);
    }

    public static JSONObject convertLoginResponseToJson(String status, String message) {
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("status", status);
        jsonResponse.put("message", message);
        return jsonResponse;
    }

    public static JSONObject convertLogoutResponseToJson(String status, String message) {
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("status", status);
        jsonResponse.put("message", message);
        return jsonResponse;
    }
}
