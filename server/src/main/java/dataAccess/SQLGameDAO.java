package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.ListGamesResult;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLGameDAO implements GameDAO{

    public SQLGameDAO() throws DataAccessException {
        configureGameDatabase();
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement("TRUNCATE games")) {
                preparedStatement.executeUpdate();
            }
        }
        catch (Exception e){
            throw new DataAccessException("Error: bad request");
        }
    }

    @Override
    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException {
        Random random = new Random();
        int randomPositiveInt = random.nextInt(Integer.MAX_VALUE - 1) + 1;
        String game = new Gson().toJson(new ChessGame());
        //msql code here
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement("INSERT INTO games (gameID, gameName, game) " +
                    "VALUES(?, ?, ?)", RETURN_GENERATED_KEYS)) {
                preparedStatement.setInt(1, randomPositiveInt);
                preparedStatement.setString(2, request.gameName());
                preparedStatement.setString(3, game);
                preparedStatement.executeUpdate();
            }
        }
        catch (Exception e){
            throw new DataAccessException("Error: bad request");
        }
        return new CreateGameResult(randomPositiveInt);
    }

    @Override
    public ListGamesResult listGame() throws DataAccessException {
        List<GameData> list = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement("SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games")) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        var gameID = rs.getInt("gameID");
                        var whiteUsername = rs.getString("whiteUsername");
                        var blackUsername = rs.getString("blackUsername");
                        var gameName = rs.getString("gameName");

                        // Read and deserialize the friend JSON.
                        var json = rs.getString("game");
                        var game = new Gson().fromJson(json, ChessGame.class);

                        list.add(new GameData(gameID, whiteUsername, blackUsername, gameName, game));
                    }
                }
            }
        }
        catch (Exception e){
            throw new DataAccessException("Error: bad request");
        }
        return new ListGamesResult(list);
    }

    @Override
    public void joinGame(JoinGameRequest joinGameRequest, String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement("SELECT gameName FROM games WHERE gameID=?")) {
                preparedStatement.setInt(1, joinGameRequest.gameID());
                try (var rs = preparedStatement.executeQuery()) {
                    if(!rs.next()) {
                        throw new DataAccessException("Error: bad request");
                    }
                }
            }
        }
        catch (Exception e){
            throw new DataAccessException("Error: bad request");
        }

        if(!(joinGameRequest.playerColor() == null)){
            try (var conn = DatabaseManager.getConnection()){
                String whiteUsername = null;
                String blackUsername = null;
                try (var preparedStatement = conn.prepareStatement("SELECT whiteUsername, blackUsername FROM games WHERE gameID=?")) {
                    preparedStatement.setInt(1, joinGameRequest.gameID());
                    try (var rs = preparedStatement.executeQuery()) {
                        rs.next();
                        whiteUsername = rs.getString("whiteUsername");
                        blackUsername = rs.getString("blackUsername");
                    }
                }
                if( (!(blackUsername == null) && joinGameRequest.playerColor().equals("BLACK")) ||
                        (!(whiteUsername == null) && joinGameRequest.playerColor().equals("WHITE")) ){
                    throw new DataAccessException("Error: already taken");
                }
                if(joinGameRequest.playerColor().equals("BLACK")){
                    try (var preparedStatement = conn.prepareStatement("UPDATE games SET blackUsername=? WHERE gameID=?")) {
                        preparedStatement.setString(1, username);
                        preparedStatement.setInt(2, joinGameRequest.gameID());
                        preparedStatement.executeUpdate();
                    }
                }
                else if (joinGameRequest.playerColor().equals("WHITE")) {
                    try (var preparedStatement = conn.prepareStatement("UPDATE games SET whiteUsername=? WHERE gameID=?")) {
                        preparedStatement.setString(1, username);
                        preparedStatement.setInt(2, joinGameRequest.gameID());
                        preparedStatement.executeUpdate();
                    }
                }
            }
            catch (DataAccessException e){
                throw e;
            }
            catch (Exception e){
                throw new DataAccessException("Error: bad request");
            }
        }
    }

    @Override
    public void updateGame(ChessGame updatedGame, int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement("UPDATE games SET game=? WHERE gameID=?")) {
                var gameStringObj = new Gson().toJson(updatedGame);
                preparedStatement.setString(1, gameStringObj);
                preparedStatement.setInt(2, gameID);
                preparedStatement.executeUpdate();
            }
        }
        catch (Exception e){
            throw new DataAccessException("Error: bad request");
        }
    }

    @Override
    public void updateUser(String username, int gameID, boolean isWhite) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            if(isWhite){
                try (var preparedStatement = conn.prepareStatement("UPDATE games SET whiteUsername=? WHERE gameID=?")) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setInt(2, gameID);
                    preparedStatement.executeUpdate();
                }
            }
            else{
                try (var preparedStatement = conn.prepareStatement("UPDATE games SET blackUsername=? WHERE gameID=?")) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setInt(2, gameID);
                    preparedStatement.executeUpdate();
                }
            }
        }
        catch (Exception e){
            throw new DataAccessException("Error: bad request");
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData game = null;
        try (var conn = DatabaseManager.getConnection())
        {
            try (var preparedStatement = conn.prepareStatement("SELECT gameID, whiteUsername, blackUsername, gameName, game FROM Games WHERE gameID = ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY))
            {
                preparedStatement.setInt(1, gameID);
                var result = preparedStatement.executeQuery();
                int count = 0;
                if (result.last())
                {
                    count = result.getRow();
                }
                if (count == 1)
                {
                    game = new GameData(result.getInt(1), result.getString(2), result.getString(3), result.getString(4), new Gson().fromJson(result.getString(5), ChessGame.class));
                }
                else if (count == 0)
                {
                    throw new DataAccessException("Error: There is no game in DB");
                }
                else if (count > 1)
                {
                    throw new DataAccessException("Error: There are more than one game data.");
                }
            }
        }
        catch (SQLException E)
        {
            throw new DataAccessException(E.getMessage());
        }
        return game;
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  games (
              `gameID` int NOT NULL,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256) NOT NULL,
              `game` longtext NOT NULL,
              PRIMARY KEY (`gameID`)
            ) 
            """
    };

    @Override
    public void configureGameDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure game database: %s", ex.getMessage()));
        }
    }

}
