package org.sk.races.rest.reader;
import org.sk.races.rest.api.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {
    private static final Logger logger = Logger.getLogger(DatabaseConnection.class.getName());
    private static Connection connection = null;
    private static Properties properties = new Properties();

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Configuration config = Configuration.getInstance();
                Properties props = config.getProperties();

                String url = props.getProperty("db.url");
                String user = props.getProperty("db.user");
                String password = props.getProperty("db.password");
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(url, user, password);
                logger.info("The connection to the database has been established.");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Database connection error", e);
            }
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error when closing the connection", e);
        }
    }
}