package ui;

import chess.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class Board {
    public static ChessBoard board = new ChessBoard();
    static {
        board.resetBoard();
    }
    private static final String whitePiece = SET_TEXT_COLOR_LIGHT_GREY;
    private static final String blackPiece = SET_TEXT_COLOR_BLACK;
    private static final String index = SET_TEXT_COLOR_BLACK;
    private static final String border = SET_BG_COLOR_BLUE;
    private static final String textColor = SET_TEXT_COLOR_WHITE;
    private static final String orgBackgroundColor = SET_BG_COLOR_BLACK;
    private static final String boardLight = SET_BG_COLOR_WHITE;
    private static final String boardDark = SET_BG_COLOR_DARK_GREEN;

    public static void printAll(ChessBoard board){
        printBoard(board, ChessGame.TeamColor.WHITE);
        printBlankFullRow(System.out, RESET_BG_COLOR);
        printBoard(board, ChessGame.TeamColor.BLACK);
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print("\u001B[49m");
        out.print(SET_TEXT_COLOR_BLACK);
    }
    public static void printBoard(ChessBoard board, ChessGame.TeamColor teamColor) {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        int spaceCol;
        int [] rowOrder;
        int [] colOrder;
        if (teamColor == ChessGame.TeamColor.WHITE) {
            spaceCol = 0;
            rowOrder = new int [] {8,7,6,5,4,3,2,1};
            colOrder = new int [] {1,2,3,4,5,6,7,8};
        } else {
            spaceCol = 1;
            rowOrder = new int [] {1,2,3,4,5,6,7,8};
            colOrder = new int [] {8,7,6,5,4,3,2,1};
        }

        printHeader(out, teamColor);
        ChessGame.TeamColor currSpaceColor;
        for(int i: rowOrder) {
            out.print(border);
            out.print(index);
            out.print(" \u2006");
            out.print(String.valueOf(i));
            out.print(" \u2006");
            if (i % 2 == spaceCol) { currSpaceColor = ChessGame.TeamColor.WHITE; }
            else { currSpaceColor = ChessGame.TeamColor.BLACK; }
            for (int j: colOrder) {
                if (currSpaceColor == ChessGame.TeamColor.WHITE) {
                    out.print(boardLight);
                    currSpaceColor = ChessGame.TeamColor.BLACK;
                } else {
                    out.print(boardDark);
                    currSpaceColor = ChessGame.TeamColor.WHITE;
                }
                ChessPiece currPiece = board.getPiece(new ChessPosition(i, j));
                if (!(currPiece == null)) { printPiece(out, currPiece.getPieceType(), currPiece.getTeamColor()); }
                else { out.print(EMPTY); }
            }
            out.print(border);
            out.print(EMPTY);
            out.print(orgBackgroundColor);
            out.print(System.lineSeparator());
        }
        printBlankFullRow(out, border);
        out.print(textColor);
        out.print(orgBackgroundColor);
    }
    private static void printHeader(PrintStream out, ChessGame.TeamColor callerColor) {
        String [] headerOrder;
        out.print(border);
        out.print(EMPTY);
        out.print(index);
        if (callerColor == ChessGame.TeamColor.WHITE) {
            headerOrder = new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};
        } else { headerOrder = new String [] {"h", "g", "f", "e", "d", "c", "b", "a"}; }
        for (String headerLetter: headerOrder) {
            out.print(" \u2006");
            out.print(String.valueOf(headerLetter));
            out.print(" \u2006");
        }
        out.print(EMPTY);
        out.print(textColor);
        out.print(orgBackgroundColor);
        out.print(System.lineSeparator());
    }
    private static void printPiece(PrintStream out, ChessPiece.PieceType piece, ChessGame.TeamColor color) {
        if (color == ChessGame.TeamColor.WHITE) { out.print(whitePiece); }
        else { out.print(blackPiece); }
        printPieceIcon(out, piece);
    }

    private static void printPieceIcon(PrintStream out, ChessPiece.PieceType piece) {
        String pieceString;
        switch (piece) {
            case KING -> pieceString = BLACK_KING;
            case QUEEN -> pieceString = BLACK_QUEEN;
            case BISHOP -> pieceString = BLACK_BISHOP;
            case ROOK -> pieceString = BLACK_ROOK;
            case KNIGHT -> pieceString = BLACK_KNIGHT;
            case PAWN -> pieceString = BLACK_PAWN;
            default -> pieceString = EMPTY;
        }
        out.print(pieceString);
    }
    private static void printBlankFullRow(PrintStream out, String color) {
        out.print(color);
        for (int i = 0; i <= 9; i++) { out.print(EMPTY); }
        out.print(orgBackgroundColor);
        out.print(System.lineSeparator());
    }
}
