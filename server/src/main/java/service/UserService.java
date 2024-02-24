package service;

import dataAccess.*;
import model.AuthData;
import server.Server;

import java.util.Collection;

public class UserService {
//    public AuthData register(UserData user) {}
//    public AuthData login(UserData user) {}
//    public void logout(UserData user) {}
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();
    private final UserDAO userDAO = new MemoryUserDAO();
    public void clear(){
        //try catch
        try {
            authDAO.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        try {
            gameDAO.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        try {
            userDAO.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public AuthData Register(String username, String email, String password){
        try {
            userDAO.createUser(username, email, password);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Collection<AuthData> listAuth() {
        return (Collection<AuthData>) authDAO;
    }
}
