package webSocketResponse;
import webSocketMessages.serverMessages.ServerMessage;
import chess.ChessGame;

public class LoadGame extends ServerMessage{
    public LoadGame(ChessGame game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }
    public ChessGame getGame() {
        return this.game;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }
    private ChessGame game;
}
