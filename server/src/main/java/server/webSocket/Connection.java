package server.webSocket;

import org.eclipse.jetty.websocket.api.Session;

import webSocketMessages.serverMessages.ServerMessage;
import java.io.IOException;

public class Connection {
    public String authToken;
    public Session session;

    public Connection(String authToken, Session session) {
        this.authToken = authToken;
        this.session = session;
    }

    public void send(String message) throws IOException {
        session.getRemote().sendString(message);
    }
}
