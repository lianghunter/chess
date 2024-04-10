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
        ArrayList<Connection> singleGame = this.concurrentMap.get(gameID);
        for (Connection connection : singleGame)
        {
            if (connection.session.isOpen())
            {
                if (!connection..equals(excludeVistorName))
                {
                    String msg = new Gson().toJson(serverMessage, Notification.class);
                    connection.send(msg);
                }
            }
            else
            {
            }
        }

        for (var c : removeList)
        {
            singleGame.remove(c);
        }

    }

    public void sendOneLoad(int gameID, String authToken, LoadGame loadGame) throws IOException {
        var removeList = new Vector<Connection>();
        for (var c : connections.get(gameID))
        {
            if (c.session.isOpen())
            {
                if (c.memberAuthToken.equals(authToken))
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
    }

    public void sendError(String authToken, WSError error) throws IOException {
        ArrayList<Connection> removeList = new ArrayList<Connection>();
        for (int gameID : connections.keySet()) {
            for (var c : connections.get(gameID))
            {
                if (c.session.isOpen()) {
                    if (c.memberAuthToken.equals(authToken))
                    {
                        String message = new Gson().toJson(error);
                        c.send(message);
                    }
                } else {
                    removeList.add(c);
                }
            }

            for (var c : removeList) {
                Vector<Connection> tmp = connections.get(gameID);
                tmp.remove(c);
                connections.put(gameID, tmp);
            }
        }


    }
}
