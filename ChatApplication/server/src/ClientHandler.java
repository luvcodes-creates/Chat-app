package server.src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Handles communication with a single client.
 * Listens for messages, saves them to DB, and broadcasts to all clients.
 */
public class ClientHandler extends Thread {

    private final Socket socket;
    private final Set<ClientHandler> clientHandlers;
    private final DatabaseManager databaseManager;

    private PrintWriter out;
    private BufferedReader in;
    private String username;

    public ClientHandler(Socket socket, Set<ClientHandler> clientHandlers, DatabaseManager databaseManager) {
        this.socket = socket;
        this.clientHandlers = clientHandlers;
        this.databaseManager = databaseManager;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("Enter your username:");
            username = in.readLine();
            if (username == null || username.trim().isEmpty()) {
                username = "User" + this.getId();
            }
            out.println("Welcome, " + username + "! Type 'exit' to leave.");

            broadcast("SERVER", username + " joined the chat.");

            String line;
            while ((line = in.readLine()) != null) {
                if ("exit".equalsIgnoreCase(line.trim())) {
                    break;
                }

                // Save message to DB
                databaseManager.saveMessage(username, line, LocalDateTime.now());

                // Broadcast message
                broadcast(username, line);
            }
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
            clientHandlers.remove(this);
            broadcast("SERVER", username + " left the chat.");
        }
    }

    private void broadcast(String sender, String message) {
        String formatted = "[" + sender + "]: " + message;
        synchronized (clientHandlers) {
            for (ClientHandler handler : clientHandlers) {
                handler.send(formatted);
            }
        }
        System.out.println(formatted);
    }

    private void send(String message) {
        if (out != null) {
            out.println(message);
        }
    }
}


