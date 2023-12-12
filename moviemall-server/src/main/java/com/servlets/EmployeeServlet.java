package com.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.models.DbColumnMetadata;
import com.utils.LogUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static com.db.DatabaseManager.execDbQuery;
import static com.db.DatabaseManager.getJNDIDatabaseConnection;

@WebServlet("/EmployeeServlet")
public class EmployeeServlet extends AbstractServletBase {

    private final Logger performanceLogger = LogUtil.getLogger();

    private static final String SELECT_METADATA_QUERY = """
            SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = 'moviedb'
            ORDER BY TABLE_NAME, COLUMN_NAME;
            """;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try (Connection conn = getJNDIDatabaseConnection(true)) {

            String requestUri = request.getRequestURI();
            String httpMethod = request.getMethod();

            long startJdbcTime = System.nanoTime();
            Map<String, List<DbColumnMetadata>> finalResult = execDbQuery(
                    conn,
                    SELECT_METADATA_QUERY,
                    rs -> {
                        Map<String, List<DbColumnMetadata>> tableCols = new HashMap<>();
                        while (rs.next()) {
                            String tableName = rs.getString("TABLE_NAME");
                            String columnName = rs.getString("COLUMN_NAME");
                            String dataType = rs.getString("DATA_TYPE");

                            DbColumnMetadata colDetails = new DbColumnMetadata(columnName, dataType);
                            tableCols.computeIfAbsent(tableName, k -> new ArrayList<>()).add(colDetails);
                        }
                        return tableCols;
                    }
            );
            long endJdbcTime = System.nanoTime();
            long jdbcTime = endJdbcTime - startJdbcTime;
            performanceLogger.info(httpMethod + " " + requestUri + " - JDBC Time: " + jdbcTime + "ns");

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResult = objectMapper.writeValueAsString(finalResult);
            JSONObject jsonObject = new JSONObject(jsonResult);
            super.sendJsonDataResponse(response, HttpServletResponse.SC_OK, jsonObject);

        } catch (Exception e) {
            super.exceptionHandler.handleException(response, e);
        }
    }
}
