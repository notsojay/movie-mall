package com.servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.models.CartItem;
import com.models.UserEntity;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;
import java.util.logging.Logger;

import static com.adapter.CustomerAdapter.convertAuthResponseToJson;
import static com.servlets.ShoppingCartServlet.getShoppingCart;
import static com.db.DatabaseManager.*;
import static com.utils.ReCaptchaService.verifyRecaptcha;
import static com.utils.URLUtils.decodeFromBase64;

@WebServlet("/OrderServlet")
public class OrderServlet extends AbstractServletBase {

    private static final Logger logger = Logger.getLogger(MovieListServlet.class.getName());

    private static final String SQL_QUERY = """
            SELECT
                c.id AS customer_id,
                COUNT(*) AS count
            FROM creditcards cc
            INNER JOIN customers c ON c.creditCardId = cc.id
            WHERE cc.firstName = ?
              AND cc.lastName = ?
              AND cc.id = ?
              AND cc.expiration = ?
            GROUP BY c.id;
            """;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try (Connection conn = getJNDIDatabaseConnection()) {

            ObjectMapper objectMapper = new ObjectMapper();
            UserEntity customer = objectMapper.readValue(request.getReader(), UserEntity.class);

            if (!verifyRecaptcha(customer.getCaptchaValue())) {
                JSONObject jsonResponse = convertAuthResponseToJson("error", "Recaptcha verification failed");
                super.sendJsonDataResponse(response, HttpServletResponse.SC_BAD_REQUEST, jsonResponse);
                return;
            }

            logCustomerInfo(customer);

            Integer customerId = verifyCreditCard(conn, customer);
            if (customerId == null) {
                JSONObject jsonResponse = convertAuthResponseToJson("error", "Invalid card information");
                super.sendJsonDataResponse(response, HttpServletResponse.SC_BAD_REQUEST, jsonResponse);
                return;
            }

            processSales(conn, customerId, getShoppingCart(request.getSession()));
            clearShoppingCart(request.getSession());
            JSONObject jsonResponse = convertAuthResponseToJson("success", "Order has been placed");
            super.sendJsonDataResponse(response, HttpServletResponse.SC_OK, jsonResponse);

        } catch (Exception e) {
            super.exceptionHandler.handleException(response, e);
        }
    }

    private void logCustomerInfo(UserEntity customer) {
        logger.info("\nfirstName: " + customer.getFirstName()
                + "\nlastName: " + customer.getLastName()
                + "\ncardNumber: " + customer.getCardNumber()
                + "\n" + customer.getCardExpiryDate() + "\n");
    }

    private Integer verifyCreditCard(Connection conn, UserEntity customer) throws SQLException, JsonProcessingException {
        return execDbQuery(
                conn,
                SQL_QUERY,
                rs -> {
                    if (!rs.next()) return null;
                    Integer count = getSafeColumnValue(rs, "count", ResultSet::getInt);
                    if (count == null || count == 0) return null;
                    return getSafeColumnValue(rs, "customer_id", ResultSet::getInt);
                },
                customer.getFirstName(),
                customer.getLastName(),
                customer.getCardNumber(),
                customer.getCardExpiryDate()
        );
    }

    private void processSales(Connection conn, Integer customerId, Map<String, CartItem> cart) throws SQLException {
        LocalDate saleDate = LocalDate.now();
        for (Map.Entry<String, CartItem> entry : cart.entrySet()) {
            String movieId = entry.getKey();
            CartItem cartItem = entry.getValue();
            int quantity = cartItem.getQuantity();
            insertSaleEntry(conn, customerId, movieId, saleDate, quantity);
        }
    }

    private void clearShoppingCart(HttpSession session) {
        Map<String, CartItem> cart = getShoppingCart(session);
        cart.clear();
        session.setAttribute("cart", cart);
    }

    private void insertSaleEntry(Connection conn, int customerId, String movieId, LocalDate saleDate, int quantity) throws SQLException {
        movieId = decodeFromBase64(movieId);
        logger.info("\ncustomerId: " +  customerId + "\nmovieId: " + movieId + "\nsaleDate: " + saleDate + "\n quantity: " + quantity + '\n');
        final String SQL_INSERT = "INSERT INTO sales (customerId, moviesId, saleDate, quantity) VALUES (?, ?, ?, ?)";
        execDbUpdate(conn, SQL_INSERT,
                updateCount -> {if (updateCount != 1) throw new SQLException("Insert sale failed");},
                customerId, movieId, Date.valueOf(saleDate), quantity
        );
    }
}
