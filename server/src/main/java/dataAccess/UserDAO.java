package dataAccess;

import model.UserData;
import request.RegisterRequest;
import result.RegisterResult;

import javax.xml.crypto.Data;

public interface UserDAO {
    public void clear() throws DataAccessException;
    public void createUser(RegisterRequest u) throws DataAccessException;
    public void validateUserPassword(String username, String password) throws DataAccessException;
}
