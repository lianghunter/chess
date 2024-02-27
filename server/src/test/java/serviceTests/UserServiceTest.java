package serviceTests;

import dataAccess.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.*;
import result.CreateGameResult;
import result.ListGamesResult;
import result.LoginResult;
import result.RegisterResult;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private final UserService service = new UserService();
    @BeforeEach
    void clear() throws DataAccessException {
        service.clear();
    }

    @Test
    void registerSuccess() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("bob", "bobpass", "bob@mail");
        RegisterResult result = service.register(request);
        assertEquals(result.username(), "bob");
    }

    @Test
    void registerFail() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("", "bobpass", "bob@mail");
        assertThrows(DataAccessException.class, () -> service.register(request));
    }

    @Test
    void loginSuccess() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("bob", "bobpass", "bob@mail");
        service.register(request);
        LoginRequest loginRequest = new LoginRequest("bob", "bobpass");
        LoginResult loginResult = service.login(loginRequest);
        assertEquals(loginResult.username(), "bob");
    }

    @Test
    void loginFail() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("bob", "bobpass", "bob@mail");
        service.register(request);
        LoginRequest loginRequest = new LoginRequest("bob", "joepass");
        assertThrows(DataAccessException.class, () -> service.login(loginRequest));
    }

    @Test
    void logoutSuccess() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("bob", "bobpass", "bob@mail");
        service.register(request);
        LoginRequest loginRequest = new LoginRequest("bob", "bobpass");
        LoginResult loginResult = service.login(loginRequest);
        service.logout(loginResult.authToken());
    }

    @Test
    void logoutFail() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("bob", "bobpass", "bob@mail");
        service.register(request);
        LoginRequest loginRequest = new LoginRequest("bob", "bobpass");
        LoginResult loginResult = service.login(loginRequest);
        assertThrows(DataAccessException.class, () -> service.logout("0"));
    }

    @Test
    void createGameSuccess() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("bob", "bobpass", "bob@mail");
        service.register(request);
        LoginRequest loginRequest = new LoginRequest("bob", "bobpass");
        LoginResult loginResult = service.login(loginRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("game1");
        CreateGameResult createGameResult = service.createGame(createGameRequest, loginResult.authToken());
        assertNotEquals(createGameResult.gameID(), 0);
    }

    @Test
    void createGameFail() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("bob", "bobpass", "bob@mail");
        service.register(request);
        LoginRequest loginRequest = new LoginRequest("bob", "bobpass");
        LoginResult loginResult = service.login(loginRequest);
        CreateGameRequest createGameRequest2 = new CreateGameRequest("");
        assertThrows(DataAccessException.class, () -> service.createGame(createGameRequest2, loginResult.authToken()));
        CreateGameRequest createGameRequest = new CreateGameRequest("game1");
        assertThrows(DataAccessException.class, () -> service.createGame(createGameRequest, ""));
    }

    @Test
    void listGameSuccess() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("bob", "bobpass", "bob@mail");
        service.register(request);
        LoginRequest loginRequest = new LoginRequest("bob", "bobpass");
        LoginResult loginResult = service.login(loginRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("game1");
        service.createGame(createGameRequest, loginResult.authToken());
        ListGamesResult listGamesResult = service.listGames(loginResult.authToken());
        assertEquals(listGamesResult.games().size(), 1);
        service.createGame(new CreateGameRequest("game2"), loginResult.authToken());
        listGamesResult = service.listGames(loginResult.authToken());
        assertEquals(listGamesResult.games().size(), 2);
    }

    @Test
    void listGameFail() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("bob", "bobpass", "bob@mail");
        service.register(request);
        LoginRequest loginRequest = new LoginRequest("bob", "bobpass");
        LoginResult loginResult = service.login(loginRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("game1");
        service.createGame(createGameRequest, loginResult.authToken());
        assertThrows(DataAccessException.class, () -> service.listGames("not a real token"));
    }

    @Test
    void joinGameSuccess() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("bob", "bobpass", "bob@mail");
        service.register(request);
        LoginRequest loginRequest = new LoginRequest("bob", "bobpass");
        LoginResult loginResult = service.login(loginRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("game1");
        CreateGameResult createGameResult = service.createGame(createGameRequest, loginResult.authToken());
        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", createGameResult.gameID());
        service.joinGame(joinGameRequest, loginResult.authToken());
        ListGamesResult listGamesResult = service.listGames(loginResult.authToken());
        assertEquals(listGamesResult.games().get(0).whiteUsername(), "bob");
    }

    @Test
    void joinGameFail() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("bob", "bobpass", "bob@mail");
        service.register(request);
        LoginRequest loginRequest = new LoginRequest("bob", "bobpass");
        LoginResult loginResult = service.login(loginRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("game1");
        CreateGameResult createGameResult = service.createGame(createGameRequest, loginResult.authToken());
        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", createGameResult.gameID());
        assertThrows(DataAccessException.class, () -> service.joinGame(joinGameRequest, "bad token"));
    }
}
