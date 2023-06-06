package com.libman.data;

// clang-format off
import com.libman.App;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
// clang-format on

public class Database
{
    private static final String URL = "jdbc:mariadb://";

    public Connection connection;

    public void initialize() throws SQLException
    {
        Map<String, String> preferences = (Map<String, String>)App.preferences.get("database");

        Properties properties = new Properties();

        properties.setProperty("user", preferences.get("username"));
        properties.setProperty("password", preferences.get("password"));
        properties.setProperty("allowPublicKeyRetrieval", "true");

        String URL = String.format("%s%s:%s/", Database.URL, preferences.get("host"), preferences.get("port"));
        this.connection = DriverManager.getConnection(URL, properties);
    }

    public void disconnect() throws SQLException
    {
        this.connection.close();
    }
}
