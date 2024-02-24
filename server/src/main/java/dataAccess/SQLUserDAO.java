package dataAccess;

import model.UserData;

public class SQLUserDAO implements  UserDAO{
    @Override
    public void clear() throws DataAccessException {

    }

    @Override
    public void createUser(String username, String email, String password) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }
}
