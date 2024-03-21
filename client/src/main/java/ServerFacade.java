import com.google.gson.Gson;
import request.CreateGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.CreateGameResult;
import result.ListGamesResult;
import result.LoginResult;
import result.RegisterResult;

import javax.naming.CommunicationException;
import java.io.*;
import java.net.*;


public class ServerFacade {
    private final int port;
    private final String url;

    public ServerFacade(int port) {
        this.port = port;
        url = "http://localhost:";
    }

    public RegisterResult register(RegisterRequest request) throws CommunicationException {
        RegisterResult result = null;
        String requestStr = new Gson().toJson(request);
        try {
            HttpURLConnection connection = makeRequest(port, url, "/user", "POST", requestStr, null);
            if (!(connection.getResponseCode() == 200)) {
                throw new Exception(connection.getResponseCode() + connection.getResponseMessage());
            }
            else {
                result = responseBody(connection, RegisterResult.class);
            }
        }
        catch (Exception e){
            throw new CommunicationException(e.getMessage());
        }
        return result;
    }

    public LoginResult login(LoginRequest request) throws CommunicationException{
        LoginResult result = null;
        String requestStr = new Gson().toJson(request);
        try {
            HttpURLConnection connection = makeRequest(port, url, "/session", "POST", requestStr, null);
            if (!(connection.getResponseCode() == 200)) {
                throw new Exception(connection.getResponseCode() + connection.getResponseMessage());
            }
            else {
                result = responseBody(connection, LoginResult.class);
            }
        }
        catch (Exception e){
            throw new CommunicationException(e.getMessage());
        }
        return result;
    }

    public ListGamesResult listGames(String authToken) throws CommunicationException {
        ListGamesResult result;
        try {
            HttpURLConnection connection = makeRequest(port, url, "/game", "GET", "", authToken);
            if (!(connection.getResponseCode() == 200)) {
                throw new Exception(connection.getResponseCode() + connection.getResponseMessage());
            }
            else {
                result = responseBody(connection, ListGamesResult.class);
            }
        } catch (Exception ex) { throw new CommunicationException(ex.getMessage()); }
        return result;
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws CommunicationException {
        CreateGameResult result;
        String requestStr = new Gson().toJson(request);
        try {
            HttpURLConnection connection = makeRequest(port, url, "/game", "POST", requestStr, authToken);
            if (!(connection.getResponseCode() == 200)) {
                throw new Exception(connection.getResponseCode() + connection.getResponseMessage());
            }
            else {
                result = responseBody(connection, CreateGameResult.class);
            }
        } catch (Exception ex) { throw new CommunicationException(ex.getMessage()); }
        return result;
    }

    private HttpURLConnection makeRequest(int port, String stem, String path, String method, String body, String header) throws URISyntaxException, IOException {
        URL url = (new URI(stem + port + path)).toURL();
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod(method);
        if (!(header == null)) { http.setRequestProperty("authorization", header); }
        if (!body.isEmpty()) {
            http.setDoOutput(true);
            try (OutputStream outputStream = http.getOutputStream()) {
                outputStream.write(body.getBytes());
            }
        }
        http.connect();
        return http;
    }

    private static <T> T responseBody(HttpURLConnection connection, Class<T> classType) throws IOException {
        T body;
        try (InputStream inputStream = connection.getInputStream()) {
            body = new Gson().fromJson(new InputStreamReader(inputStream), classType);
        }
        return body;
    }
}
