package daos;

import exceptions.DataAccessException;
import exceptions.ForbiddenException;
import models.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This table provides access to user database tables
 */
public class UserDAO {

    public static Map<String, User> users = new HashMap<>();

    /**
     * Creates a new user in the database
     *
     * @param newUser to be created
     * @throws DataAccessException if there is an error, exception is thrown
     */
    public void CreateUser(User newUser) throws DataAccessException, ForbiddenException {
        if (users.get(newUser.getUsername()) == null) {
            users.put(newUser.getUsername(), newUser);
        } else {
            throw new ForbiddenException("Error: already taken");
        }
    }

    /**
     * Deletes all users from the database
     *
     * @throws DataAccessException if there is an error, exception is thrown
     */
    public void DeleteAllUsers() throws DataAccessException {
        users.clear();
    }

    /**
     * Gets a single user from the database by their authToken
     *
     * @param Username of user to be retrieved
     * @return user of the given Username
     * @throws DataAccessException if there is an error, exception is thrown
     */
    public Boolean AuthenticateUser(String Username, String Password) throws DataAccessException {
        User DbUser = users.get(Username);
        if (DbUser == null) {
            return false;
        }
        return Objects.equals(DbUser.getPassword(), Password);
    }
}
