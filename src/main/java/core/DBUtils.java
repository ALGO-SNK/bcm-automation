package core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DBUtils implements AutoCloseable {

    private final String dbUrl;
    private final String dbUsername;
    private final String dbPassword;
    private Connection connection;

    public DBUtils(String dbUrl, String dbUsername, String dbPassword) {
        this.dbUrl = dbUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
    }

    public Connection connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        }
        return connection;
    }

    public List<Map<String, Object>> executeQuery(String sql, Object... params) throws SQLException {
        try (PreparedStatement statement = connect().prepareStatement(sql)) {
            bindParams(statement, params);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Map<String, Object>> rows = new ArrayList<>();
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columns = metaData.getColumnCount();

                while (resultSet.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= columns; i++) {
                        row.put(metaData.getColumnLabel(i), resultSet.getObject(i));
                    }
                    rows.add(row);
                }
                return rows;
            }
        }
    }

    public int executeUpdate(String sql, Object... params) throws SQLException {
        try (PreparedStatement statement = connect().prepareStatement(sql)) {
            bindParams(statement, params);
            return statement.executeUpdate();
        }
    }

    private void bindParams(PreparedStatement statement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
