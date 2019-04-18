package example.dao.impl;

import example.dao.UserAddressDao;
import example.jdbc.JDBCUtils;
import example.jdbc.SimpleSQLException;
import example.model.User;
import example.model.UserAddress;

import java.sql.*;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of the UserAddress Model Dao
 */
public class UserAddressDaoJdbc extends AbstractDaoJdbc implements UserAddressDao {

    private static final String SELECT_ONE_QUERY = "SELECT * FROM useraddress where idUserAddress = ?";
    private static final String SELECT_ALL_QUERY = "SELECT * FROM useraddress";
    private static final String SELECT_ALL_USER_QUERY = "SELECT * FROM useraddress where idUser = ?";
    private static final String INSERT_QUERY = "INSERT INTO useraddress(idUser, address) VALUES (?, ?)";
    private static final String UPDATE_QUERY = "UPDATE useraddress SET address = ? WHERE idUserAddress= ?";
    private static final String DELETE_QUERY = "DELETE FROM useraddress WHERE idUserAddress = ?";

    @Override
    public Optional<UserAddress> get(Integer id) {

        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(SELECT_ONE_QUERY)) {

            statement.setInt(1, id);
            return JDBCUtils.getSingleValue(statement.executeQuery(), this::buildUserFromResultSet);
        } catch (SQLException e) {
            throw new SimpleSQLException(e);
        }
    }

    @Override
    public List<UserAddress> getAll() {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL_QUERY)) {

            return JDBCUtils.getAllValues(statement.executeQuery(), this::buildUserFromResultSet);
        } catch (SQLException e) {
            throw new SimpleSQLException(e);
        }
    }

    @Override
    public List<UserAddress> getForUser(User user) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL_USER_QUERY)) {

            statement.setInt(1, user.getId());
            return JDBCUtils.getAllValues(statement.executeQuery(), this::buildUserFromResultSet);
        } catch (SQLException e) {
            throw new SimpleSQLException(e);
        }
    }

    @Override
    public void save(UserAddress userAddress) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection
                .prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, userAddress.getUser().getId());
            statement.setString(2, userAddress.getAddress());
            JDBCUtils.assetInsert(statement.executeUpdate());

            Integer id = JDBCUtils.getGeneratedId(statement, (rs) -> rs.getInt(1));

            userAddress.setId(id);
        } catch (SQLException e) {
            throw new SimpleSQLException(e);
        }
    }

    @Override
    public void update(UserAddress userAddress) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)) {
            statement.setString(1, userAddress.getAddress());
            statement.setInt(2, userAddress.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new SimpleSQLException(e);
        }
    }

    @Override
    public void delete(UserAddress user) {
        Connection connection = getConnection();
        try (PreparedStatement statement = connection.prepareStatement(DELETE_QUERY)) {
            statement.setInt(1, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new SimpleSQLException(e);
        }
    }

    /**
     * Utility function builds and populates his properties from the result set value.
     *
     * @param resultSet Result set ready to get the values
     * @return A UserAddress populated
     */
    private UserAddress buildUserFromResultSet(ResultSet resultSet) {
        UserAddress userAddress = new UserAddress();
        try {
            userAddress.setId(resultSet.getInt("idUserAddress"));
            userAddress.setAddress(resultSet.getString("address"));
        } catch (SQLException e) {
            throw new SimpleSQLException(e);
        }
        return userAddress;
    }
}
