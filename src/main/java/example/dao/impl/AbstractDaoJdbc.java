package example.dao.impl;

import example.jdbc.JDBCSession;

import java.sql.*;

/**
 * Base Dao for the JDBC implementations of the DAO's to avoid duplicated code
 */
abstract class AbstractDaoJdbc {

    Connection getConnection() {
        JDBCSession session = JDBCSession.current();
        return session.getConnection();
    }

}
