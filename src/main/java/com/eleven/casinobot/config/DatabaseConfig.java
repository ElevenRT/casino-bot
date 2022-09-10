package com.eleven.casinobot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    private DatabaseConfig() {}

    public static void initalizeDatabase() {
        if (!AppConfig.isUSE_DDL()) {
            return;
        }

        try {
            runDDL();
        } catch (ClassNotFoundException e) {
            log.error("cannot load class: org.postgresql.Driver");
            throw new RuntimeException(e);
        }
    }

    private static void runDDL() throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        try (final Connection connection
                     = DriverManager.getConnection(
                             AppConfig.getDbUrl(), AppConfig.getDbUsername(), AppConfig.getDbPassword())
        ) {
            try (final PreparedStatement statement = connection.prepareStatement(parseDDL())) {
                statement.execute();
            }
        } catch (SQLException e) {
            log.error(e.getSQLState());
            log.error(String.valueOf(e.getErrorCode()));
            throw new RuntimeException(e);
        }
    }

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
}
