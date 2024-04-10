package webSocketRequests;
import chess.ChessGame;
import webSocketMessages.userCommands.UserGameCommand;
public class PlayerJoin extends UserGameCommand{
    private final ChessGame.TeamColor playerColor;
    public PlayerJoin(String authToken, int gameID, ChessGame.TeamColor playerColor) {
        super(authToken, gameID);
        this.playerColor = playerColor;
        this.commandType = CommandType.JOIN_PLAYER;
    }
    public ChessGame.TeamColor getPlayerColor(){
        return playerColor;
    }
}
