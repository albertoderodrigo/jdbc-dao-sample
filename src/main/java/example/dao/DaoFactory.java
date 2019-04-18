package example.dao;

import example.dao.impl.UserAddressDaoJdbc;
import example.dao.impl.UserDaoJdbc;

/**
 * Encapsulates the implementation of the dao and the complexity of build a Dao
 */
public interface DaoFactory {

    static UserDao buildUserDao() {
        return new UserDaoJdbc();
    }

    static UserAddressDao buildUserAddressDao() {
        return new UserAddressDaoJdbc();
    }

}
