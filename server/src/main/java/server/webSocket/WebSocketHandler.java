package server.webSocket;


import chess.ChessGame;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import webSocketMessages.userCommands.UserGameCommand;
import webSocketRequests.*;
import com.google.gson.Gson;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class WebSocketHandler {
    private final ConnectionManager connectionManager = new ConnectionManager();
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
        connectionManager.addMember(playerJoin.getGameID(), playerJoin.getAuthString(), session); // 将用户加入到某个游戏里
        ChessGame.TeamColor teamColor = playerJoin.getPlayerColor(); // 得到用户加入游戏的颜色是什么

        // 检查用户加入游戏的颜色有没有被占用
        sqlGame mysqlGame = new sqlGame();
        sqlAuth mysqlAuth = new sqlAuth(); // 得到sqlAuth来get username
        GameData game = mysqlGame.getGame(joinplayer.getGameID());
        ChessGame chessgame = new Gson().fromJson(game.game(), ChessGame.class);
        String username = mysqlAuth.getUserName(joinplayer.getAuthString()); // 得到了username
        if (teamColor == ChessGame.TeamColor.BLACK)
        {
            if (!username.equals(game.blackUsername()))
            {
                try
                {
                    connectionManager.sendError(joinplayer.getAuthString(),
                            new WSError("Error: This username is already taken.", ServerMessage.ServerMessageType.ERROR) );
                    System.out.println("sent error");
                }
                catch(IOException E)
                {
                    throw new IOException(E.getMessage());
                }
                return;
            }
        }

        if (teamColor == ChessGame.TeamColor.WHITE)
        {
            if(!username.equals(game.whiteUsername()))
            {
                try
                {
                    connectionManager.sendError(joinplayer.getAuthString(), new WSError("Error: This username is already taken.", ServerMessage.ServerMessageType.ERROR));
                    System.out.println("send error");
                }
                catch (IOException E)
                {
                    throw new IOException(E.getMessage());
                }
                return;
            }
        }


        // 通过sqlgame 得到username
        // 创造一个myConnectionManager
        Notification notification = new Notification("A player " + username + " is joining the game with team: " + teamColor); // 会得到一个joinPlayer 的notification
        var loadGame = new LoadGame(chessgame);
        try
        {
            connectionManager.broadcast(joinplayer.getAuthString(), notification, joinplayer.getGameID()); // 然后我再把这个notification发送给其他所有人
            connectionManager.sendOneLoad(joinplayer.getGameID(), joinplayer.getAuthString(), loadGame);
        }
        catch (IOException e)
        {
            throw new IOException(e.getMessage());
        }
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
