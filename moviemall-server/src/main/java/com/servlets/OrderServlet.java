package com.servlets;

import com.adapter.CustomerAdapter;
import com.models.CartItem;
import com.models.CustomerEntity;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
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
import java.util.stream.Collectors;

import static com.servlets.ShoppingCartServlet.getCart;
import static com.utils.DatabaseManager.*;
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

            String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            JSONObject json = new JSONObject(requestBody);
            String firstName = json.getString("firstName");
            String lastName = json.getString("lastName");
            String cardNumber = json.getString("cardNumber");
            String expiryDate = json.getString("expiryDate");

            logger.info("\nfirstName: " + firstName
            + "\nlastName: " + lastName + "\ncardNumber: " + cardNumber + "\n " + expiryDate + "\n");

            Integer customerId = queryFrom_moviedb(
                    conn,
                    SQL_QUERY,
                    rs -> {
                        if (!rs.next()) return null;
                        Integer count = getSafeColumnValue(rs, "count", ResultSet::getInt);
                        if (count == null || count == 0) return null;
                        return getSafeColumnValue(rs, "customer_id", ResultSet::getInt);
                    },
                    firstName,
                    lastName,
                    cardNumber,
                    expiryDate
            );

            if (customerId == null) {
                JSONObject jsonResponse = CustomerAdapter.convertStatusResponseToJson("error", "Invalid card information");
                super.sendJsonDataResponse(response, jsonResponse);
                return;
            }

            LocalDate saleDate = LocalDate.now();
            Map<String, CartItem> cart = getCart(request.getSession());

            for (Map.Entry<String, CartItem> entry : cart.entrySet()) {
                String movieId = entry.getKey();
                CartItem cartItem = entry.getValue();
                int quantity = cartItem.getQuantity();
                insertSale(conn, customerId, movieId, saleDate, quantity);
            }

            cart.clear();
            request.getSession().setAttribute("cart", cart);
            JSONObject jsonResponse = CustomerAdapter.convertStatusResponseToJson("success", "Order has been placed");
            super.sendJsonDataResponse(response, jsonResponse);

        } catch (Exception e) {
            super.exceptionHandler.handleException(response, e);
        }
    }

    public void insertSale(Connection conn, int customerId, String movieId, LocalDate saleDate, int quantity) throws SQLException {
        movieId = decodeFromBase64(movieId);
        logger.info("\ncustomerId: " +  customerId + "\nmovieId: " + movieId + "\nsaleDate: " + saleDate + "\n quantity: " + quantity + '\n');
        final String SQL_INSERT = "INSERT INTO sales (customerId, moviesId, saleDate, quantity) VALUES (?, ?, ?, ?)";
        updateIn_moviedb(conn, SQL_INSERT,
                updateCount -> {if (updateCount != 1) throw new SQLException("Insert sale failed");},
                customerId, movieId, Date.valueOf(saleDate), quantity
        );
    }
}
