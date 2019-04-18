package example.dao.impl;

import example.dao.UserDao;
import example.jdbc.SimpleSQLException;
import example.model.User;
import example.jdbc.JDBCUtils;

import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * JDBC implementation of the User Model Dao
 */
public class UserDaoJdbc extends AbstractDaoJdbc implements UserDao {

    private static final String SELECT_ONE_QUERY = "SELECT * FROM user where idUser = ?";
    private static final String SELECT_ALL_QUERY = "SELECT * FROM user";
    private static final String SELECT_CONTACTS_QUERY = "SELECT c.* " +
            "FROM user u " +
            "join usercontact uc on uc.idUser= u.idUser " +
            "join user c on uc.idContact = c.idUser " +
            "where u.idUser = ?";
    private static final String INSERT_QUERY = "INSERT INTO user(name, email) VALUES (?, ?)";
    private static final String INSERT_CONTACT_QUERY = "INSERT INTO usercontact(idUser, idContact) VALUES (?, ?)";
    private static final String UPDATE_QUERY = "UPDATE user SET name = ?, email = ? WHERE idUser= ?";
    private static final String DELETE_QUERY = "DELETE FROM user WHERE idUser = ?";
    private static final String DELETE_CONTACT_QUERY = "DELETE FROM usercontact WHERE idUser = ? and idContact = ?";

    @Override
    public Optional<User> get(Integer id) {

        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(SELECT_ONE_QUERY)) {

            statement.setInt(1, id);
            return JDBCUtils.getSingleValue(statement.executeQuery(), this::buildUserFromResultSet);
        } catch (SQLException e) {
            throw new SimpleSQLException(e);
        }
    }

    @Override
    public List<User> getAll() {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL_QUERY)) {

            return JDBCUtils.getAllValues(statement.executeQuery(), this::buildUserFromResultSet);
        } catch (SQLException e) {
            throw new SimpleSQLException(e);
        }
    }

    @Override
    public void save(User user) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection
                .prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());

            JDBCUtils.assetInsert(statement.executeUpdate());
            user.setId(JDBCUtils.getGeneratedId(statement, rs -> rs.getInt(1)));

        } catch (SQLException e) {
            throw new SimpleSQLException(e);
        }
    }

    @Override
    public void update(User user) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setInt(3, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new SimpleSQLException(e);
        }
    }

    @Override
    public void delete(User user) {
        Connection connection = getConnection();
        try (PreparedStatement statement = connection.prepareStatement(DELETE_QUERY)) {
            statement.setInt(1, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new SimpleSQLException(e);
        }
    }

    @Override
    public Set<User> getContacts(User user) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(SELECT_CONTACTS_QUERY)) {
            statement.setInt(1, user.getId());
            return new HashSet<>(JDBCUtils.getAllValues(statement.executeQuery(), this::buildUserFromResultSet));
        } catch (SQLException e) {
            throw new SimpleSQLException(e);
        }
    }

    @Override
    public void populateContacts(User user) {
        user.setContacts(getContacts(user));
    }

    @Override
    public void addContact(User user, User contact) {
        Connection connection = getConnection();
        user.getContacts().add(contact);

        try (PreparedStatement statement = connection.prepareStatement(INSERT_CONTACT_QUERY)) {

            statement.setInt(1, user.getId());
            statement.setInt(2, contact.getId());

            JDBCUtils.assetInsert(statement.executeUpdate());
        } catch (SQLException e) {
            throw new SimpleSQLException(e);
        }
    }

    @Override
    public void deleteContact(User user, User contact) {
        Connection connection = getConnection();
        user.getContacts().remove(contact);

        try (PreparedStatement statement = connection.prepareStatement(DELETE_CONTACT_QUERY)) {

            statement.setInt(1, user.getId());
            statement.setInt(2, contact.getId());

            JDBCUtils.assetInsert(statement.executeUpdate());
        } catch (SQLException e) {
            throw new SimpleSQLException(e);
        }
    }

    /**
     * Utility function builds and user and populates his properties from the result set value.
     *
     * @param resultSet Result set ready to get the values
     * @return A User populated
     */
    private User buildUserFromResultSet(ResultSet resultSet) {
        User user = new User();
        try {
            user.setId(resultSet.getInt("idUser"));
            user.setName(resultSet.getString("name"));
            user.setEmail(resultSet.getString("email"));
        } catch (SQLException e) {
            throw new SimpleSQLException(e);
        }
        return user;
    }
}
