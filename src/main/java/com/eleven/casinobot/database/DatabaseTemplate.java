package com.eleven.casinobot.database;

import com.eleven.casinobot.config.DatabaseConfig;
import com.eleven.casinobot.database.pool.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Formatter;
import java.util.function.Function;

/**
 * Runs a database query and receives the results.
 * Queries that need to be executed inherit the class and then act through overrides.
 * A database template that primarily manages basic CRUDs.
 * Use database pool class to management resources in databases more efficiently.
 * Util methods are also defined to make the results and queries easier to use.
 * @param <T> Type to return
 * @param <K> Unique type of type to return
 *
 * @see ConnectionPool
 * @author iqpizza6349
 * @version 1.0.0
 */
public abstract class DatabaseTemplate<T, K> implements Cloneable {
    protected static final Logger log = LoggerFactory.getLogger(DatabaseTemplate.class);
    private static final ConnectionPool CONNECTION_POOL = ConnectionPool.getInstance(5, 100);

    /**
     * select type with unique type
     * @param id unique type such as Integer
     * @return select type's primary key is same as {@link K}
     */
    public final T selectById(K id) {
        Connection connection = null;
        try {
            connection = CONNECTION_POOL.getConnection();
            try (final PreparedStatement statement = connection
                    .prepareStatement(selectByIdQuery(id))) {
                try (final ResultSet resultSet = statement.executeQuery()) {
                    return result(resultSet);
                }
            }
        } catch (SQLException e) {
            DatabaseConfig.logDatabaseError(log, e);
            throw new RuntimeException(e);
        } finally {
            CONNECTION_POOL.releaseConnection(connection);
        }
    }

    /**
     * save(=insert) entity(=type) to real database table. <br>
     * for example:
     * <pre>
     *     User user = new User(1, "username");
     *     UserDAO userDAO = new UserDAO();
     *     userDAO.save(user);
     * </pre>
     * @param entity return type(=T) of instance
     */
    public final void save(T entity) {
        Connection connection = null;
        try {
            connection = CONNECTION_POOL.getConnection();
            try (final PreparedStatement statement = connection
                    .prepareStatement(saveQuery(entity))) {
                statement.execute();
            }
        } catch (SQLException e) {
            DatabaseConfig.logDatabaseError(log, e);
            throw new RuntimeException(e);
        } finally {
            CONNECTION_POOL.releaseConnection(connection);
        }
    }

    /**
     * format the data for DAO class
     * @param s the specified format string
     * @param args arguments
     * @return Returns a formatted string using the specified format string and arguments.
     */
    public String format(String s, Object... args) {
        return new Formatter().format(s, args).toString();
    }

    // check the value is null
    public <V> boolean isNull(V data) {
        return (data == null);
    }

    // get null if data is null, if data is not null, return using function
    public <V, U> V getDataOrNull(U data, Function<U, V> function) {
        return (isNull(data)) ? null : function.apply(data);
    }

    public <V> String getDataOrDefault(V data) {
        return getDataOrDefault(data, false);
    }

    // get default when the data is null, if data is not null return data
    public <V> String getDataOrDefault(V data, boolean comma) {
        return (isNull(data))
                ? (comma) ? ", DEFAULT" : "DEFAULT"
                : data.toString();
    }

    /**
     * save query that use in method {@link DatabaseTemplate#save(T)}
     * @param entity target to save
     * @return full raw query from DAO
     */
    protected abstract String saveQuery(T entity);

    /**
     * select query that use in method {@link DatabaseTemplate#selectById(K)}
     * @param id unique type({@link K}) of returning type({@link T})'s primary key
     * @return raw query String from DAO
     */
    protected abstract String selectByIdQuery(K id);

    /**
     * get mapper(same as {@link T}) from {@link ResultSet}
     * @param resultSet raw data from executed sql
     * @return mapper (same as {@link T})
     * @throws SQLException caused when {@link ResultSet#next()} method has invoked
     */
    protected abstract T result(ResultSet resultSet) throws SQLException;

    @Override
    @SuppressWarnings("rawtypes")
    public DatabaseTemplate clone() throws CloneNotSupportedException {
        return (DatabaseTemplate) super.clone();
    }
}
