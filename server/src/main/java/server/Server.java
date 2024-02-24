package server;

import com.google.gson.Gson;
import request.RegisterRequest;
import service.UserService;
import spark.*;

public class Server {
    private final UserService service = new UserService();

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");
        Spark.delete("/db", this::clear);
        Spark.init();
        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object clear(Request request, Response response) {
        service.clear();
        response.status(200);
        return "";
    }

    private Object register(Request request, Response response){
        RegisterRequest registerRequest = new Gson().fromJson(request.body(), RegisterRequest.class);
        registerRequest.username();
        registerRequest.email();
        registerRequest.password();
        return null;
    }
    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }


}
