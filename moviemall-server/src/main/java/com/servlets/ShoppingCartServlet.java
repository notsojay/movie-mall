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
            Map<String, CartItem> cart = getShoppingCart(session);
            JSONArray jsonArray = convertShoppingCartToJson(cart);
            super.sendJsonDataResponse(response, HttpServletResponse.SC_OK, jsonArray);

        } catch (Exception e) {
            super.exceptionHandler.handleException(response, e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            CartItem cartItem = objectMapper.readValue(request.getReader(), CartItem.class);
            HttpSession session = request.getSession();
            Map<String, CartItem> cart = getShoppingCart(session);
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
                    jsonResponse = CustomerAdapter.convertAuthResponseToJson("error", "Invalid quantity");
                    super.sendJsonDataResponse(response, HttpServletResponse.SC_BAD_REQUEST, jsonResponse);
                    return;
                }
            }

            JSONArray jsonArray = convertShoppingCartToJson(cart);
            super.sendJsonDataResponse(response, HttpServletResponse.SC_OK, jsonArray);
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
            Map<String, CartItem> cart = getShoppingCart(session);
            JSONObject jsonResponse = null;
            int status = HttpServletResponse.SC_OK;

            if (cart.containsKey(movieId)) {
                cart.remove(movieId);
                session.setAttribute("cart", cart);
                jsonResponse = CustomerAdapter.convertAuthResponseToJson("success", "Movie removed from cart successfully");

            } else {
                jsonResponse = CustomerAdapter.convertAuthResponseToJson("error", "Movie not found in cart");
                status = HttpServletResponse.SC_BAD_REQUEST;
            }

            super.sendJsonDataResponse(response, status, jsonResponse);

        } catch (Exception e) {
            super.exceptionHandler.handleException(response, e);
        }
    }

    public static Map<String, CartItem> getShoppingCart(HttpSession session) {
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
