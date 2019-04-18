package example;

import example.dao.DaoFactory;
import example.dao.UserDao;
import example.jdbc.SimpleSQLException;
import example.model.User;
import example.jdbc.JDBCSession;


public class Main {
    public static void main(String[] args) {

        System.out.println("Welcome to this simple JDBC example for educational purposes.");


        try (JDBCSession session = JDBCSession.open()) {
            UserDao userDao = DaoFactory.buildUserDao();

            System.out.println("Creating a new user");
            User user = new User("Juan", "juan@test.com");
            userDao.save(user);
            System.out.printf("New user created --> %s\n", user);

            System.out.println("Updating the new user");
            user.setName("Juanito");
            userDao.update(user);
            System.out.printf("User updated --> %s\n", user);

            System.out.println("Listing all the users");
            System.out.println(userDao.getAll());

            System.out.println("Finding the user by id");
            System.out.println(userDao.get(user.getId()));
            System.out.println("Creating a contacts");
            User contact1 = new User("Contact 1", "contact1@test.com");
            User contact2 = new User("Contact 2", "contact2@test.com");

            session.onTransaction(() -> {
                userDao.save(contact1);
                userDao.addContact(user, contact1);
                userDao.save(contact2);
                userDao.addContact(user, contact2);
            });

            System.out.println("Fetching contacts");
            System.out.printf("contacts --> %s\n", userDao.getContacts(user));

            System.out.println("Deleting the contact in cascade");
            userDao.delete(contact1);
            System.out.printf("contacts --> %s\n", userDao.getContacts(user));

            System.out.println("Deleting the contact directly");
            userDao.deleteContact(user, contact2);
            System.out.printf("contacts --> %s\n", userDao.getContacts(user));


            System.out.println("Deleting the user on a transaction");
            session.onTransaction(() -> userDao.delete(user));
            System.out.println(userDao.get(user.getId()));


        } catch (SimpleSQLException e) {
            e.printSQLException();
        }
    }
}
