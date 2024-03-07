package dataAccess;

import model.AuthData;

public interface AuthDAO {
    public void clear() throws DataAccessException;
    public void createAuth(String token, String username) throws DataAccessException;
    //Retrieve an authorization given an authToken.
    //what to return? what to input? where is authToken created?
    public void deleteAuth(String authToken) throws DataAccessException;
    public void authExists(String authToken) throws DataAccessException;
    public String getUserFromAuth(String authToken) throws DataAccessException;
    public void configureDatabase() throws DataAccessException;
}
