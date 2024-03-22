package clientTests;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.util.ArrayList;
import java.util.List;

import request.*;
import result.*;
import server.Server;
import facade.ServerFacade;
import client.ClientMain;
import javax.naming.CommunicationException;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    private static final RegisterRequest jimRegisterRequest =
            new RegisterRequest("jim", "tomato", "jim@jim.com");
    private static final LoginRequest jimLoginRequest = new LoginRequest("jim", "tomato");

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clear() throws CommunicationException {
        facade.clear();
    }

    @Test
    public void registerGood()throws CommunicationException{
        RegisterResult result = facade.register(jimRegisterRequest);
        String authToken = result.authToken();
        String username = result.username();
        assertEquals("jim", username);
        assertNotEquals("", authToken);
    }
    @Test
    public void registerBad()throws CommunicationException{
        facade.register(jimRegisterRequest);
        assertThrows(CommunicationException.class, () -> facade.register(jimRegisterRequest));
    }
    @Test
    public void loginGood() throws CommunicationException{
        String auth = facade.register(jimRegisterRequest).authToken();
        facade.logout(auth);
        LoginResult result = facade.login(jimLoginRequest);
        assertNotEquals("", result.authToken());
    }
    @Test
    public void loginBad() throws CommunicationException{
        String auth = facade.register(jimRegisterRequest).authToken();
        facade.logout(auth);
        LoginRequest request = new LoginRequest("jim", "banana");
        assertThrows(CommunicationException.class, () -> facade.login(request));
    }
    @Test
    public void logoutGood() throws CommunicationException{
        String auth = facade.register(jimRegisterRequest).authToken();
        facade.logout(auth);
        assertThrows(CommunicationException.class, () -> facade.listGames(auth));
    }
    @Test
    public void logoutBad() throws CommunicationException{
        facade.register(jimRegisterRequest).authToken();
        assertThrows(CommunicationException.class, () -> facade.logout(""));
    }
    @Test
    public void createGood() throws CommunicationException{
        String auth = facade.register(jimRegisterRequest).authToken();
        CreateGameResult result = facade.createGame(new CreateGameRequest("kiwi"), auth);
        assertNotEquals(0, result.gameID());
    }
    @Test
    public void createBad() throws CommunicationException{
        String auth = facade.register(jimRegisterRequest).authToken();
        assertThrows(CommunicationException.class, () -> facade.createGame(new CreateGameRequest("kiwi"), ""));
    }
    @Test
    public void joinGood() throws CommunicationException{
        String auth = facade.register(jimRegisterRequest).authToken();
        int i = facade.createGame(new CreateGameRequest("kiwi"), auth).gameID();
        facade.join(new JoinGameRequest("BLACK", i), auth);
    }@Test
    public void joinBad() throws CommunicationException{
        String auth = facade.register(jimRegisterRequest).authToken();
        facade.createGame(new CreateGameRequest("kiwi"), auth).gameID();
        assertThrows(CommunicationException.class, () -> facade.join(new JoinGameRequest("BLACK", 0), auth));
    }
    @Test
    public void listGood() throws CommunicationException{
        String auth = facade.register(jimRegisterRequest).authToken();
        facade.createGame(new CreateGameRequest("kiwi"), auth);
        int coconutID = facade.createGame(new CreateGameRequest("coconut"), auth).gameID();
        List<GameData> games = facade.listGames(auth).games();
        assertEquals(new GameData(coconutID, null, null, "coconut", new ChessGame()), games.get(0));
    }
    @Test
    public void listBad() throws CommunicationException{
        String auth = facade.register(jimRegisterRequest).authToken();
        facade.createGame(new CreateGameRequest("kiwi"), auth);
        int coconutID = facade.createGame(new CreateGameRequest("coconut"), auth).gameID();
        assertThrows(CommunicationException.class, () -> facade.listGames(""));
    }
    @Test
    public void clearGood() throws CommunicationException{
        String auth = facade.register(jimRegisterRequest).authToken();
        facade.createGame(new CreateGameRequest("kiwi"), auth);
        facade.clear();
        auth = facade.register(jimRegisterRequest).authToken();
        assertEquals(0, facade.listGames(auth).games().size());
    }
    @Test
    public void clearBad() throws CommunicationException{
        String auth = facade.register(jimRegisterRequest).authToken();
        facade.createGame(new CreateGameRequest("kiwi"), auth);
        facade.logout(auth);
        facade.clear();
        assertThrows(CommunicationException.class, ()-> facade.login(jimLoginRequest));
    }
}
