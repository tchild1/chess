import adapters.ChessBoardAdapter;
import adapters.ChessGameAdapter;
import adapters.ChessPieceAdapter;
import adapters.ListGamesAdapter;
import chess.Board;
import chess.ChessGame;
import chess.ChessPiece;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.Game;
import requests.*;
import responses.ListGamesResponse;
import responses.LoginResponse;
import responses.RegisterUserResponse;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class client {

    static String tokenString = null;

    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.print("Welcome to Chess! Type Help to see commands.\n\n");

        boolean cont = true;
        while (cont) {
            if (!isAuthenticated()) {
                System.out.print("[LOGGED OUT] Enter a command or 'help' to see command options >>> ");
            } else {
                System.out.print("[LOGGED IN] Enter a command or 'help' to see command options >>> ");
            }

            String userInput = getUserInput().nextLine();
            cont = handleUserInput(userInput);
        }
    }

    private static void printChessboardForWhite(Board b) {
        int numRow = 1;
        printBorderBackwards();
        for (int row=b.board.length-1;row>=0;row--) {
            System.out.print("\u001b[30;100m " + numRow + " \u001b[0m");
            for (int col=b.board.length-1;col>=0;col--) {
                if (((row + col) % 2) == 0) {
                    System.out.printf("\u001b[107m%s\u001b[0m", getPieceRepresentation(b.board[row][col]));
                } else {
                    System.out.printf("\u001b[40m%s\u001b[0m", getPieceRepresentation(b.board[row][col]));
                }
            }
            System.out.print("\u001b[30;100m " + numRow + " \u001b[0m");
            System.out.print("\n");
            numRow++;
        }
        printBorderBackwards();
    }

    private static void printChessboardForBlack(Board b) {
        int numRow = 8;
        printBorderInOrder();
        for (int row=0;row<b.board.length;row++) {
            System.out.print("\u001b[30;100m " + numRow + " \u001b[0m");
            for (int col=0;col<b.board[row].length;col++) {
                if (((row + col) % 2) == 0) {
                    System.out.printf("\u001b[107m%s\u001b[0m", getPieceRepresentation(b.board[row][col]));
                } else {
                    System.out.printf("\u001b[40m%s\u001b[0m", getPieceRepresentation(b.board[row][col]));
                }
            }
            System.out.print("\u001b[30;100m " + numRow + " \u001b[0m");
            System.out.print("\n");
            numRow--;
        }
        printBorderInOrder();
    }

    private static void printBorderBackwards() {
        System.out.print("\u001b[30;100m   \u001b[0m");
        System.out.print("\u001b[30;100m h \u001b[0m");
        System.out.print("\u001b[30;100m g \u001b[0m");
        System.out.print("\u001b[30;100m f \u001b[0m");
        System.out.print("\u001b[30;100m e \u001b[0m");
        System.out.print("\u001b[30;100m d \u001b[0m");
        System.out.print("\u001b[30;100m c \u001b[0m");
        System.out.print("\u001b[30;100m b \u001b[0m");
        System.out.print("\u001b[30;100m a \u001b[0m");
        System.out.print("\u001b[30;100m   \u001b[0m");
        System.out.print("\n");
    }

    private static void printBorderInOrder() {
        System.out.print("\u001b[30;100m   \u001b[0m");
        System.out.print("\u001b[30;100m a \u001b[0m");
        System.out.print("\u001b[30;100m b \u001b[0m");
        System.out.print("\u001b[30;100m c \u001b[0m");
        System.out.print("\u001b[30;100m d \u001b[0m");
        System.out.print("\u001b[30;100m e \u001b[0m");
        System.out.print("\u001b[30;100m f \u001b[0m");
        System.out.print("\u001b[30;100m g \u001b[0m");
        System.out.print("\u001b[30;100m h \u001b[0m");
        System.out.print("\u001b[30;100m   \u001b[0m");
        System.out.print("\n");
    }

    private static String getPieceRepresentation(ChessPiece piece) {
        if (piece == null) {
            return "\u001b[34m   \u001b[0m";
        }

        String pieceRepresentation1;
        String pieceRepresentation3;
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            pieceRepresentation1 = "\u001b[34m ";
        } else {
            pieceRepresentation1 = "\u001b[31m ";
        }
        pieceRepresentation3 = " \u001b[0m";


        String pieceRepresentation2;
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            pieceRepresentation2 = "K";
        } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            pieceRepresentation2 = "Q";
        } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            pieceRepresentation2 = "P";
        } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            pieceRepresentation2 = "R";
        } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            pieceRepresentation2 = "N";
        } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            pieceRepresentation2 = "B";
        } else {
            pieceRepresentation2 = " ";
        }

        return pieceRepresentation1 + pieceRepresentation2 + pieceRepresentation3;
    }

    private static Scanner getUserInput() {
        return new Scanner(System.in);
    }

    private static boolean isAuthenticated() {
        return tokenString != null;
    }

    private static boolean handleUserInput(String input) throws IOException, InterruptedException {
        if (Objects.equals(input, "register")) {
            if (!isAuthenticated()) {
                userEnteredRegister();
            } else {
                System.out.println("Cannot register a user while logged in.");
            }
        } else if (Objects.equals(input, "login")) {
            if (isAuthenticated()) {
                System.out.println("Cannot log in while logged in.");
            } else {
                userEnteredLogin();
            }
        } else if (Objects.equals(input, "quit")) {
            System.out.println("Thanks for playing!");
            return false;
        } else if (Objects.equals(input, "help")) {
            userEnteredHelp();
        } else if (Objects.equals(input, "logout")) {
            if (isAuthenticated()) {
                userEnteredLogout();
            } else {
                System.out.println("Cannot log out without being logged in.");
            }
        } else if (Objects.equals(input, "create")) {
            if (isAuthenticated()) {
                userEnteredCreateGame();
            } else {
                System.out.println("Must be logged in to create a game.");
            }
        } else if (Objects.equals(input, "list")) {
            if (isAuthenticated()) {
                userEnteredListGames();
            } else {
                System.out.println("Must be logged in to list games.");
            }
        } else if (Objects.equals(input, "join")) {
            if (isAuthenticated()) {
                userEnteredJoin();
            } else {
                System.out.println("Must be logged in to join a game.");
            }
        } else if (Objects.equals(input, "clear")) {
            userEnteredClear();
        } else {
            System.out.println("Command not recognized. Please enter a valid command.");
        }
        return true;
    }

    private static void userEnteredClear() throws IOException, InterruptedException {
        HttpResponse<String> response = makeRequest("/db", "DELETE", new ClearApplicationRequest());

        if (response.statusCode() == 200) {
            System.out.println("Database cleared.");
        } else {
            System.out.println("Error occurred clearing the database. ");
        }
    }

    private static void userEnteredJoin() throws IOException, InterruptedException {
        userEnteredListGames();

        System.out.println("Please enter the ID of the game you would like to join: ");
        String gameToJoin = getUserInput().next();

        System.out.println("What color would you like to play? [W/B/O])");
        String colorToPlay = getUserInput().next();

        ChessGame.TeamColor requestColor;
        if (Objects.equals(colorToPlay, "W")) {
            requestColor = ChessGame.TeamColor.WHITE;
        } else if (Objects.equals(colorToPlay, "B")) {
            requestColor = ChessGame.TeamColor.BLACK;
        } else {
            requestColor = null;
        }

        HttpResponse<String> response = makeRequest("/game", "PUT", new JoinGameRequest(tokenString, requestColor, gameToJoin));

        if (response.statusCode() == 200) {
            System.out.println("Added to game " + gameToJoin + " Successfully");
            System.out.print("\n");

            Board board = new Board();
            board.resetBoard();
            printChessboardForBlack(board);
            System.out.print("\n");
            System.out.print("\n");
            printChessboardForWhite(board);
        } else {
            System.out.println("Failed to add user to game.");
        }
    }

    private static void userEnteredListGames() throws IOException, InterruptedException {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(models.Game.class, new ChessGameAdapter());
        builder.registerTypeAdapter(chess.Board.class, new ChessBoardAdapter());
        builder.registerTypeAdapter(chess.Piece.class, new ChessPieceAdapter());
        builder.registerTypeAdapter(responses.ListGamesResponse.class, new ListGamesAdapter());

        HttpResponse<String> response = makeRequest("/game", "GET", new ListGamesRequest(tokenString));

        if (response.statusCode() != 200) {
            System.out.println("Problem occurred when listing games.");
        } else {
            ListGamesResponse games = builder.create().fromJson(response.body(), ListGamesResponse.class);
            ArrayList<Game> allGames = games.getGames();

            System.out.println("All Games: ");
            for (Game currGame : allGames) {
                System.out.println("    Game ID: " + currGame.getGameID());
                System.out.println("    Game Name: " + currGame.getGameName());
                if (currGame.getWhiteUsername() != null) {
                    System.out.println("    White Player: " + currGame.getWhiteUsername());
                } else {
                    System.out.println("    White Player: [NONE]");
                }
                if (currGame.getBlackUsername() != null) {
                    System.out.println("    Black Player: " + currGame.getBlackUsername());
                } else {
                    System.out.println("    Black Player: [NONE]");
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    private static void userEnteredCreateGame() throws IOException, InterruptedException {
        System.out.println("Please enter the game's name: ");
        String gameName = getUserInput().next();
        HttpResponse<String> response = makeRequest("/game", "POST", new CreateGameRequest(tokenString, gameName));

        if (response.statusCode() != 200) {
            System.out.println("Error occurred when creating game. ");
        } else {
            System.out.println("Game created. ");
        }
    }

    private static void userEnteredLogout() throws IOException, InterruptedException {
        HttpResponse<String> response = makeRequest("/session", "DELETE", null);

        if (response.statusCode() == 200) {
            tokenString = null;
        } else {
            System.out.println("There was an error logging out.");
        }
    }

    private static void userEnteredRegister() throws IOException, InterruptedException {
        System.out.println("Please enter your username: ");
        String username = getUserInput().nextLine();

        System.out.println("Please enter your password: ");
        String password = getUserInput().nextLine();

        System.out.println("Please enter your email: ");
        String email = getUserInput().nextLine();

        HttpResponse<String> response = makeRequest("/user", "POST", new RegisterUserRequest(username, password, email));

        if (response.statusCode() == 200) {
            RegisterUserResponse registerUserResponse = new Gson().fromJson(response.body(), RegisterUserResponse.class);
            tokenString = registerUserResponse.getAuthToken();
        } else {
            System.out.println("There was an error registering user.");
        }
    }

    private static void userEnteredLogin() throws IOException, InterruptedException {
        System.out.println("Please enter your username: ");
        String username = getUserInput().nextLine();

        System.out.println("Please enter your password: ");
        String password = getUserInput().nextLine();

        HttpResponse<String> response = makeRequest("/session", "POST", new LoginRequest(username, password));

        if (response.statusCode() == 200) {
            LoginResponse loginResponse = new Gson().fromJson(response.body(), LoginResponse.class);
            tokenString = loginResponse.getToken();
        } else {
            System.out.println("There was an error logging in. ");
        }
    }

    private static HttpResponse<String> makeRequest(String route, String method, Request body) throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = null;
        if (Objects.equals(method, "POST")) {
            if (!isAuthenticated()) {
                request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080"+route))
                        .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(body)))
                        .build();
            } else {
                request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080"+route))
                        .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(body)))
                        .header("Authorization", tokenString)
                        .build();
            }
        } else if (Objects.equals(method, "DELETE")) {
            if (isAuthenticated()) {
                request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080"+route))
                        .DELETE()
                        .header("Authorization", tokenString)
                        .build();
            } else {
                request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080"+route))
                        .DELETE()
                        .build();
            }

        } else if (Objects.equals(method, "GET")) {
            request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080"+route))
                    .GET()
                    .header("Authorization", tokenString)
                    .build();
        } else if (Objects.equals(method, "PUT")) {
            request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080"+route))
                    .method("PUT", HttpRequest.BodyPublishers.ofString(new Gson().toJson(body)))
                    .header("Authorization", tokenString)
                    .build();
        }

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static void userEnteredHelp() {
        if (!isAuthenticated()) {
            System.out.println("\nOptions:");
            System.out.println("register - to create an account");
            System.out.println("login - to play chess");
            System.out.println("quit - stop playing chess");
            System.out.println("help - with possible commands");
            System.out.println();
        } else {
            System.out.println("\nOptions:");
            System.out.println("create - a game");
            System.out.println("list - games");
            System.out.println("join - a game");
            System.out.println("logout - when you are done");
            System.out.println("quit - playing chess");
            System.out.println("help - with possible commands");
            System.out.println();
        }
    }
}
