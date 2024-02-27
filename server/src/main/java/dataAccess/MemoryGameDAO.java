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
        if(gameExists(0, request.gameName()) || request.gameName().isEmpty()){
            throw new DataAccessException("Error: bad request");
        }
        Random random = new Random();
        int randomPositiveInt = random.nextInt(Integer.MAX_VALUE - 1) + 1;
        while(gameExists(randomPositiveInt, "")){
            randomPositiveInt = random.nextInt(Integer.MAX_VALUE - 1) + 1;
        }
        gameSet.add(new GameData(randomPositiveInt, "",
                "", request.gameName(), new ChessGame()));
        return new CreateGameResult(randomPositiveInt);
    }

    @Override
    public ListGamesResult listGame() throws DataAccessException {
        List<GameData> list = new ArrayList<>(gameSet);
        return new ListGamesResult(list);
    }



    @Override
    public void joinGame(JoinGameRequest joinGameRequest, String username) throws DataAccessException {
        if(!gameExists(joinGameRequest.gameID(), "")){
            throw new DataAccessException("Error: bad request");
        }
        GameData currentGame = null;
        for(GameData game : gameSet){
            //ensure right game
            if(game.gameID() == joinGameRequest.gameID()){
                //ensure color not taken
                if((!game.blackUsername().isEmpty() && joinGameRequest.playerColor().equals("BLACK")) ||
                        (!game.whiteUsername().isEmpty() && joinGameRequest.playerColor().equals("WHITE"))){
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


    @Override
    public boolean gameExists(int gameID, String gameName) throws DataAccessException {
        if(gameSet.isEmpty()){
            return false;
        }
        for(GameData game : gameSet){
            if(game.gameID() == gameID || game.gameName().equals(gameName)){
                return true;
            }
        }
        return false;
    }
}
