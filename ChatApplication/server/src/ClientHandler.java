import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * ClientHandler: Handles individual client connections in separate threads
 * Manages authentication, message receiving, and broadcasting
 */
public class ClientHandler implements Runnable {
    private Socket socket;
    private DataInputStream din;
    private DataOutputStream dout;
    private String username;
    private ConcurrentHashMap<String, ClientHandler> connectedClients;
    
    public ClientHandler(Socket socket, ConcurrentHashMap<String, ClientHandler> connectedClients) throws IOException {
        this.socket = socket;
        this.connectedClients = connectedClients;
        this.din = new DataInputStream(socket.getInputStream());
        this.dout = new DataOutputStream(socket.getOutputStream());
    }
    
    @Override
    public void run() {
        try {
            // Authentication phase
            if (!authenticate()) {
                return;
            }
            
            // Register client
            connectedClients.put(username, this);
            System.out.println("[LOGIN] Client logged in: " + username);
            broadcastMessage(username + " joined the chat");
            
            // Message receiving loop
            while (true) {
                String message = din.readUTF();
                
                if (message.equalsIgnoreCase("QUIT")) {
                    break;
                }
                
                // Handle history request
                if (message.equalsIgnoreCase("GET_HISTORY")) {
                    sendMessageHistory();
                    continue;
                }
                
                String logEntry = formatTimestamp() + " " + username + ": " + message;
                System.out.println(logEntry);
                
                // Save message to database
                int userId = MessageDB.getUserId(username);
                if (userId > 0) {
                    MessageDB.saveMessage(userId, message);
                }
                
                // Broadcast to all connected clients
                broadcastMessage(logEntry);
            }
            
        } catch (EOFException eof) {
            System.out.println("[DISCONNECT] Client " + username + " disconnected (EOF)");
        } catch (java.net.SocketException se) {
            System.out.println("[DISCONNECT] Client " + username + " connection reset");
        } catch (Exception e) {
            System.err.println("[ERROR] Error handling client " + username + ": " + e.getMessage());
        } finally {
            cleanup();
        }
    }
    
    /**
     * Authenticate client with LOGIN or REGISTER command
     * Uses the MySQL-backed UserDB for persistent user storage.
     */
    private boolean authenticate() throws IOException {
        while (true) {
            try {
                String msg = din.readUTF();
                String[] parts = msg.split("\\|", 3);

                if (parts.length < 1) {
                    dout.writeUTF("ERROR|Invalid command");
                    continue;
                }

                String cmd = parts[0].toUpperCase();

                if ("REGISTER".equals(cmd)) {
                    if (parts.length < 3) {
                        dout.writeUTF("REGISTER_FAIL|Missing username or password");
                        continue;
                    }
                    String user = parts[1];
                    String pass = parts[2];
                    if (user.isEmpty() || pass.isEmpty()) {
                        dout.writeUTF("REGISTER_FAIL|Username or password cannot be empty");
                    } else if (UserDB.userExists(user)) {
                        dout.writeUTF("REGISTER_FAIL|Username already taken");
                    } else {
                        try {
                            UserDB.addUser(user, pass);
                            this.username = user;
                            dout.writeUTF("REGISTER_OK");
                            System.out.println("[REGISTER] New user registered: " + user);
                            return true;
                        } catch (IOException e) {
                            System.err.println("[ERROR] Failed to register user " + user + ": " + e.getMessage());
                            dout.writeUTF("REGISTER_FAIL|Internal server error");
                        }
                    }
                } else if ("LOGIN".equals(cmd)) {
                    if (parts.length < 3) {
                        dout.writeUTF("LOGIN_FAIL|Missing username or password");
                        continue;
                    }
                    String user = parts[1];
                    String pass = parts[2];
                    if (!UserDB.userExists(user)) {
                        dout.writeUTF("LOGIN_FAIL|User does not exist");
                    } else if (!UserDB.validateUser(user, pass)) {
                        dout.writeUTF("LOGIN_FAIL|Invalid password");
                    } else if (connectedClients.containsKey(user)) {
                        dout.writeUTF("LOGIN_FAIL|User already logged in");
                    } else {
                        this.username = user;
                        dout.writeUTF("LOGIN_OK");
                        System.out.println("[LOGIN] User authenticated: " + user);
                        return true;
                    }
                } else {
                    dout.writeUTF("ERROR|Unknown command. Use REGISTER or LOGIN");
                }
            } catch (EOFException eof) {
                return false;
            }
        }
    }
    
    /**
     * Send message to this specific client
     */
    public synchronized void sendMessage(String message) throws IOException {
        if (dout != null) {
            dout.writeUTF(message);
            dout.flush();
        }
    }
    
    /**
     * Broadcast message to all connected clients
     */
    private synchronized void broadcastMessage(String message) {
        for (ClientHandler client : connectedClients.values()) {
            try {
                client.sendMessage(message);
            } catch (IOException e) {
                System.err.println("[ERROR] Failed to send message to " + client.username);
            }
        }
    }
    
    /**
     * Format timestamp for messages
     */
    private String formatTimestamp() {
        return "[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "]";
    }
    
    /**
     * Send message history to client
     */
    private void sendMessageHistory() {
        try {
            sendMessage("HISTORY_START");
            List<String> messages = MessageDB.getRecentMessages(50); // Last 50 messages
            for (String msg : messages) {
                sendMessage("HISTORY:" + msg);
            }
            sendMessage("HISTORY_END");
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to send message history: " + e.getMessage());
        }
    }
    
    /**
     * Cleanup on disconnect
     */
    private void cleanup() {
        try {
            if (username != null) {
                connectedClients.remove(username);
                broadcastMessage(username + " left the chat");
                System.out.println("[LOGOUT] Client removed: " + username);
            }
            
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Error during cleanup: " + e.getMessage());
        }
    }
}
