package dataAccess;

import chess.ChessGame;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.ListGamesResult;

public interface GameDAO {
    public void clear() throws DataAccessException;
    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException;
    public ListGamesResult listGame() throws DataAccessException;
    public void joinGame(JoinGameRequest joinGameRequest, String username) throws DataAccessException;


    GameData getGame(int gameID) throws DataAccessException;

    public void configureGameDatabase() throws DataAccessException;
    public void updateGame(ChessGame updatedGame, int gameID) throws DataAccessException;
}
