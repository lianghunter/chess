package dataAccess;

import model.AuthData;
import model.GameData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MemoryGameDAO implements GameDAO{
    private static HashMap<Integer, GameData> gameSet = new HashMap<Integer, GameData>();
    @Override
    public void clear() throws DataAccessException {
        gameSet.clear();
    }

    @Override
    public void createGame() throws DataAccessException {

    }

    @Override
    public GameData getGame() throws DataAccessException {
        return null;
    }

    @Override
    public List<GameData> listGame() throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame() throws DataAccessException {

    }
}
