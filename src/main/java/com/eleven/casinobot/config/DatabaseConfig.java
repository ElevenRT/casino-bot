package com.eleven.casinobot.config;

import com.eleven.casinobot.database.pool.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Initialize the table in the database and verify that it is functioning properly.
 *
 * @author iqpizza6349
 * @version 1.0.0
 */
public final class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);
    private static final ConnectionPool CONNECTION_POOL = ConnectionPool.getInstance(1, 4);

    private DatabaseConfig() {}

    /**
     * drop and create table in database. <br>
     * You must use application.yml. ddl must be true
     */
    public static void initalizeDatabase() {
        if (!AppConfig.isUSE_DDL()) {
            return;
        }

        runDDL();
    }

    /**
     * run script schema.sql
     */
    private static void runDDL() {
        Connection connection = null;
        try {
            connection = CONNECTION_POOL.getConnection();
            try (final PreparedStatement statement = connection
                    .prepareStatement(parseDDL())) {
                statement.execute();
            }
        } catch (SQLException e) {
            logDatabaseError(log, e);
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                CONNECTION_POOL.releaseConnection(connection);
            }
            CONNECTION_POOL.closeAll();
        }
    }

    /**
     * parse ddl from schema.sql in resources. <br>
     * must do not has comments in schema.sql.
     * @return pure ddl from schema.sql
     */
    private static String parseDDL() {
        try (@SuppressWarnings("all") final BufferedReader reader
                     = new BufferedReader(new InputStreamReader(DatabaseConfig.class
                .getClassLoader().getResourceAsStream("schema.sql")))) {
            String line;
            StringBuilder sql = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                log.debug(line);
                sql.append(line);
            }
            return sql.toString();
        } catch (IOException e) {
            log.error("cannot parse ddl!");
            throw new RuntimeException(e);
        }
    }

    /**
     * log when critical error in running script
     * @param log Location of which classes occurred error
     * @param e occurred sql exception
     */
    public static void logDatabaseError(Logger log, SQLException e) {
        log.error(e.getSQLState());
        log.error(String.valueOf(e.getErrorCode()));
    }
}
