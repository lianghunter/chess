import chess.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
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
    private static boolean scanning = true;

    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    public static void main(String[] args) throws IOException {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece
                            + "\n" + INTRO + "\n");
        parseInput();
        reader.close();
    }
    private static void parseInput() throws IOException {
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
                break;
            case "login":
                if (wordList.size() != 3 || loggedIn == true) {
                    printBadOutput();
                }
            case "register":
                if (wordList.size() != 4 || loggedIn == true) {
                    printBadOutput();
                }

            case "list":
                if (wordList.size() != 1 || loggedIn == false) {
                    printBadOutput();
                }
            case "create":
                if (wordList.size() != 2 || loggedIn == false) {
                    printBadOutput();
                }
            case "observe":
                if (wordList.size() != 2 || loggedIn == false) {
                    printBadOutput();
                }
            case "logout":
                if (wordList.size() != 1 || loggedIn == false) {
                    printBadOutput();
                }
            case "join":
                if (wordList.size() != 3 || loggedIn == false) {
                    printBadOutput();
                }
            default:
                printBadOutput();
        }
    }
    private static void printBadOutput() throws IOException {
        System.out.println("\nIncorrect or no input. " +
                "Please try again. " +
                "To view available commands, please type \"help\".");
        parseInput();
    }
    private static void help() throws IOException {
        if(loggedIn){
            System.out.println(POSTLOGIN_UI);
        }
        else {
            System.out.println(PRELOGIN_UI);
        }
        parseInput();
    }
    private static void register(String username, String password, String email) throws IOException, CommunicationException {
        RegisterResult result = server.register(new RegisterRequest(username, password, email));
        loggedIn = true;
    }
    private static void login(String username, String password) throws IOException{

    }
}