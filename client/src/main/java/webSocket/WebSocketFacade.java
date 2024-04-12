package webSocket;

import dataAccess.DataAccessException;
import ui.BoardPrinter;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketResponse.*;
import webSocketRequests.*;
import chess.*;
import client.ClientMain;
import com.google.gson.Gson;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

public class WebSocketFacade extends Endpoint{
    static String authToken;
    Session session;
    public WebSocketFacade(String url, String authToken) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socket = new URI(url + "/connect");
            this.authToken = authToken;
            WebSocketContainer cont = ContainerProvider.getWebSocketContainer();
            this.session = cont.connectToServer(this, socket);
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage return_message = new Gson().fromJson(message, ServerMessage.class);
                    if(return_message.getServerMessageType().equals(ServerMessage.ServerMessageType.NOTIFICATION)) {
                        ClientMain.messageType = ServerMessage.ServerMessageType.NOTIFICATION;
                        Notification notification = new Gson().fromJson(message, Notification.class);
                        System.out.println("\n" + notification.getMessage());
                    }
                    else if (return_message.getServerMessageType().equals(ServerMessage.ServerMessageType.LOAD_GAME)){
                        ClientMain.messageType = ServerMessage.ServerMessageType.LOAD_GAME;
                        LoadGame game = new Gson().fromJson(message, LoadGame.class);
                        ClientMain.chessGame = game.getGame();
                        ClientMain.teamColor = game.getTeamColor();
                        System.out.println();
                        if(ClientMain.teamColor.equals(null)){
                            BoardPrinter.printAll(ClientMain.chessGame.getBoard());
                        }
                        else {
                            BoardPrinter.printBoard(ClientMain.chessGame.getBoard(), ClientMain.teamColor);
                        }
                    }
                    else if (return_message.getServerMessageType().equals(ServerMessage.ServerMessageType.ERROR)){
                        ClientMain.messageType = ServerMessage.ServerMessageType.ERROR;
                        SocketError error = new Gson().fromJson(message, SocketError.class);
                        System.out.println("\n" + error.getErrorMessage());
                    }

                }
            });
        }
        catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }


    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    public void leave(int gameID) throws DataAccessException {
        try {
            var action = new Leave(authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }
    public void resign(int gameID) throws DataAccessException {
        try {
            var action = new Resign(authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }
    public void makeMove(int gameID, ChessMove move) throws DataAccessException {
        try {
            var action = new MakeMove(authToken, gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }
    public void joinObserver(int gameID) throws DataAccessException {
        try {
            var action = new ObserverJoin(authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }
    public void joinPlayer(int gameID, ChessGame.TeamColor playerColor) throws DataAccessException {
        try {
            var action = new PlayerJoin(authToken, gameID, playerColor);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }
}
