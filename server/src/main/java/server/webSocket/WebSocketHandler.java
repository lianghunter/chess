package server.webSocket;


import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import webSocketMessages.userCommands.UserGameCommand;
import webSocketRequests.*;
import com.google.gson.Gson;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException
    {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch(userGameCommand.getCommandType())
        {
            case JOIN_PLAYER -> joinPlayer(new Gson().fromJson(message, PlayerJoin.class), session);
            case JOIN_OBSERVER -> joinObserver(new Gson().fromJson(message, ObserverJoin.class));
            case MAKE_MOVE -> makeMove(new Gson().fromJson(message, MakeMove.class));
            case LEAVE -> leave(new Gson().fromJson(message, Leave.class));
            case RESIGN -> reSign(new Gson().fromJson(message, Resign.class));
        }
    }

    private void joinPlayer(PlayerJoin playerJoin, Session session)
    {
        connections.addMember(playerJoin.getGameID(), playerJoin.getAuthString(), session);
    }

    private void joinObserver(ObserverJoin joinObserver)
    {
    }

    private void makeMove(MakeMove move)
    {}

    private void leave(Leave leave)
    {}

    private void reSign(Resign resign)
    {}


}
