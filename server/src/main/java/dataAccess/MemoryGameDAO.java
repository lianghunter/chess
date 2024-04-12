package dataAccess;

import chess.ChessGame;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.ListGamesResult;

import java.util.*;

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
    public ListGamesResult listGame() throws DataAccessException {
        List<GameData> list = new ArrayList<>(gameSet);
        return new ListGamesResult(list);
    }



    @Override
    public void joinGame(JoinGameRequest joinGameRequest, String username) throws DataAccessException {
        /*if(!gameExists(joinGameRequest.gameID(), "")){
            throw new DataAccessException("Error: bad request");
        }*/
        GameData currentGame = null;
        //if color null, skip. might be done better after checking the game.
        if(!(joinGameRequest.playerColor() == null)){
            for(GameData game : gameSet){
                //ensure right game
                if(game.gameID() == joinGameRequest.gameID()){
                    //ensure color not taken
                    if( (!(game.blackUsername() == null) && joinGameRequest.playerColor().equals("BLACK")) ||
                            (!(game.whiteUsername() == null) && joinGameRequest.playerColor().equals("WHITE")) ){
                        throw new DataAccessException("Error: already taken");
                    }
                    if(joinGameRequest.playerColor().equals("BLACK")){
                        currentGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
                        gameSet.add(currentGame);
                        gameSet.remove(game);
                        break;
                    }
                    else if (joinGameRequest.playerColor().equals("WHITE")) {
                        currentGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
                        gameSet.add(currentGame);
                        gameSet.remove(game);
                        break;
                    }
                    //might add an else for an observer
                }
            }
        }
    }

    @Override
    public void updateUser(String username, int gameID, boolean isWhite) throws DataAccessException {

    }


    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public void configureGameDatabase() throws DataAccessException {

    }

    @Override
    public void updateGame(ChessGame updatedGame, int gameID) throws DataAccessException {

    }



}
