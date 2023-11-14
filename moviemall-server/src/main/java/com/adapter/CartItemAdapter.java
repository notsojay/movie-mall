package com.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import com.models.CartItem;

import java.util.Map;

public class CartItemAdapter {

    public static JSONArray convertShoppingCartToJson(Map<String, CartItem> cart) throws JsonProcessingException {
        JSONArray jsonArray = new JSONArray();

        for (Map.Entry<String, CartItem> entry : cart.entrySet()) {
            JSONObject itemJson;
            CartItem item = entry.getValue();
            itemJson = convertCartItemToJson(item);
            jsonArray.put(itemJson);
        }

        return jsonArray;
    }

    public static JSONObject convertCartItemToJson(CartItem item) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = mapper.writeValueAsString(item);
        return new JSONObject(jsonStr);
    }
}
