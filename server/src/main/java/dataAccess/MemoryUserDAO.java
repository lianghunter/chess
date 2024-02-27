package dataAccess;

import model.AuthData;
import model.UserData;
import request.RegisterRequest;
import result.RegisterResult;

import java.util.HashMap;
import java.util.HashSet;

public class MemoryUserDAO implements UserDAO{
    private final HashSet<UserData> userSet = new HashSet<UserData>();
    @Override
    public void clear() throws DataAccessException {
        userSet.clear();
    }

    @Override
    public void createUser(RegisterRequest request) throws DataAccessException {
        String username = request.username();
        String password = request.password();
        if(username.trim().isEmpty() || username == null ||
           password.trim().isEmpty() || password == null){
            throw new DataAccessException("Error: bad request");
        }
        for(UserData user : userSet){
            if(user.username().equals(username)){
                throw new DataAccessException("Error: already taken");
            }
        }

        UserData newUser = new UserData(request.username(), request.email(), request.password());
        userSet.add(newUser);
    }

    @Override
    public void validateUserPassword(String username, String password) throws DataAccessException{
        for(UserData user: userSet){
            if(user.username().equals(username) && !user.password().equals(password)){
                throw new DataAccessException("Error: unauthorized");
            }
        }
    }
}
