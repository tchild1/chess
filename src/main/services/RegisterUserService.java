package services;

import daos.AuthDAO;
import exceptions.DataAccessException;
import daos.UserDAO;
import exceptions.BadRequestException;
import exceptions.ForbiddenException;
import models.AuthToken;
import models.User;
import requests.RegisterUserRequest;
import responses.RegisterUserResponse;

import java.sql.SQLException;

/**
 * service for a request to register a new user
 */
public class RegisterUserService {

    /**
     * This method registers a new user
     *
     * @param request object with user's information to be created in Database
     * @return returns an AuthToken and Username
     */
    public RegisterUserResponse registerUser(RegisterUserRequest request) throws DataAccessException, ForbiddenException, BadRequestException, SQLException, dataAccess.DataAccessException {
        User newUser = new User(request.getUsername(), request.getPassword(), request.getEmail());
        AuthToken authToken = new AuthToken(request.getUsername());
        validateUserInformation(newUser);
        new UserDAO().CreateUser(newUser);
        new AuthDAO().AddAuthToken(authToken);
        return new RegisterUserResponse(newUser.getUsername(), authToken.getAuthToken());
    }

    /**
     * Ensures that a user entered all proper information to create their user
     *
     * @param newUser object with user's information
     * @throws BadRequestException if not all information is present
     */
    private void validateUserInformation(User newUser) throws BadRequestException {
        if (newUser.getUsername() == null ||
            newUser.getPassword() == null ||
            newUser.getEmail() == null) {
            throw new BadRequestException("Error: bad request");
        }
    }
}
