package com.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.models.DatabaseColumnMetadata;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.db.DatabaseManager.execDbQuery;
import static com.db.DatabaseManager.getJNDIDatabaseConnection;

@WebServlet("/EmployeeServlet")
public class EmployeeServlet extends AbstractServletBase{
    private static final String SELECT_METADATA_QUERY = """
            SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = 'moviedb'
            ORDER BY TABLE_NAME, COLUMN_NAME;
            """;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try (Connection conn = getJNDIDatabaseConnection()) {

            Map<String, List<DatabaseColumnMetadata>> finalResult = execDbQuery(
                    conn,
                    SELECT_METADATA_QUERY,
                    rs -> {
                        Map<String, List<DatabaseColumnMetadata>> tableCols = new HashMap<>();
                        while (rs.next()) {
                            String tableName = rs.getString("TABLE_NAME");
                            String columnName = rs.getString("COLUMN_NAME");
                            String dataType = rs.getString("DATA_TYPE");

                            DatabaseColumnMetadata colDetails = new DatabaseColumnMetadata(columnName, dataType);
                            tableCols.computeIfAbsent(tableName, k -> new ArrayList<>()).add(colDetails);
                        }
                        return tableCols;
                    }
            );

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResult = objectMapper.writeValueAsString(finalResult);
            JSONObject jsonObject = new JSONObject(jsonResult);
            super.sendJsonDataResponse(response, HttpServletResponse.SC_OK, jsonObject);

        } catch (Exception e) {
            super.exceptionHandler.handleException(response, e);
        }
    }
}
