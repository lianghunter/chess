package dataAccess;

import model.AuthData;
import model.GameData;
import request.CreateGameRequest;
import result.CreateGameResult;
import result.ListGamesResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MemoryGameDAO implements GameDAO{
    private static HashSet<GameData> gameSet = new HashSet<GameData>();
    @Override
    public void clear() throws DataAccessException {
        gameSet.clear();
    }

    @Override
    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException {
        return null;
    }

    @Override
    public GameData getGame() throws DataAccessException {
        return null;
    }

    @Override
    public ListGamesResult listGame() throws DataAccessException {
        List<GameData> list = new ArrayList<>(gameSet);
        return new ListGamesResult(list);
    }

    @Override
    public void updateGame() throws DataAccessException {

    }
}
