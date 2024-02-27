package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import request.ListGamesRequest;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.FailureResult;
import result.ListGamesResult;
import result.LoginResult;
import result.RegisterResult;
import service.UserService;
import spark.*;

public class Server {
    private final UserService service = new UserService();

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");
        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
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

    //depends on authtoken, remove the authtoken needed, authtoken linked to user
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

        }
        catch (DataAccessException e){
            return errorResult(e, response);
        }
    }

    private String errorResult(DataAccessException e, Response response){
        switch (e.getMessage()){
            case "Error: bad request":
                response.status(400);
            case "Error: unauthorized":
                response.status(401);
            case "Error: already taken":
                response.status(403);
            default:
                response.status(500);
        }
        FailureResult result = new FailureResult(e.getMessage());
        return new Gson().toJson(result);
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }


}
