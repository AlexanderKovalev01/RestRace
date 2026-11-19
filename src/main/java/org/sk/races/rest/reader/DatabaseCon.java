package org.sk.races.rest.reader;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseCon {
    private static final Logger logger = Logger.getLogger(DatabaseConnection.class.getName());
    private static DataSource dataSource;

    private static DataSource getDataSource() {
        if (dataSource == null) {
            try {
                InitialContext ctx = new InitialContext();
                dataSource = (DataSource) ctx.lookup("java:jboss/datasources/MarathonDS");
                logger.info("DataSource MarathonDS successfully looked up.");
            } catch (NamingException e) {
                logger.log(Level.SEVERE, "Failed to lookup DataSource", e);
                throw new RuntimeException(e);
            }
        }
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }
}
