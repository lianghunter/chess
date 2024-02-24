package dataAccess;

import model.AuthData;

public interface AuthDAO {
    public void clear() throws DataAccessException;
    public void createAuth() throws DataAccessException;
    //Retrieve an authorization given an authToken.
    //what to return? what to input? where is authToken created?
    public AuthData getAuth() throws DataAccessException;
    public void deleteAuth() throws DataAccessException;
}
