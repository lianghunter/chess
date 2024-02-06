package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor turn;

    public ChessGame() {
        this.turn = TeamColor.WHITE;
        this.board = new ChessBoard();
        this.board.resetBoard();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && turn == chessGame.turn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, turn);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();
        ChessPiece currentPiece = this.board.getPiece(startPosition);
        if(currentPiece == null){
            return null;
        }

        ChessPiece removedPiece = null;
        Collection<ChessMove> testMoves = currentPiece.pieceMoves(this.board, startPosition);
        for(ChessMove move : testMoves){
            this.board.removePiece(startPosition);
            removedPiece = this.board.getPiece(move.getEndPosition());
            this.board.addPiece(move.getEndPosition(), currentPiece);
            if(!isInCheck(currentPiece.getTeamColor())){
                moves.add(move);
            }
            this.board.addPiece(startPosition, currentPiece);
            this.board.addPiece(move.getEndPosition(), removedPiece);
        }
        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> valMoves = validMoves(move.getStartPosition());
        ChessPiece currentPiece = this.board.getPiece(move.getStartPosition());
        if(!valMoves.contains(move) || currentPiece.getTeamColor() != this.turn){
            throw new InvalidMoveException("Invalid Move");
        }
        this.board.removePiece(move.getStartPosition());
        ChessPiece removedPiece = this.board.getPiece(move.getEndPosition());
        ChessPiece promotionPiece = null;
        if(move.getPromotionPiece() != null){
            promotionPiece = new ChessPiece(currentPiece.getTeamColor(), move.getPromotionPiece());
            this.board.addPiece(move.getEndPosition(), promotionPiece);
        }
        else {
            this.board.addPiece(move.getEndPosition(), currentPiece);
        }
        if(isInCheck(currentPiece.getTeamColor())){
            this.board.addPiece(move.getStartPosition(), currentPiece);
            this.board.addPiece(move.getEndPosition(), removedPiece);
            throw new InvalidMoveException("Invalid Move");
        }
        if(this.turn == TeamColor.WHITE){
            this.turn = TeamColor.BLACK;
        }
        else{
            this.turn = TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     * loop through all pieces, find the king and find enemy pieces+locations
     * using list of enemy pieces, check each pieces' moves and see if they can attack the king
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPiece selfKing = null;
        ChessPosition selfKingPos = null;
        ArrayList<ChessPiece> enemyPieces = new ArrayList<>();
        ArrayList<ChessPosition> enemyPositions = new ArrayList<>();
        ChessPiece currentPiece;

        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                currentPiece = this.board.getPiece(new ChessPosition(i, j));
                if(currentPiece != null){
                    if(currentPiece.getPieceType() == ChessPiece.PieceType.KING &&
                            currentPiece.getTeamColor() == teamColor){
                        selfKing = currentPiece;
                        selfKingPos = new ChessPosition(i, j);
                    }
                    if(currentPiece.getTeamColor() != teamColor){
                        enemyPieces.add(currentPiece);
                        enemyPositions.add(new ChessPosition(i, j));
                    }
                }
            }
        }

        if(selfKing == null){
            return false;
        }

        Collection<ChessMove> enemyMoves = new ArrayList<>();
        for(int i = 0; i < enemyPieces.size(); i++){
            enemyMoves = enemyPieces.get(i).pieceMoves(this.board, enemyPositions.get(i));
            for(ChessMove move : enemyMoves){
                if(move.getEndPosition().getRow() == selfKingPos.getRow() &&
                        move.getEndPosition().getColumn() == selfKingPos.getColumn()){
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(isInCheck(teamColor)){
            return noValidMoves(teamColor);
        }
        return false;
    }


    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(!isInCheck(teamColor)){
            return noValidMoves(teamColor);
        }
        return false;
    }

    public boolean noValidMoves(TeamColor teamColor){
        ChessPiece currentPiece;
        ArrayList<ChessPosition> selfPositions = new ArrayList<>();
        Collection<ChessMove> selfMoves = new ArrayList<>();
        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                currentPiece = this.board.getPiece(new ChessPosition(i, j));
                if(currentPiece != null){
                    if(currentPiece.getTeamColor() == teamColor){
                        selfMoves.addAll(validMoves(new ChessPosition(i, j)));
                    }
                }
            }
        }

        if(selfMoves.isEmpty()){
            return true;
        }
        return false;
    }
    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
