package example.jdbc;

import java.sql.SQLException;

/**
 * Function that expects a SQLException
 *
 * @param <T> Type of the argument of the function
 * @param <R> Type of result of the function
 */
@FunctionalInterface
public interface SqlThrowableFunction<T, R> {

    R apply(T t) throws SQLException;

}
