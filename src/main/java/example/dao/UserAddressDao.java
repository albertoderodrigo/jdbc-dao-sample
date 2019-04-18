package example.dao;

import example.model.User;
import example.model.UserAddress;

import java.util.List;

/**
 * Definition of the User Address model Dao
 */
public interface UserAddressDao extends Dao<UserAddress, Integer> {

    List<UserAddress> getForUser(User user);
}
