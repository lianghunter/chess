package dataAccess;

import chess.ChessGame;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import request.RegisterRequest;

import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLUserDAO implements UserDAO{

    public SQLUserDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement("TRUNCATE users")) {
                preparedStatement.executeUpdate();
            }
        }
        catch (Exception e){
            throw new DataAccessException("Error: bad request");
        }
    }

    @Override
    public void createUser(RegisterRequest register) throws DataAccessException {
        String username = register.username();
        String password = register.password();
        if(username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()){
            throw new DataAccessException("Error: bad request");
        }
        try (var conn = DatabaseManager.getConnection()){
            //add
            try (var preparedStatement = conn.prepareStatement("INSERT INTO users (username, email, password) " +
                    "VALUES(?, ?, ?)", RETURN_GENERATED_KEYS)) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                String hash = encoder.encode(register.password());

                preparedStatement.setString(1, register.username());
                preparedStatement.setString(2, register.email());
                preparedStatement.setString(3, hash);

                preparedStatement.executeUpdate();
            }
        }
        catch (Exception e){
            String duplicateMessage = "Duplicate entry '" + register.username() + "' for key 'users.PRIMARY'";
            if(e.getMessage().equals(duplicateMessage)){
                throw new DataAccessException("Error: already taken");
            }
            throw new DataAccessException("Error: bad request");
        }
    }

    @Override
    public void validateUserPassword(String username, String password) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement("SELECT password FROM users WHERE username=?")) {
                preparedStatement.setString(1, username);
                try(var rs = preparedStatement.executeQuery()){
                    if(!rs.next()){
                        throw new DataAccessException("Error: unauthorized");
                    }
                    var hash = rs.getString("password");
                    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                    String testHash = encoder.encode(password);
                    Boolean match = encoder.matches(password, hash);
                    if(!encoder.matches(password, hash)) {
                        throw new DataAccessException("Error: unauthorized");
                    }
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

    @Override
    public void removeUser(ChessGame.TeamColor color, int gameID) throws DataAccessException {
        boolean colorIsWhite;
        if(color.equals(ChessGame.TeamColor.WHITE)){
            colorIsWhite = true;
        }
        else if(color.equals(ChessGame.TeamColor.BLACK)){
            colorIsWhite = false;
        }
        else{
            throw new DataAccessException("Error: bad request");
        }
        try (var conn = DatabaseManager.getConnection()){
            if(colorIsWhite){
                try (var preparedStatement = conn.prepareStatement("UPDATE games SET whiteUsername=? WHERE gameID=?")) {
                    preparedStatement.setString(1, null);
                    preparedStatement.setInt(2, gameID);
                    preparedStatement.executeUpdate();
                }
            }
            else {
                try (var preparedStatement = conn.prepareStatement("UPDATE games SET blackUsername=? WHERE gameID=?")) {
                    preparedStatement.setString(1, null);
                    preparedStatement.setInt(2, gameID);
                    preparedStatement.executeUpdate();
                }
            }
        }
        catch (Exception e){
            throw new DataAccessException("Error: bad request");
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              `username` varchar(256) NOT NULL,
              `email` varchar(256),
              `password` varchar(256) NOT NULL,
              PRIMARY KEY (`username`)
            ) 
            """
    };

    @Override
    public void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
