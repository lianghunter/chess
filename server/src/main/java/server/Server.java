package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import request.*;
import result.*;
import service.UserService;
import spark.*;
import server.webSocket.WebSocketHandler;

public class Server {
    private final UserService service = new UserService();

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");
        WebSocketHandler handler = new WebSocketHandler();
        Spark.webSocket("/connect", handler);
        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.init();
        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object clear(Request request, Response response) {
        try{
            service.clear();
            response.status(200);
            return "{}";
        }
        catch (DataAccessException e){
            return errorResult(e, response);
        }
    }

    private Object register(Request request, Response response){
        try{
            RegisterRequest registerRequest = new Gson().fromJson(request.body(), RegisterRequest.class);
            RegisterResult result = service.register(registerRequest);
            response.status(200);
            return new Gson().toJson(result);
        }
        catch (DataAccessException e){
            return errorResult(e, response);
        }
    }

    private Object login(Request request, Response response){
        try{
            LoginRequest loginRequest = new Gson().fromJson(request.body(), LoginRequest.class);
            LoginResult result = service.login(loginRequest);
            response.status(200);
            return new Gson().toJson(result);
        }
        catch (DataAccessException e){
            return errorResult(e, response);
        }
    }

    //depends on authToken, remove the authToken needed, authToken linked to user
    private Object logout(Request request, Response response){
        try {
            String authToken = request.headers("authorization");
            service.logout(authToken);
            response.status(200);
            return "{}";
        }
        catch (DataAccessException e){
            return errorResult(e, response);
        }
    }

    private Object listGames(Request request, Response response){
        try {
            String authToken = request.headers("authorization");
            ListGamesResult result = service.listGames(authToken);
            response.status(200);
            return new Gson().toJson(result);
        }
        catch (DataAccessException e){
            return errorResult(e, response);
        }
    }

    public Object createGame(Request request, Response response){
        try {
            String authToken = request.headers("authorization");
            //LoginRequest loginRequest = new Gson().fromJson(request.body(), LoginRequest.class);
            CreateGameRequest createGameRequest = new Gson().fromJson(request.body(), CreateGameRequest.class);
            CreateGameResult result = service.createGame(createGameRequest, authToken);
            response.status(200);
            return new Gson().toJson(result);
        }
        catch (DataAccessException e){
            return errorResult(e, response);
        }
    }

    public Object joinGame(Request request, Response response){
        try {
            String authToken = request.headers("authorization");
            JoinGameRequest joinGameRequest = new Gson().fromJson(request.body(), JoinGameRequest.class);
            service.joinGame(joinGameRequest, authToken);
            response.status(200);
            return "{}";
        }
        catch (DataAccessException e){
            return errorResult(e, response);
        }
    }

    private String errorResult(DataAccessException e, Response response){
        switch (e.getMessage()){
            case "Error: bad request":
                response.status(400);
                break;
            case "Error: unauthorized":
                response.status(401);
                break;
            case "Error: already taken":
                response.status(403);
                break;
            default:
                response.status(500);
                break;
        }
        FailureResult result = new FailureResult(e.getMessage());
        return new Gson().toJson(result);
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }


}
