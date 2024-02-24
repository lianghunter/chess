package dataAccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MemoryAuthDAO implements AuthDAO{
    private final HashMap<Integer, AuthData> authSet = new HashMap<Integer, AuthData>();
    //private static final of data structure of type authdata hashset/map
    @Override
    public void clear() throws DataAccessException {
        authSet.clear();
    }

    @Override
    public void createAuth() throws DataAccessException {

    }

    @Override
    public AuthData getAuth() throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth() throws DataAccessException {

    }
}
