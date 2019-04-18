package example.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Produces ThreadLocal JDBCSession that contains an instance of the connection.
 * This help us to avoid send the connection in every function as an argument
 */
public class JDBCSession implements AutoCloseable {

    private final static ThreadLocal<JDBCSession> localSession = new ThreadLocal<>();

    private final Connection connection;
    private boolean closed = false;

    private JDBCSession() {
        connection = JDBCUtils.openConnection();
    }

    /**
     * Implementation of auto closeable
     * <p>
     * Closes all the resources opened by the session.
     */
    @Override
    public void close() {
        closed = true;
        localSession.remove();
        try {
            connection.close();
        } catch (SQLException e) {
            throw new SimpleSQLException(e);
        }
    }

    /**
     * Delegate function of JDBCUtils.onTransaction
     *
     * @param doOnTransaction Code to execute inside the transaction
     */
    public void onTransaction(Runnable doOnTransaction) {
        JDBCUtils.onTransaction(connection, (c) -> doOnTransaction.run());
    }

    /**
     * Retrieves the JDBC connection associated with the session.
     * <p>
     * Be aware that maybe closed
     *
     * @return JDBC connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Indicates if the close function was called
     */
    public boolean isClosed() {
        return closed;
    }

    // ------ Static management

    /**
     * Creates a new JDBCSession
     *
     * @throws IllegalStateException if the session is already open in the current thread
     */
    public static JDBCSession open() throws IllegalStateException {

        if (localSession.get() != null) {
            throw new IllegalStateException("The session is already opened");
        }

        JDBCSession jdbcSession = new JDBCSession();
        localSession.set(jdbcSession);
        return jdbcSession;
    }


    /**
     * Retrieves the current
     *
     * @throws IllegalStateException if the session is not opened in the current thread
     */
    public static JDBCSession current() {
        JDBCSession jdbcSession = localSession.get();

        if (jdbcSession == null) {
            throw new IllegalStateException("The session is closed");
        }

        return jdbcSession;
    }
}
