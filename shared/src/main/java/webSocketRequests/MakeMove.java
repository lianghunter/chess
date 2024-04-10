package webSocketRequests;

import webSocketMessages.userCommands.UserGameCommand;
import chess.ChessMove;
public class MakeMove extends UserGameCommand{
    private final ChessMove move;

    public MakeMove(String authToken, int gameID, ChessMove move) {
        super(authToken, gameID);
        this.move = move;
        this.commandType = CommandType.MAKE_MOVE;
    }

    public ChessMove getMove()
    {
        return move;
    }
}
