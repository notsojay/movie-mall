package com.adapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.models.MovieEntity;
import org.json.JSONObject;

import java.util.Map;
import com.models.CartItem;

public class CartItemAdapter {
    public static JSONObject convertShoppingCartToJson(CartItem cart) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = mapper.writeValueAsString(cart);
        return new JSONObject(jsonStr);
    }
}
