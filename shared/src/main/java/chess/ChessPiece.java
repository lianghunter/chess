package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor color;
    private ChessPiece.PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.pieceType = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */

    /*
    what piece is in position
    promotion -> 4 moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece currentPiece = board.getPiece(myPosition);
        PieceType currentType = currentPiece.pieceType;
        ChessGame.TeamColor currentColor = color;
        List<ChessMove> moveList = new ArrayList<>();
        int col = myPosition.getColumn();
        int row = myPosition.getRow();
        switch (currentType){
            case KING:
                moveList.addAll(diagonal(board,row,col, currentColor, currentType));
                moveList.addAll(straight(board,row,col, currentColor, currentType));
                break;
            case QUEEN:
                moveList.addAll(diagonal(board,row,col, currentColor, currentType));
                moveList.addAll(straight(board,row,col, currentColor, currentType));
                break;
            case BISHOP:
                moveList = diagonal(board, row, col, currentColor, currentType);
                break;
            case KNIGHT:
                break;
            case ROOK:
                moveList = straight(board, row, col, currentColor, currentType);
                break;
            case PAWN:
                moveList = pawnMoves(board,row, col, currentColor, currentType);
                break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + currentType.toString());
        }
        //needs to be changed
        return moveList;
    }

    public static List<ChessMove> diagonal(ChessBoard chessBoard, int row, int col, ChessGame.TeamColor currentColor, PieceType currentType) {
        ChessPosition startPos = new ChessPosition(row, col);
        List<ChessMove> validMoves = new ArrayList<>();

        // right down
        for (int i = row - 1, j = col + 1; i >= 1 && j <= 8; i--, j++) {
            if (addCheck(chessBoard, currentColor, startPos, validMoves, i, j)) break;
            if(currentType == PieceType.KING) break;
        }

        // left down
        for (int i = row - 1, j = col - 1; i >= 1 && j >= 1; i--, j--) {
            if (addCheck(chessBoard, currentColor, startPos, validMoves, i, j)) break;
            if(currentType == PieceType.KING) break;
        }

        // right up
        for (int i = row + 1, j = col + 1; i <= 8 && j <= 8; i++, j++) {
            if (addCheck(chessBoard, currentColor, startPos, validMoves, i, j)) break;
            if(currentType == PieceType.KING) break;
        }

        // left up
        for (int i = row + 1, j = col - 1; i <= 8 && j >= 1; i++, j--) {
            if (addCheck(chessBoard, currentColor, startPos, validMoves, i, j)) break;
            if(currentType == PieceType.KING) break;
        }

        return validMoves;
    }

    public static List<ChessMove> straight(ChessBoard chessBoard, int row, int col, ChessGame.TeamColor currentColor, PieceType currentType) {
        ChessPosition startPos = new ChessPosition(row, col);
        List<ChessMove> validMoves = new ArrayList<>();

        //up
        for (int i = col + 1; i <= 8; i++) {
            if (addCheck(chessBoard, currentColor, startPos, validMoves, row, i)) break;
            if(currentType == PieceType.KING) break;
        }

        //down
        for (int i = col - 1; i >= 1; i--) {
            if (addCheck(chessBoard, currentColor, startPos, validMoves, row, i)) break;
            if(currentType == PieceType.KING) break;
        }

        //left
        for (int i = row - 1; i >= 1; i--) {
            if (addCheck(chessBoard, currentColor, startPos, validMoves, i, col)) break;
            if(currentType == PieceType.KING) break;
        }

        //right
        for (int i = row + 1; i <= 8; i++) {
            if (addCheck(chessBoard, currentColor, startPos, validMoves, i, col)) break;
            if(currentType == PieceType.KING) break;
        }

        return validMoves;
    }

    private static boolean addCheck(ChessBoard chessBoard, ChessGame.TeamColor currentColor, ChessPosition startPos, List<ChessMove> validMoves, int i, int j) {
        ChessPiece observedPiece = chessBoard.getPiece(new ChessPosition(i, j));
        if(observedPiece != null){
            if(observedPiece.color == currentColor){
                return true;
            }
            validMoves.add(new ChessMove(startPos, new ChessPosition(i, j), null));
            return true;
        }
        validMoves.add(new ChessMove(startPos, new ChessPosition(i, j), null));
        return false;
    }

    public static List<ChessMove> pawnMoves(ChessBoard chessBoard, int row, int col, ChessGame.TeamColor currentColor, PieceType currentType){
        ChessPosition startPos = new ChessPosition(row, col);
        List<ChessMove> validMoves = new ArrayList<>();
        int startRow;
        int upDown;
        if(currentColor == ChessGame.TeamColor.WHITE){
            startRow = 2;
            upDown = 1;
        }
        else {
            startRow = 7;
            upDown = -1;
        }
        int nextRow = row + upDown;
        //check if single move is clear, change promotion later?
        if(nextRow >= 1 && nextRow <= 8 && chessBoard.getPiece(new ChessPosition(nextRow, col)) == null){
            validMoves.add(new ChessMove(startPos, new ChessPosition(nextRow, col), null));
        }
        //check diagonals
        //next and right
        int nextCol = col + 1;
        ChessPiece observedPiece = chessBoard.getPiece(new ChessPosition(nextRow, nextCol));
        //if there is a piece of the opposite color
        if(observedPiece != null && observedPiece.color != currentColor){
            validMoves.add(new ChessMove(startPos, new ChessPosition(nextRow, nextCol), null));
        }
        //next and left
        nextCol -= 2;
        observedPiece = chessBoard.getPiece(new ChessPosition(nextRow, nextCol));
        if(observedPiece != null && observedPiece.color != currentColor){
            validMoves.add(new ChessMove(startPos, new ChessPosition(nextRow, nextCol), null));
        }

        //if at starting position
        if(startRow == row){
            //check if second row is clear
            nextRow += upDown;
            if(nextRow >= 1 && nextRow <= 8 && chessBoard.getPiece(new ChessPosition(nextRow, col)) == null){
                validMoves.add(new ChessMove(startPos, new ChessPosition(nextRow, col), null));
            }
        }
        return validMoves;
    }
}
