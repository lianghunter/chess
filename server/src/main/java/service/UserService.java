package service;

import dataAccess.*;
import model.AuthData;
import model.UserData;
import request.*;
import result.CreateGameResult;
import result.ListGamesResult;
import result.LoginResult;
import result.RegisterResult;
import server.Server;

import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

public class UserService {
    //change to sql daos
    private final AuthDAO authDAO;
    {
        try {
            authDAO = new SQLAuthDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    private final GameDAO gameDAO ;
    {
        try {
            gameDAO = new SQLGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    private final UserDAO userDAO;
    {
        try {
            userDAO = new SQLUserDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

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
            return new RegisterResult(request.username(), authToken);
        } catch (DataAccessException e) {
            throw e;
        }
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        String authToken;
        try {
            userDAO.validateUserPassword(loginRequest.username(), loginRequest.password());
            authToken = UUID.randomUUID().toString();
            authDAO.createAuth(authToken, loginRequest.username());
            return new LoginResult(loginRequest.username(), authToken);
        } catch (DataAccessException e) {
            throw e;
        }
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

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws DataAccessException{
        try{
            authDAO.authExists(authToken);
        }
        catch (DataAccessException e){
            throw e;
        }
        try{
            return gameDAO.createGame(request);
        }
        catch (DataAccessException e){
            throw e;
        }
    }

    public void joinGame(JoinGameRequest joinGameRequest, String authToken) throws DataAccessException{
        String username;
        try{
            authDAO.authExists(authToken);
            username = authDAO.getUserFromAuth(authToken);
        }
        catch (DataAccessException e){
            throw e;
        }
        try{
            gameDAO.joinGame(joinGameRequest, username);
        }
        catch (DataAccessException e){
            throw e;
        }
    }
}
