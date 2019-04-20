package example.jdbc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;
import java.util.function.Consumer;

import static java.lang.String.format;

/**
 * Utility function to manage the JDBC library without more of the pain
 */
public abstract class JDBCUtils {

    private static final String CONFIG_FILE = "jdbc.properties";
    private static final String JDBC_DRIVER = "jdbc.driver";
    private static final String JDBC_URL = "jdbc.url";
    private static final String JDBC_USER = "jdbc.user";
    private static final String JDBC_PASS = "jdbc.pass";

    private final static Properties properties;

    // In the static initialization we fetch the jdbc properties
    static {
        properties = getProperties();

        try {
            Class.forName(properties.getProperty(JDBC_DRIVER));
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Opens a new connection with the configuration from the jbc properties
     * <p>
     * Note: The caller must close the connection after use it
     *
     * @return A valid connection to the database
     */
    public static Connection openConnection() {
        try {
            return DriverManager.getConnection(
                    properties.getProperty(JDBC_URL),
                    properties.getProperty(JDBC_USER),
                    properties.getProperty(JDBC_PASS));
        } catch (SQLException e) {
            printSQLException(e);
            throw new SimpleSQLException(e);
        }
    }

    /**
     * Fetch one the value from the ResultSet and converts it with the parser function
     * <p>
     * Note: this function closes the result set after process it
     *
     * @param resultSet To get the result
     * @param parser    parser to transform the result into an object
     * @param <T>       Type of the object that is providing the parser function
     * @return A an optional value of the T
     */
    public static <T> Optional<T> getSingleValue(ResultSet resultSet, SqlThrowableFunction<ResultSet, T> parser) {
        try {
            if (resultSet.next()) {
                return Optional.of(parser.apply(resultSet));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new SimpleSQLException(e);
        } finally {
            try {
                resultSet.close();
            } catch (SQLException e) {
                printSQLException(e);
            }
        }
    }

    /**
     * Fetch all the values of the ResultSet and converts them with the parser function
     * <p>
     * Note: this function closes the result set after process it
     *
     * @param resultSet To get the results
     * @param parser    parser to transform the results into an object
     * @param <T>       Type of the object that is providing the parser function
     * @return A list of parsed results from the result set
     */
    public static <T> List<T> getAllValues(ResultSet resultSet, SqlThrowableFunction<ResultSet, T> parser) {
        List<T> results = new ArrayList<>();
        try {
            while (resultSet.next()) {
                results.add(parser.apply(resultSet));
            }
        } catch (SQLException e) {
            throw new SimpleSQLException(e);
        } finally {
            try {
                resultSet.close();
            } catch (SQLException e) {
                printSQLException(e);
            }
        }
        return results;
    }

    /**
     * Asserts that at last one element was inserted
     *
     * @param affectedRows amount of rows affected after the insert
     * @throws SQLException If no row was affected
     */
    public static void assetInsert(int affectedRows) throws SQLException {
        if (affectedRows == 0) {
            throw new SQLException("Creating user failed, no rows affected.");
        }
    }

    /**
     * Fetch the last insert id from statement and converts the ResultSet with the parser function
     * <p>
     * Note: this function closes the result set after process it
     *
     * @param statement that was use for the insert
     * @param parser    parser to transform the result into an object
     * @param <ID>      Type of the id that is providing the parser function
     * @return the ID parsed
     * @throws SQLException If no id was found
     */
    public static <ID> ID getGeneratedId(Statement statement,
                                         SqlThrowableFunction<ResultSet, ID> parser) throws SQLException {
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return parser.apply(generatedKeys);
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        }
    }

    /**
     * Executes a runnable inside a transaction avoiding the boilerplate code
     * <p>
     * If a runtime exception is thrown inside the Consumer, the transaction if going to do a rollback
     *
     * @param connection      connection to open the transaction
     * @param doOnTransaction Code to execute inside the transaction
     */
    public static void onTransaction(Connection connection, Consumer<Connection> doOnTransaction) {
        boolean autoCommit = true;

        try {
            autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            doOnTransaction.accept(connection);

            connection.commit();
        } catch (Throwable e) {
            try {
                System.err.print("Transaction is being rolled back");
                connection.rollback();
            } catch (SQLException e1) {
                throw new SimpleSQLException(e1);
            }

            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else if (e instanceof SQLException) {
                throw new SimpleSQLException((SQLException) e);
            } else {
                e.printStackTrace();
            }
        } finally {
            try {
                connection.setAutoCommit(autoCommit);
            } catch (SQLException e) {
                printSQLException(e);
            }
        }
    }

    /**
     * Prints the sql exception in a fancy way
     * <p>
     * Inspired from: https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlexception.html
     *
     * @param sqlException
     */
    public static void printSQLException(SQLException sqlException) {
        for (Throwable e : sqlException) {
            if (e instanceof SQLException) {
                if (!ignoreSQLException(((SQLException) e).getSQLState())) {

                    e.printStackTrace(System.err);
                    System.err.println("SQLState: " +
                            ((SQLException) e).getSQLState());

                    System.err.println("Error Code: " +
                            ((SQLException) e).getErrorCode());

                    System.err.println("Message: " + e.getMessage());

                    Throwable t = e.getCause();
                    while (t != null) {
                        System.out.println("Cause: " + t);
                        t = t.getCause();
                    }
                }
            }
        }
    }

    /**
     * Indicates if certan exception codes can be ignored
     * <p>
     * Inspired from: https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlexception.html
     *
     * @param sqlState state to check if ignore
     */
    public static boolean ignoreSQLException(String sqlState) {

        if (sqlState == null) {
            System.out.println("The SQL state is not defined!");
            return false;
        }

        // X0Y32: Jar file already exists in schema
        if (sqlState.equalsIgnoreCase("X0Y32"))
            return true;

        // 42Y55: Table already exists in schema
        return sqlState.equalsIgnoreCase("42Y55");

    }

    /**
     * Gets the properties containing the configurations for the jdbc connector
     */
    private static Properties getProperties() {
        Properties properties = new Properties();

        try (InputStream is = JDBCUtils.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {

            if (is == null)
                throw new FileNotFoundException(format("The config file %s was not found please create it",
                        CONFIG_FILE));

            properties.load(is);

        } catch (IOException e) {
            throw new IllegalStateException(format("Something went wrong opening the %s file", CONFIG_FILE), e);
        }

        return properties;
    }

}
