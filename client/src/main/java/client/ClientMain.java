package client;

import chess.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.module.ResolutionException;
import java.util.ArrayList;
import java.util.List;

import facade.ServerFacade;
import model.GameData;
import request.*;
import result.*;
import ui.*;
import webSocket.WebSocketFacade;
import webSocketMessages.serverMessages.ServerMessage;

import javax.naming.CommunicationException;


public class ClientMain {
    private static final ServerFacade server = new ServerFacade(8080);
    private static WebSocketFacade webSocketFacade;
    public static ChessGame.TeamColor teamColor;
    public static boolean joinFail = false;
    private static int gameID;
    public static ChessGame chessGame;
    public static ServerMessage.ServerMessageType messageType;
    private static final String INTRO= "Welcome to 240 chess. Type \"help\" to view options.";
    private static final String PRELOGIN_UI = """
                    OPTIONS: Please input exactly as shown. 
                    eg.: login steve33 password22
                    register <username> <password> <email>
                    login <username> <password>
                    quit
                    help""";
    private static final String POSTLOGIN_UI = """
                    OPTIONS: Please input exactly as shown. 
                    eg.: login steve33 password22
                    create <name>
                    list
                    join <ID> [WHITE|BLACK|<empty>]
                    observe<ID>
                    logout
                    quit
                    help""";
    private static final String GAME_UI = """
                    OPTIONS: Please input exactly as shown. 
                    eg.: login steve33 password22
                    move <start row, start col> <end row end col> eg. move 11 17
                    redraw
                    resign
                    leave
                    highlight <start row, start col>
                    help""";
    private static boolean loggedIn = false;
    private static boolean inGame = false;
    private static String authToken = null;
    public static String output = "";
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    public static void main(String[] args) throws IOException, CommunicationException {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece
                            + "\n" + INTRO + "\n");
        parseInput();
        reader.close();
    }
    private static void parseInput() throws IOException, CommunicationException {
        if(!loggedIn){
            System.out.print("[LOGGED_OUT] >> ");
        }
        else if(inGame){
            System.out.print("[IN_GAME] >> ");
        }
        else{
            System.out.print("[LOGGED_IN] >> ");
        }
        //take in input
        List<String> wordList = new ArrayList<>();
        try {
            // Read the input line
            String inputLine = reader.readLine();

            // Split the input line by spaces and store in a list
            String[] words = inputLine.split("\\s+"); // "\\s+" means one or more spaces
            for (String word : words) {
                if(word.length() > 0){
                    wordList.add(word);
                }
                else{
                    printBadOutput();
                }
            }
        }
        catch (IOException e) {
            throw e;
        }
        //check if empty
        if(wordList.isEmpty() || wordList.size() > 4){
            printBadOutput();
        }
        //prelogin
        if (!loggedIn) {
            prelogin(wordList);
        }
        else if(inGame){
            postGame(wordList);
        }
        //postlogin
        else {
            postLogin(wordList);
        }
    }

    private static void prelogin(List<String> wordList) throws IOException, CommunicationException {
        switch (wordList.get(0).toLowerCase()) {
            case "help":
                if (wordList.size() != 1) {
                    printBadOutput();
                }
                help();
            case "quit":
                if (wordList.size() != 1) {
                    printBadOutput();
                }
                System.out.println("Exited program");
                break;
            case "login":
                if (wordList.size() != 3) {
                    printBadOutput();
                }
                login(wordList.get(1), wordList.get(2));
                break;
            case "register":
                if (wordList.size() != 4) {
                    printBadOutput();
                }
                register(wordList.get(1), wordList.get(2), wordList.get(3));
                break;
            default:
                printBadOutput();
        }
    }

    private static void postGame(List<String> wordList) throws IOException, CommunicationException {
        /*move <start row, start col> <end row end col> eg. move 11 17
                    redraw
                    resign
                    leave
                    highlight <start row, start col>*/
        switch (wordList.get(0).toLowerCase()) {
            case "help":
                if (wordList.size() != 1) {
                    printBadOutput();
                }
                help();
            case "redraw":
                if (wordList.size() != 1) {
                    printBadOutput();
                }
                redraw();
                break;
            case "leave":
                if (wordList.size() != 1) {
                    printBadOutput();
                }
                leave();
                break;
            case "resign":
                if (wordList.size() != 1) {
                    printBadOutput();
                }
                resign();
                break;
            case "highlight":
                if (wordList.size() != 2) {
                    printBadOutput();
                }
                highlight(wordList.get(1));
                break;
            case "move":
                if (wordList.size() != 3) {
                    printBadOutput();
                }
                move(wordList.get(1), wordList.get(2));
                break;
        }
    }

    private static void postLogin(List<String> wordList) throws IOException, CommunicationException {
        switch (wordList.get(0).toLowerCase()) {
            case "help":
                if (wordList.size() != 1) {
                    printBadOutput();
                }
                help();
                break;
            case "list":
                if (wordList.size() != 1) {
                    printBadOutput();
                }
                listGames();
                break;
            case "create":
                if (wordList.size() != 2) {
                    printBadOutput();
                }
                createGame(wordList.get(1));
                break;
            case "observe":
                if (wordList.size() != 2 || loggedIn == false) {
                    printBadOutput();
                }
                int i = 0;
                try {
                    i = Integer.parseInt(wordList.get(1));
                }
                catch (Exception e){
                    printBadOutput();
                }
                join(i);
                break;
            case "logout":
                if (wordList.size() != 1 || loggedIn == false) {
                    printBadOutput();
                }
                loguot();
                break;
            case "join":
                if (wordList.size() != 3 || loggedIn == false) {
                    printBadOutput();
                }
                int j = 0;
                try {
                    j = Integer.parseInt(wordList.get(1));
                }
                catch (Exception e){
                    printBadOutput();
                }
                String color = null;
                color = wordList.get(2);
                join(j, color);
            default:
                printBadOutput();
        }
    }

    private static void printBadOutput() throws IOException, CommunicationException {
        output = "\nIncorrect or no input. " +
                "Please try again. " +
                "To view available commands, please type \"help\".";
        System.out.println("\nIncorrect or no input. " +
                "Please try again. " +
                "To view available commands, please type \"help\".");
        parseInput();
    }
    private static void printBadOutput(String msg) throws IOException, CommunicationException {
        output = "\nIncorrect or no input. " +
                "Please try again. " +
                "Error message: " + msg +
                " To view available commands, please type \"help\".";
        System.out.println("\nIncorrect or no input. " +
                "Please try again. " +
                "Error message: " + msg +
                " To view available commands, please type \"help\".");
        parseInput();
    }
    private static void help() throws IOException, CommunicationException {
        if(inGame){
            System.out.println(GAME_UI);
        }
        else if(loggedIn){
            System.out.println(POSTLOGIN_UI);
        }
        else {
            System.out.println(PRELOGIN_UI);
        }
        parseInput();
    }
    private static void register(String username, String password, String email) throws IOException, CommunicationException {
        //RegisterResult result =
        try {
            server.register(new RegisterRequest(username, password, email));
            System.out.println("user registered.");
            login(username, password);
        } catch (Exception e) {
            printBadOutput(e.getMessage());
        }
    }
    private static void login(String username, String password) throws IOException, CommunicationException {
        try {
            LoginResult result = server.login(new LoginRequest(username, password));
            output = "logged in.";
            System.out.println("logged in.");
            loggedIn = true;
            authToken = result.authToken();
            parseInput();
        } catch (Exception e) {
            printBadOutput(e.getMessage());
        }
    }
    private static void listGames() throws CommunicationException, IOException {
        try {
            ListGamesResult response = server.listGames(authToken);
            for(GameData game: response.games()) {
                System.out.println("name: " + game.gameName() +
                        " | ID: " + game.gameID() +
                        " | White Username: " + game.whiteUsername() +
                        " | Black Username: " + game.blackUsername());
            }
            parseInput();
        } catch (Exception e) {
            printBadOutput(e.getMessage());
        }
    }
    private static void createGame(String gameName) throws CommunicationException, IOException {
        try {
            CreateGameResult result = server.createGame(new CreateGameRequest(gameName), authToken);
            System.out.println("Game " + "\"" + gameName + "\" #" + result.gameID() + " created");
            parseInput();
        } catch (Exception e) {
            printBadOutput(e.getMessage());
        }
    }
    private static void loguot() throws CommunicationException, IOException{
        try {
            server.logout(authToken);
            authToken = null;
            loggedIn = false;
            inGame = false;
            System.out.println("logged out.");
            parseInput();
        } catch (Exception e) {
            printBadOutput(e.getMessage());
        }
    }
    private static void join(int id, String colorStr) throws CommunicationException, IOException{
        gameID = id;
        if(colorStr.equals(null)){
            printBadOutput(" color is not black or white ");
        }
        else if(colorStr.equalsIgnoreCase("black")){
            teamColor = ChessGame.TeamColor.BLACK;
        }
        else if(colorStr.equalsIgnoreCase("white")){
            teamColor = ChessGame.TeamColor.WHITE;
        }
        else {
            printBadOutput(" color is not black or white ");
        }
        try {
            webSocketFacade = new WebSocketFacade("http://localhost:8080", authToken);
            webSocketFacade.joinPlayer(id, teamColor);
            server.join(new JoinGameRequest(colorStr.toLowerCase(), id), authToken);

        }
        catch (Exception e) {
            printBadOutput(e.getMessage());
        }
        //System.out.println(messageType);
        inGame = true;
        System.out.println("You have joined the game.");
        parseInput();
    }
    private static void join(int id) throws CommunicationException, IOException{
        try {
            gameID = id;
            inGame = true;
            teamColor = null;
            String color = null;
            webSocketFacade = new WebSocketFacade("http://localhost:8080", authToken);
            webSocketFacade.joinObserver(id);
            server.join(new JoinGameRequest(color, id), authToken);
            System.out.println("You are now observing the game");
            parseInput();
        } catch (Exception e) {
            printBadOutput(e.getMessage());
        }
    }
    private static void leave() throws CommunicationException, IOException {
        try {
            inGame = false;
            webSocketFacade.leave(gameID);
            System.out.println("You have left the game.");
            parseInput();
        } catch (Exception e) {
            printBadOutput(e.getMessage());
        }
    }
    private static void resign() throws CommunicationException, IOException {
        try {
            inGame = false;
            if(teamColor == null){
                System.out.println("You have left the game, but as an observer no one has resigned.");
            }
            else {
                System.out.println("You have left the game.");
            }
            webSocketFacade.resign(gameID);
            parseInput();
        } catch (Exception e) {
            printBadOutput(e.getMessage());
        }
    }
    private static void redraw() throws CommunicationException, IOException {
        try {
            BoardPrinter.printBoard(chessGame.getBoard(), teamColor);
            parseInput();
        } catch (Exception e) {
            printBadOutput(e.getMessage());
        }
    }
    private static void highlight(String startNum) throws CommunicationException, IOException {
        try {
            int[] startInts = checkStringToNum(startNum);
            int row = startInts[0];
            int col = startInts[1];
            parseInput();
        } catch (Exception e) {
            printBadOutput(e.getMessage());
        }
    }

    private static void move(String startNum, String endNum) throws CommunicationException, IOException {
        try {
            int[] startInts = checkStringToNum(startNum);
            int startRow = startInts[0];
            int startCol = startInts[1];
            int[] endInts = checkStringToNum(endNum);
            int endRow = endInts[0];
            int endCol = endInts[1];
            ChessPosition startPos = new ChessPosition(startRow, startCol);
            ChessPosition endPos = new ChessPosition(endRow, endCol);
            ChessMove move = new ChessMove(startPos, endPos, null);
            try {
                chessGame.makeMove(move);
            }
            catch (Exception e){
                printBadOutput(e.getMessage());
            }
            webSocketFacade.makeMove(gameID, move);
            redraw();
        } catch (Exception e) {
            printBadOutput(e.getMessage());
        }
    }

    private static int[] checkStringToNum(String startNum) throws IOException, CommunicationException {
        int row  = 0;
        int col = 0;
        if(Character.isDigit(startNum.charAt(0)) && Character.isDigit(startNum.charAt(1))
                && (startNum.length() == 2)){
            row = Character.getNumericValue(startNum.charAt(0));
            col = Character.getNumericValue(startNum.charAt(1));
        }
        else {
            printBadOutput("provide 2 numbers");
        }
        if(row < 1 || row > 8 || col < 1 || col > 8){
            printBadOutput("numbers not in range 1-8");
        }
        int[] result = new int[2];
        result[0] = row;
        result[1] = col;
        return result;
    }


}