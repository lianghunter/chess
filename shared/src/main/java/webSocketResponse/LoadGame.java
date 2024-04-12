package webSocketResponse;
import webSocketMessages.serverMessages.ServerMessage;
import chess.ChessGame;

public class LoadGame extends ServerMessage{
    public LoadGame(ChessGame game, ChessGame.TeamColor color) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
        this.teamColor = color;
    }
    public ChessGame getGame() {
        return this.game;
    }
    public ChessGame.TeamColor getTeamColor() {return this.teamColor;}
    private ChessGame game;
    ChessGame.TeamColor teamColor;
}
