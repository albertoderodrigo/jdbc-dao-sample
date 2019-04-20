package example.jdbc;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * Runtime exception that encapsulates a SQL exception to simplify the code
 */
public class SimpleSQLException extends RuntimeException
        implements Iterable<Throwable> {


    private final SQLException sqlException;

    public SimpleSQLException(SQLException sqlException) {
        super(sqlException);
        this.sqlException = sqlException;
    }

    public SQLException getSqlException() {
        return sqlException;
    }

    @Override
    public Iterator<Throwable> iterator() {
        return sqlException.iterator();
    }

    @Override
    public void forEach(Consumer<? super Throwable> action) {
        sqlException.forEach(action);
    }

    @Override
    public Spliterator<Throwable> spliterator() {
        return sqlException.spliterator();
    }

    public void printSQLException() {
        JDBCUtils.printSQLException(sqlException);
    }
}
