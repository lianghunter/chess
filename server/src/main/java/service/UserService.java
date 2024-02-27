package service;

import dataAccess.*;
import model.AuthData;
import model.UserData;
import request.CreateGameRequest;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.CreateGameResult;
import result.ListGamesResult;
import result.LoginResult;
import result.RegisterResult;
import server.Server;

import java.util.Collection;
import java.util.UUID;

public class UserService {
//    public AuthData register(UserData user) {}
//    public AuthData login(UserData user) {}
//    public void logout(UserData user) {}
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();
    private final UserDAO userDAO = new MemoryUserDAO();
    public void clear() throws DataAccessException {
        //try catch
        try {
            authDAO.clear();
        } catch (DataAccessException e) {
            throw e;
        }
        try {
            gameDAO.clear();
        } catch (DataAccessException e) {
            throw e;
        }
        try {
            userDAO.clear();
        } catch (DataAccessException e) {
            throw e;
        }
    }

    //return type register result
    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        String authToken;
        try {
            userDAO.createUser(request);
            authToken = UUID.randomUUID().toString();
            authDAO.createAuth(authToken, request.username());
        } catch (DataAccessException e) {
            throw e;
        }
        return new RegisterResult(request.username(), authToken);
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        String authToken;
        try {
            userDAO.validateUserPassword(loginRequest.username(), loginRequest.password());
            authToken = UUID.randomUUID().toString();
            authDAO.createAuth(authToken, loginRequest.username());
        } catch (DataAccessException e) {
            throw e;
        }
        return new LoginResult(loginRequest.username(), authToken);
    }

    public void logout(String authToken) throws DataAccessException {
        try {
            authDAO.deleteAuth(authToken);
        }
        catch (DataAccessException e){
            throw e;
        }
    }

    public ListGamesResult listGames(String authToken) throws DataAccessException{
        try{
            authDAO.authExists(authToken);
        }
        catch (DataAccessException e){
            throw e;
        }
        try {
            return gameDAO.listGame();
        }
        catch (DataAccessException e){
            throw e;
        }
    }

    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException{
        try{
            return gameDAO.createGame(request);
        }
        catch (DataAccessException e){
            throw e;
        }
    }
}
