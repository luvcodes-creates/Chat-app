import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.*;

/**
 * ChatServer: Main server application that accepts client connections
 * and manages multi-client communication with 10-client maximum capacity
 */
public class ChatServer {
    private static final int PORT = 6001;
    private static final int MAX_CLIENTS = 10;
    
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private ConcurrentHashMap<String, ClientHandler> connectedClients;
    
    public ChatServer() {
        this.threadPool = Executors.newFixedThreadPool(MAX_CLIENTS);
        this.connectedClients = new ConcurrentHashMap<>();
        // Ensure some test users exist in the MySQL-backed UserDB (no-ops if already present)
        try {
            UserDB.addUser("alice", "alice123");
            UserDB.addUser("bob", "bob123");
            UserDB.addUser("charlie", "charlie123");
        } catch (Exception ignored) {}
    }
    
    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("✓ Chat Server started on port " + PORT);
            System.out.println("✓ Maximum clients: " + MAX_CLIENTS);
            System.out.println("✓ Waiting for client connections...\n");
            
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("[CONNECTION] New connection from " + clientSocket.getInetAddress());
                    
                    if (connectedClients.size() >= MAX_CLIENTS) {
                        System.out.println("[ERROR] Server full (max " + MAX_CLIENTS + " clients)");
                        clientSocket.close();
                        continue;
                    }
                    
                    ClientHandler handler = new ClientHandler(clientSocket, connectedClients);
                    threadPool.submit(handler);
                    
                } catch (IOException e) {
                    System.err.println("[ERROR] Error accepting connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Cannot start server on port " + PORT + ": " + e.getMessage());
        } finally {
            shutdown();
        }
    }
    
    private void shutdown() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            threadPool.shutdown();
            System.out.println("\n[SERVER] Shutting down...");
        } catch (IOException e) {
            System.err.println("[ERROR] Error during shutdown: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.start();
    }
}
