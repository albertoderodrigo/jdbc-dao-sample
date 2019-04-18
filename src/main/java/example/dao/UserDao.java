package example.dao;

import example.model.User;

import java.util.Set;

/**
 * Definition of the User model Dao
 */
public interface UserDao extends Dao<User, Integer> {

    /**
     * Gets all the contacts from a user
     *
     * @param user to find the contacts
     * @return A set of contacts
     */
    Set<User> getContacts(User user);

    /**
     * Gets and add to the user all the contacts from the same user
     *
     * @param user to find the contacts and add the
     */
    void populateContacts(User user);

    /**
     * Adds a contact for a user
     *
     * @param user    user to add a contact
     * @param contact contact to add to the user
     */
    void addContact(User user, User contact);

    /**
     * Deletes a contact for a user
     *
     * @param user    user to delete a contact
     * @param contact contact to delete to the user
     */
    void deleteContact(User user, User contact);
}
