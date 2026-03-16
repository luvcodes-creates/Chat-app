package server.src;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Main chat server entry point.
 * - Listens for incoming TCP connections.
 * - Limits number of connected clients to MAX_CLIENTS.
 * - For every accepted client a ClientHandler thread is started.
 */
public class ChatServer {

    public static final int PORT = 5000;
    public static final int MAX_CLIENTS = 10;

    // Thread-safe set of handlers for broadcasting messages.
    private static final Set<ClientHandler> clientHandlers =
            Collections.synchronizedSet(new HashSet<>());

    private static DatabaseManager databaseManager;

    public static void main(String[] args) {
        System.out.println("Starting Chat Server on port " + PORT + " ...");

        // Initialize database manager (SQLite by default)
        databaseManager = new DatabaseManager("jdbc:sqlite:chat.db");
        databaseManager.init();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();

                if (clientHandlers.size() >= MAX_CLIENTS) {
                    System.out.println("Connection refused: maximum clients reached.");
                    clientSocket.close();
                    continue;
                }

                ClientHandler handler = new ClientHandler(clientSocket, clientHandlers, databaseManager);
                clientHandlers.add(handler);
                handler.start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (databaseManager != null) {
                databaseManager.close();
            }
        }
    }
}


