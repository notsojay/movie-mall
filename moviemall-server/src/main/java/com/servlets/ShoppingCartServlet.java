package com.servlets;

import com.adapter.CustomerAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.models.CartItem;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static com.adapter.CartItemAdapter.convertShoppingCartToJson;

@WebServlet("/ShoppingCartServlet")
public class ShoppingCartServlet extends AbstractServletBase {

    private static final Logger logger = Logger.getLogger(MovieListServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = request.getSession();
            Map<String, CartItem> cart = getCart(session);
            JSONArray jsonArray = new JSONArray();

            for (Map.Entry<String, CartItem> entry : cart.entrySet()) {
                JSONObject itemJson = new JSONObject();
                CartItem item = entry.getValue();
                itemJson = convertShoppingCartToJson(item);
                jsonArray.put(itemJson);
            }

            super.sendJsonDataResponse(response, jsonArray);

        } catch (Exception e) {
            super.exceptionHandler.handleException(response, e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            CartItem cartItem = objectMapper.readValue(request.getReader(), CartItem.class);
            HttpSession session = request.getSession();
            Map<String, CartItem> cart = getCart(session);
            CartItem existingCartItem = cart.get(cartItem.getMovieId());
            JSONObject jsonResponse;

            if (existingCartItem != null) {
                int newQuantity = existingCartItem.getQuantity() + cartItem.getQuantity();
                if (newQuantity > 0) {
                    existingCartItem.setQuantity(newQuantity);
                } else {
                    cart.remove(cartItem.getMovieId());
                }
            } else {
                if (cartItem.getQuantity() > 0) {
                    cart.put(cartItem.getMovieId(), cartItem);
                } else {
                    jsonResponse = CustomerAdapter.convertStatusResponseToJson("error", "Invalid quantity");
                    super.sendJsonDataResponse(response, jsonResponse);
                    return;
                }
            }

            JSONArray jsonArray = new JSONArray();

            for (Map.Entry<String, CartItem> entry : cart.entrySet()) {
                JSONObject itemJson = new JSONObject();
                CartItem item = entry.getValue();
                itemJson = convertShoppingCartToJson(item);
                jsonArray.put(itemJson);
            }

            super.sendJsonDataResponse(response, jsonArray);
            session.setAttribute("cart", cart);
            logger.info(jsonArray.toString());

        } catch (Exception e) {
            super.exceptionHandler.handleException(response, e);
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        try {
            String movieId = request.getParameter("movieId");
            HttpSession session = request.getSession();
            Map<String, CartItem> cart = getCart(session);
            JSONObject jsonResponse = null;

            if (cart.containsKey(movieId)) {
                cart.remove(movieId);
                session.setAttribute("cart", cart);
                jsonResponse = CustomerAdapter.convertStatusResponseToJson("success", "Movie removed from cart successfully");
            } else {
                jsonResponse = CustomerAdapter.convertStatusResponseToJson("success", "Movie not found in cart");
            }

            super.sendJsonDataResponse(response, jsonResponse);

        } catch (Exception e) {
            super.exceptionHandler.handleException(response, e);
        }
    }

    public static Map<String, CartItem> getCart(HttpSession session) {
        if (session == null) {
            throw new IllegalStateException("Session should not be null");
        }

        Object cartObj = session.getAttribute("cart");

        if (cartObj == null) {
            Map<String, CartItem> cart = new HashMap<>();
            session.setAttribute("cart", cart);
            return cart;
        }

        if (cartObj instanceof Map<?, ?> rawMap) {
            Map<String, CartItem> cart = new HashMap<>();
            for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                if (entry.getKey() instanceof String && entry.getValue() instanceof CartItem) {
                    cart.put((String) entry.getKey(), (CartItem) entry.getValue());
                } else {
                    throw new IllegalArgumentException("Invalid cart data");
                }
            }
            return cart;
        } else {
            throw new IllegalArgumentException("Invalid cart type");
        }
    }

}
