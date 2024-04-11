package server.webSocket;


import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import dataAccess.DataAccessException;
import dataAccess.SQLAuthDAO;
import dataAccess.SQLGameDAO;
import dataAccess.SQLUserDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.userCommands.UserGameCommand;
import webSocketRequests.*;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import webSocketResponse.LoadGame;
import webSocketResponse.Notification;
import webSocketResponse.SocketError;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connectionManager = new ConnectionManager();
    private final ConnectionManager connections = new ConnectionManager();
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException, IllegalAccessException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch(userGameCommand.getCommandType())
        {
            case JOIN_PLAYER -> joinPlayer(new Gson().fromJson(message, PlayerJoin.class), session);
            case JOIN_OBSERVER -> joinObserver(new Gson().fromJson(message, ObserverJoin.class), session);
            case MAKE_MOVE -> makeMove(new Gson().fromJson(message, MakeMove.class));
            case LEAVE -> leave(new Gson().fromJson(message, Leave.class));
            case RESIGN -> resign(new Gson().fromJson(message, Resign.class));
        }
    }

    private void joinPlayer(PlayerJoin playerJoin, Session session) throws IOException, DataAccessException, IllegalAccessException {
        validateGameAndAuth(playerJoin, session);
        ChessGame.TeamColor teamColor = playerJoin.getPlayerColor();
        SQLGameDAO gameDAO = new SQLGameDAO();
        SQLAuthDAO authDAO = new SQLAuthDAO();
        GameData game = gameDAO.getGame(playerJoin.getGameID());
        ChessGame chessgame = game.game();
        String username = authDAO.getUserFromAuth(playerJoin.getAuthString());

        if (teamColor == ChessGame.TeamColor.BLACK)
        {
            if (!username.equals(game.blackUsername()))
            {
                connectionManager.sendError(playerJoin.getAuthString(),
                        new SocketError("User already exists in game.") );
                return;
            }
        }

        if (teamColor == ChessGame.TeamColor.WHITE)
        {
            if(!username.equals(game.whiteUsername()))
            {
                connectionManager.sendError(playerJoin.getAuthString(), new SocketError("User already exists in game."));
                return;
            }
        }

        Notification notif = new Notification("Player: " + username + " joined " + teamColor);
        var loadGame = new LoadGame(chessgame);
        connectionManager.broadcast(playerJoin.getAuthString(), notif, playerJoin.getGameID(), false);
        connectionManager.sendMessage(playerJoin.getGameID(), playerJoin.getAuthString(), loadGame);
    }

    private void joinObserver(ObserverJoin joinObserver, Session session) throws IOException, DataAccessException, IllegalAccessException {
        validateGameAndAuth(joinObserver, session);
        int gameID = joinObserver.getGameID();
        String auth = joinObserver.getAuthString();

        SQLGameDAO mysqlGame = new SQLGameDAO();
        SQLAuthDAO mysqlAuth = new SQLAuthDAO();
        GameData game = mysqlGame.getGame(gameID);
        ChessGame chessGame = game.game();
        String username = mysqlAuth.getUserFromAuth(auth); // 得到username

        Notification notification = new Notification("A player called " + username + " is observing the game.");
        var loadGame = new LoadGame(chessGame);
        connectionManager.broadcast(auth, notification, gameID, false);
        connectionManager.sendMessage(gameID, auth, loadGame);
    }

    private void makeMove(MakeMove move) throws DataAccessException, IOException {
        int gameID = move.getGameID();
        String auth = move.getAuthString();
        SQLGameDAO theSqlGame = new SQLGameDAO();
        SQLAuthDAO theSqlAuth = new SQLAuthDAO();
        String username = theSqlAuth.getUserFromAuth(auth);
        GameData theGame = theSqlGame.getGame(gameID);
        ChessMove theMove = move.getMove();
        ChessGame realGame = theGame.game();
        ChessPiece startPiece = realGame.getBoard().getPiece(theMove.getStartPosition());

        if(realGame.resigned){
            connectionManager.sendError(auth, new SocketError("You cannot move after another user resigned."));
            return;
        }

        ChessGame.TeamColor selfColor;
        if(theGame.whiteUsername().equals(username)){
            selfColor = ChessGame.TeamColor.WHITE;
        } else if (theGame.blackUsername().equals(username)) {
            selfColor = ChessGame.TeamColor.BLACK;
        }
        else {
            connectionManager.sendError(auth, new SocketError("not your piece"));
            return;
        }

        if(selfColor != realGame.getBoard().getPiece(theMove.getStartPosition()).getTeamColor()){
            connectionManager.sendError(auth, new SocketError("not your piece"));
            return;
        }

        try
        {
            realGame.makeMove(theMove);
        }
        catch (Exception e)
        {
            try {
                connectionManager.sendError(auth, new SocketError(e.getMessage()));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            return;
        }

        theSqlGame.updateGame(realGame, gameID);
        var loadGameMessage = new LoadGame(realGame);
        Notification notification = new Notification(username + " moved " + startPiece.getPieceType().toString() + " from " +
                theMove.getStartPosition().toString() + " to " + theMove.getEndPosition().toString() + ".");
        connectionManager.broadcast(auth, notification, gameID, false);
        connectionManager.sendMessage(gameID, loadGameMessage);
    }

    private void leave(Leave leave) throws DataAccessException, IOException {
        String auth = leave.getAuthString();
        int gameID = leave.getGameID();
        SQLGameDAO theSqlGame = new SQLGameDAO();
        SQLAuthDAO theSqlAuth = new SQLAuthDAO();
        SQLUserDAO theSqlUser = new SQLUserDAO();
        String username = theSqlAuth.getUserFromAuth(auth);
        GameData game = theSqlGame.getGame(gameID);
        ChessGame realGame = game.game();

        ChessGame.TeamColor selfColor = null;
        if (game.whiteUsername().equals(username)) {
            selfColor = ChessGame.TeamColor.WHITE;
        } else if (game.blackUsername().equals(username)) {
            selfColor = ChessGame.TeamColor.BLACK;
        } else
        {
            connectionManager.sendError(auth, new SocketError("The color does not exist.")); // 否则的话则说颜色没有被鉴别
        }

        Notification notification = new Notification(username + " is leaving the game.");
        connectionManager.broadcast(auth, notification, gameID, false); // send to others
        connectionManager.remove(auth, gameID); // 将用户从游戏里删除
        assert selfColor != null;
        theSqlUser.removeUser(selfColor, gameID);
    }

    private void resign(Resign resign) throws DataAccessException, IOException {
        String auth = resign.getAuthString();
        Integer gameID = resign.getGameID();
        SQLGameDAO theSqlGame = new SQLGameDAO();
        SQLAuthDAO theSqlAuth = new SQLAuthDAO();
        SQLUserDAO theSqlUser = new SQLUserDAO();
        String username = theSqlAuth.getUserFromAuth(auth); // 得到username
        GameData game = theSqlGame.getGame(gameID);
        ChessGame realGame = game.game();

        if(realGame.resigned){
            connectionManager.sendError(auth, new SocketError("Game is over, cannot resign.")); // 否则的话则说颜色没有被鉴别
            return;
        }

        if( !( game.whiteUsername().equals(username) || game.blackUsername().equals(username) ) )
        {
            connectionManager.sendError(auth, new SocketError("Observer cannot resign."));
            return;
        }

        ChessGame.TeamColor selfColor = null;
        if (game.whiteUsername().equals(username)) {
            selfColor = ChessGame.TeamColor.WHITE;
        } else if (game.blackUsername().equals(username)) {
            selfColor = ChessGame.TeamColor.BLACK;
        }

        realGame.setTeamTurn(null);
        Notification notification = new Notification(username + " resigned the game.");
        realGame.resigned = true;
        theSqlGame.updateGame(realGame, gameID);
        connectionManager.broadcast(auth, notification, gameID, true);

        connectionManager.remove(auth, gameID);
        assert selfColor != null;
        theSqlUser.removeUser(selfColor, gameID);
    }

    private void validateGameAndAuth(UserGameCommand userGameCommand, Session session) throws IOException, DataAccessException, IllegalAccessException {
        int gameID = userGameCommand.getGameID();
        String auth = userGameCommand.getAuthString();
        connectionManager.addMember(gameID, auth, session);
        SQLGameDAO mysqlGame = new SQLGameDAO();
        SQLAuthDAO mysqlAuth = new SQLAuthDAO();
        try {
            mysqlGame.getGame(gameID);
        }
        catch (Exception e){
            connectionManager.sendError(userGameCommand.getAuthString(), new SocketError(e.getMessage()));
            return;
        }
        try {
            mysqlAuth.authExists(auth);
        }
        catch (Exception e){
            connectionManager.sendError(userGameCommand.getAuthString(), new SocketError(e.getMessage()));
        }
    }


}
