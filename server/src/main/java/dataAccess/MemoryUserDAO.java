package dataAccess;

import chess.ChessGame;
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
        if(username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()){
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
        if(userSet.isEmpty()){
            throw new DataAccessException("Error: unauthorized");
        }
        boolean userFound = false;
        for(UserData user: userSet){
            if(user.username().equals(username)){
                userFound = true;
            }
            if(user.username().equals(username) && !user.password().equals(password)){
                throw new DataAccessException("Error: unauthorized");
            }
        }
        if(!userFound){
            throw new DataAccessException("Error: unauthorized");
        }
    }

    @Override
    public void removeUser(ChessGame.TeamColor color, int gameID) throws DataAccessException {

    }

    @Override
    public void configureDatabase() throws DataAccessException {

    }
}
