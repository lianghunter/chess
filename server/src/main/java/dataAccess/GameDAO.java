package dataAccess;

import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.ListGamesResult;

public interface GameDAO {
    public void clear() throws DataAccessException;
    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException;
    public GameData getGame() throws DataAccessException;
    public ListGamesResult listGame() throws DataAccessException;
    public void updateGame() throws DataAccessException;
    public boolean gameExists(int gameID, String gameName) throws DataAccessException;
    public void joinGame(JoinGameRequest joinGameRequest, String username) throws DataAccessException;
}
