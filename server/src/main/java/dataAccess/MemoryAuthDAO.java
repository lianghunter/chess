package dataAccess;

import model.AuthData;

import java.util.HashMap;
import java.util.HashSet;

public class MemoryAuthDAO implements AuthDAO{
    private final HashSet<AuthData> authSet = new HashSet<AuthData>();
    //private static final of data structure of type authdata hashset/map
    @Override
    public void clear() throws DataAccessException {
        authSet.clear();
    }

    @Override
    public void createAuth(String token, String username) throws DataAccessException {
        AuthData a = new AuthData(token, username);
        authSet.add(a);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if(authToken == null){
            throw new DataAccessException("Error: unauthorized");
        }
        boolean authFound = false;
        for(AuthData auth : authSet){
            if(auth.authToken().equals(authToken)){
                authFound = true;
                authSet.remove(auth);
                break;
            }
        }
        if(!authFound){
            throw new DataAccessException("Error: unauthorized");
        }
    }

    @Override
    public void authExists(String authToken) throws DataAccessException {
        boolean authFound = false;
        if(authToken == null || authToken.isEmpty()){
            throw new DataAccessException("Error: unauthorized");
        }
        for(AuthData auth : authSet){
            if(auth.authToken().equals(authToken)){
                authFound = true;
                break;
            }
        }
        if(!authFound){
            throw new DataAccessException("Error: unauthorized");
        }
    }

    @Override
    public String getUserFromAuth(String authToken) {
        for(AuthData auth : authSet){
            if(auth.authToken().equals(authToken)){
                return auth.username();
            }
        }
        return "";
    }
}
