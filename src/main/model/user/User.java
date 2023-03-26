package main.model.user;

import main.model.Model;
import main.utils.parameters.NotNull;

/**
 * A class that represents a user
 */
public interface User extends Model {
    /**
     * Gets the user ID of the user.
     *
     * @return the ID of the user.
     */
    String getID();

    /**
     * Gets the username of the user
     *
     * @return the name of the user
     */
    String getUserName();

    String getHashedPassword();

    void setHashedPassword(String hashedPassword);
    /**
     * Gets the email of the user
     *
     * @return the email of the user
     */
    String getEmail();
}
