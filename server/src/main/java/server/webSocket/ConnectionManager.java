package server.webSocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import webSocketResponse.*;

import webSocketMessages.serverMessages.ServerMessage;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, ArrayList<Connection>> concurrentMap = new ConcurrentHashMap<>();
    public void addMember(int gameID, String authToken, Session session){
        var connection = new Connection(authToken, session);
        ArrayList<Connection> singleGame = concurrentMap.get(gameID);
        if (singleGame.equals(null))
        {
            singleGame = new ArrayList<>();
        }
        singleGame.add(connection);
    }
    public void remove(String authToken,Integer gameID)
    {
        concurrentMap.remove(authToken);
    }
    public void broadcast(String excludeVistorName, Notification serverMessage, Integer gameID) throws IOException
    {

        var removeList = new ArrayList<Connection>();
        ArrayList<Connection> connections = this.concurrentMap.get(gameID);
        for (Connection connection : connections)
        {
            if (connection.session.isOpen())
            {
                if (!connection.equals(excludeVistorName))
                {
                    String msg = new Gson().toJson(serverMessage, Notification.class);
                    connection.send(msg);
                }
            }
            else
            {
                removeList.add(connection);
            }
        }

        for (var c : removeList)
        {
            connections.remove(c);
        }

    }

    public void sendOneLoad(int gameID, String authToken, LoadGame loadGame) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var connection : concurrentMap.get(gameID))
        {
            if (connection.session.isOpen())
            {
                if (connection.authToken.equals(authToken))
                {
                    String msg = new Gson().toJson(loadGame, LoadGame.class);
                    connection.send(msg);
                }
            }
            else
            {
                removeList.add(connection);
            }
        }
    }

    public void sendError(String authToken, SocketError error) throws IOException {
        ArrayList<Connection> removeList = new ArrayList<Connection>();
        for (int gameID : concurrentMap.keySet()) {
            for (var connection : concurrentMap.get(gameID))
            {
                if (connection.session.isOpen()) {
                    if (connection.authToken.equals(authToken))
                    {
                        String message = new Gson().toJson(error);
                        connection.send(message);
                    }
                } else {
                    removeList.add(connection);
                }
            }

            for (var connection : removeList) {
                ArrayList<Connection> temp = concurrentMap.get(gameID);
                temp.remove(connection);
                concurrentMap.put(gameID, temp);
            }
        }


    }
}
