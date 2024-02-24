package dataAccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.HashSet;

public class MemoryUserDAO implements UserDAO{
    private final HashSet<UserData> userSet = new HashSet<UserData>();
    @Override
    public void clear() throws DataAccessException {
        userSet.clear();
    }

    @Override
    public void createUser(String username, String email, String password) throws DataAccessException {
        for(UserData user : userSet){
            if(user.username().equals(username)){
                throw new DataAccessException("username already exists");
            }
        }
        UserData newUser = new UserData(username, email, password);
        userSet.add(newUser);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        //return userSet.
        return null;
    }
}
