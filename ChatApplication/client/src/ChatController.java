import java.io.*;
import java.net.Socket;

/**
 * ChatController: Manages client-server communication
 */
public class ChatController {
    private String host;
    private int port;
    private String username;
    private String password;
    private boolean isLogin;
    private Socket socket;
    private DataInputStream din;
    private DataOutputStream dout;
    private ChatClient chatClient;
    
    public ChatController(String host, int port, String username, String password, boolean isLogin, ChatClient chatClient) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.isLogin = isLogin;
        this.chatClient = chatClient;
    }
    
    public boolean connect() {
        try {
            socket = new Socket(host, port);
            din = new DataInputStream(socket.getInputStream());
            dout = new DataOutputStream(socket.getOutputStream());
            
            // Send authentication command
            if (isLogin) {
                dout.writeUTF("LOGIN|" + username + "|" + password);
            } else {
                dout.writeUTF("REGISTER|" + username + "|" + password);
            }
            dout.flush();
            
            // Receive response
            String response = din.readUTF();
            
            if (response.startsWith("LOGIN_OK") || response.startsWith("REGISTER_OK")) {
                chatClient.appendMessage("✓ Connected to server");
                return true;
            } else {
                chatClient.appendMessage("✗ " + response);
                return false;
            }
        } catch (IOException e) {
            chatClient.appendMessage("Connection error: " + e.getMessage());
            return false;
        }
    }
    
    public void sendMessage(String message) throws IOException {
        if (dout != null) {
            dout.writeUTF(message);
            dout.flush();
        }
    }
    
    public void receiveMessages() {
        try {
            while (true) {
                String message = din.readUTF();
                
                // Check if it's message history
                if (message.startsWith("HISTORY_START")) {
                    // Start receiving history
                    continue;
                } else if (message.startsWith("HISTORY_END")) {
                    // End of history
                    continue;
                } else if (message.startsWith("HISTORY:")) {
                    // History message
                    String historyMsg = message.substring(8); // Remove "HISTORY:" prefix
                    chatClient.appendMessage(historyMsg);
                    continue;
                }
                
                // Regular message - parse formatting
                String formattedMessage = parseFormatting(message);
                chatClient.appendMessage(formattedMessage);
            }
        } catch (EOFException eof) {
            chatClient.appendMessage("\n✗ Connection closed by server");
        } catch (IOException e) {
            chatClient.appendMessage("\n✗ Connection error: " + e.getMessage());
        }
    }
    
    public void requestMessageHistory() {
        try {
            if (dout != null) {
                dout.writeUTF("GET_HISTORY");
                dout.flush();
            }
        } catch (IOException e) {
            System.err.println("Error requesting history: " + e.getMessage());
        }
    }
    
    private String parseFormatting(String message) {
        // Formatting is handled in ChatClient's parseAndInsertFormattedText
        // Just return the message as-is
        return message;
    }
    
    public void disconnect() {
        try {
            if (dout != null) {
                dout.writeUTF("QUIT");
                dout.flush();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
