package dataAccess;

import model.GameData;
import request.CreateGameRequest;
import result.CreateGameResult;
import result.ListGamesResult;

import javax.xml.crypto.Data;
import java.util.List;

public interface GameDAO {
    public void clear() throws DataAccessException;
    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException;
    public GameData getGame() throws DataAccessException;
    public ListGamesResult listGame() throws DataAccessException;
    public void updateGame() throws DataAccessException;
}
