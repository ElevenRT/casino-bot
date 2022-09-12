package com.eleven.casinobot.database.pool;

import com.eleven.casinobot.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manage database pools <br>
 * You can access database tables more effectively, manage resources. <br>
 * also available to set the minimum of pool size, and the maximum pool size. <br>
 * Thread-safe implementation using the synchronization keyword
 *
 * @author iqpizza6349
 * @version 1.0.0
 */
public final class ConnectionPool {
    private static final Logger log = LoggerFactory.getLogger(ConnectionPool.class);

    // initialize driver which is using
    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            log.error("cannot load class: org.postgresql.Driver");
            throw new RuntimeException(e);
        }
    }

    private final List<Connection> free;
    private final List<Connection> used;

    private static final String URL = AppConfig.getDbUrl();
    private static final String USERNAME = AppConfig.getDbUsername();
    private static final String PASSWORD = AppConfig.getDbPassword();
    private int maxConnections;
    private int numberOfConnections = 0;

    private static ConnectionPool connectionPool;

    /**
     * get database singleton instance. <br>
     * initialize database connection pools size <br>
     * Create the maximum number of db connections. <br>
     * @param initialConnections initial count of database pool size, default is 5.
     * @param maxConnections set count of database max pool size. default is 10.
     * @return singleton instance of connectionPool
     */
    public static ConnectionPool getInstance(int initialConnections, int maxConnections) {
        try {
            if (connectionPool == null) {
                synchronized (ConnectionPool.class) {
                    connectionPool = new ConnectionPool(initialConnections, maxConnections);
                }
            }
        } catch (SQLException e) {
            log.error(e.getSQLState());
            log.error(String.valueOf(e.getErrorCode()));
        }
        return connectionPool;
    }

    /**
     * private-constructor to setting initial pool size, max pool size
     * @param initialConnections initial pool size
     * @param maxConnections max count of pool size
     * @throws SQLException when making new database connection has sql exception
     */
    private ConnectionPool(int initialConnections, int maxConnections)
            throws SQLException {
        this.maxConnections = maxConnections;

        if (initialConnections < 0) {
            initialConnections = 5;
        }

        if (maxConnections < 0) {
            this.maxConnections = 10;
        }

        free = Collections.synchronizedList(new ArrayList<>(initialConnections));
        used = Collections.synchronizedList(new ArrayList<>(initialConnections));

        while (numberOfConnections < initialConnections) {
            addConnection();
        }
    }

    // make new database connection and add into free connection pools
    private void addConnection() {
        free.add(getNewConnection());
    }

    // make new database connection
    private Connection getNewConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            log.error(e.getSQLState());
            log.error(String.valueOf(e.getErrorCode()));
        }
        numberOfConnections++;
        return connection;
    }

    /**
     * get database connection from the latest free connection pools.
     * also add new connection to free pools when free pools are empty and max count is greater than number of total connections
     * @return the latest unused connection
     * @throws SQLException when free connection has
     */
    public synchronized Connection getConnection() throws SQLException {
        if (free.isEmpty()) {
            while (numberOfConnections < maxConnections) {
                addConnection();
            }
        }
        Connection con0 = free.get(free.size() - 1);
        free.remove(con0);
        used.add(con0);
        return con0;
    }


    /**
     * release connection after finishing the database work
     * @apiNote do not invoke this method before the task is complete
     * @param connection0 used connection
     */
    public synchronized void releaseConnection(Connection connection0) {
        used.remove(connection0);
        free.add(connection0);
    }

    // close all database connection
    // please use when the class does not use any-longer
    public void closeAll() {
        closePool(used);
        closePool(free);
    }

    private void closePool(List<Connection> pool) {
        for (int i = 0; i < pool.size(); i++) {
            Connection connection0 = free.get(i);
            pool.remove(i--);
            try {
                connection0.close();
            } catch (SQLException e) {
                log.error(e.getSQLState());
                log.error(String.valueOf(e.getErrorCode()));
            }
        }
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public int getNumberOfConnections() {
        return numberOfConnections;
    }
}
