package dataAccess;

import model.UserData;
import request.RegisterRequest;
import result.RegisterResult;

import javax.xml.crypto.Data;
import java.sql.SQLException;

public interface UserDAO {
    public void clear() throws DataAccessException;
    public void createUser(RegisterRequest register) throws DataAccessException;
    public void validateUserPassword(String username, String password) throws DataAccessException;
    public void configureDatabase() throws DataAccessException;
}
