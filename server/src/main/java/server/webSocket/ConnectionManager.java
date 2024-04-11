package server.webSocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import webSocketResponse.*;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, ArrayList<Connection>> concurrentMap = new ConcurrentHashMap<>();
    public void addMember(int gameID, String authToken, Session session){
        var connection = new Connection(authToken, session);
        ArrayList<Connection> singleGame = concurrentMap.get(gameID);
        if (singleGame == null)
        {
            singleGame = new ArrayList<>();
        }
        singleGame.add(connection);
        concurrentMap.put(gameID, singleGame);
    }
    public void remove(String authToken,Integer gameID)
    {
        ArrayList<Connection> singleGame = this.concurrentMap.get(gameID);
        for (Connection c : singleGame)
        {
            if (Objects.equals(c.authToken, authToken))
            {
                singleGame.remove(c);
            }
        }
    }
    public void broadcast(String excludeVistorName, Notification serverMessage, Integer gameID, Boolean selfIncluded) throws IOException
    {
        var removeList = new ArrayList<Connection>();
        ArrayList<Connection> singleGame = this.concurrentMap.get(gameID);
        for (Connection connection : singleGame)
        {
            if (connection.session.isOpen())
            {
                if (!connection.authToken.equals(excludeVistorName) || selfIncluded)
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
            singleGame.remove(c);
        }
    }

    public void sendMessage(int gameID, String authToken, LoadGame loadGame) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : concurrentMap.get(gameID))
        {
            if (c.session.isOpen())
            {
                if (c.authToken.equals(authToken))
                {
                    String msg = new Gson().toJson(loadGame, LoadGame.class);
                    c.send(msg);
                }
            }
            else
            {
                removeList.add(c);
            }
        }
        removeElements(gameID, removeList);
    }

    public void sendMessage(int gameID, LoadGame loadGame) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : concurrentMap.get(gameID))
        {
            if (c.session.isOpen())
            {
                String msg = new Gson().toJson(loadGame, LoadGame.class);
                c.send(msg);
            }
            else
            {
                removeList.add(c);
            }
        }
        removeElements(gameID, removeList);
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

            removeElements(gameID, removeList);
        }
    }

    private void removeElements(int gameID, ArrayList<Connection> removeList) {
        for (var connection : removeList) {
            ArrayList<Connection> temp = concurrentMap.get(gameID);
            temp.remove(connection);
            concurrentMap.put(gameID, temp);
        }
    }
}
