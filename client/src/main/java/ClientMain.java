import chess.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import model.GameData;
import request.*;
import result.*;

import javax.naming.CommunicationException;


public class ClientMain {
    private static final ServerFacade server = new ServerFacade(3676);
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
                    join <ID> [WHITE|BLACK|<empty>] (for empty, simply type "[]" without a space)
                    observe<ID>
                    logout
                    quit
                    help""";
    private static boolean loggedIn = false;
    private static String authToken = null;

    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    public static void main(String[] args) throws IOException, CommunicationException {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece
                            + "\n" + INTRO + "\n");
        parseInput();
        reader.close();
    }
    private static void parseInput() throws IOException, CommunicationException {
        if(loggedIn){
            System.out.print("[LOGGED_IN] >> ");
        }
        else{
            System.out.print("[LOGGED_OUT] >> ");
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
                    if (wordList.size() != 3 || loggedIn == true) {
                        printBadOutput();
                    }
                    login(wordList.get(1), wordList.get(2));
                    break;
                case "register":
                    if (wordList.size() != 4 || loggedIn == true) {
                        printBadOutput();
                    }
                    register(wordList.get(1), wordList.get(2), wordList.get(3));
                    break;
                default:
                    printBadOutput();
            }
        }
        //postlogin
        else {
            switch (wordList.get(0).toLowerCase()) {
                case "help":
                    if (wordList.size() != 1) {
                        printBadOutput();
                    }
                    help();
                    break;
                case "list":
                    if (wordList.size() != 1 || loggedIn == false) {
                        printBadOutput();
                    }
                    listGames();
                    break;
                case "create":
                    if (wordList.size() != 2 || loggedIn == false) {
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
                    join(i, null);
                    break;
                case "logout":
                    if (wordList.size() != 1 || loggedIn == false) {
                        printBadOutput();
                    }
                    loguot();
                    break;
                case "join":
                    if (wordList.size() < 2 || wordList.size() > 3 || loggedIn == false) {
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
                    if(wordList.size() == 3){
                        color = wordList.get(2);
                    }
                    join(j, color);
                default:
                    printBadOutput();
            }
        }
    }
    private static void printBadOutput() throws IOException, CommunicationException {
        System.out.println("\nIncorrect or no input. " +
                "Please try again. " +
                "To view available commands, please type \"help\".");
        parseInput();
    }
    private static void help() throws IOException, CommunicationException {
        if(loggedIn){
            System.out.println(POSTLOGIN_UI);
        }
        else {
            System.out.println(PRELOGIN_UI);
        }
        parseInput();
    }
    private static void register(String username, String password, String email) throws IOException, CommunicationException {
        //RegisterResult result =
        server.register(new RegisterRequest(username, password, email));
        System.out.println("user registered.");
        login(username, password);
    }
    private static void login(String username, String password) throws IOException, CommunicationException {
        LoginResult result = server.login(new LoginRequest(username, password));
        System.out.println("logged in.");
        loggedIn = true;
        authToken = result.authToken();
        parseInput();
    }
    private static void listGames() throws CommunicationException, IOException {
        ListGamesResult response = server.listGames(authToken);
        for(GameData game: response.games()) {
            System.out.println("name: " + game.gameName() + " | ID: " +
                    game.gameID() + " | White Username: " +
                    game.whiteUsername() + " | Black Username: " +
                    game.blackUsername());
        }
        parseInput();
    }
    private static void createGame(String gameName) throws CommunicationException, IOException {
        CreateGameResult result = server.createGame(new CreateGameRequest(gameName), authToken);
        System.out.println("Game " + "\"" + gameName + "\" #" + result.gameID() + " created");
        parseInput();
    }
    private static void loguot() throws CommunicationException, IOException{
        server.logout(authToken);
        authToken = null;
        loggedIn = false;
        System.out.println("logged out.");
        parseInput();
    }
    private static void join(int ID, String colorStr) throws CommunicationException, IOException{
        String color;
        if(colorStr != null){
            color = colorStr.toUpperCase();
        }
        else color = null;
        server.join(new JoinGameRequest(color, ID), authToken);
        //print board
        parseInput();
    }
}