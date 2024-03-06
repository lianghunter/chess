package dataAccess;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import request.RegisterRequest;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLUserDAO implements UserDAO{

    public SQLUserDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement("DROP DATABASE users")) {
                preparedStatement.executeUpdate();
            }
        }
        catch (Exception e){
            throw new DataAccessException("Error: bad request");
        }
    }

    @Override
    public void createUser(RegisterRequest register) throws DataAccessException {
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
            throw new DataAccessException("Error: bad request");
        }
    }

    @Override
    public void validateUserPassword(String username, String password) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement("SELECT password FROM users WHERE username=?")) {
                preparedStatement.setString(1, username);
                try(var rs = preparedStatement.executeQuery()){
                    rs.next();
                    var hash = rs.getString("password");

                    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

                    if(!encoder.matches(password, hash)) {
                        throw new DataAccessException("Error: bad request");
                    }}
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
