package example.dao;

import java.util.List;
import java.util.Optional;

/**
 * Simple definition of all methods that must have a Dao
 *
 * @param <T>  Type of the object that manages the dao
 * @param <ID> Type of the Id that manages the dao
 */
public interface Dao<T, ID> {

    Optional<T> get(ID id);

    List<T> getAll();

    void save(T t);

    void update(T t);

    void delete(T t);
}