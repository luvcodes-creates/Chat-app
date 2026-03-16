package client.src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Simple console chat client.
 * Connects to the chat server, reads from console, and prints messages from server.
 */
public class ChatClient {

    public static final String HOST = "localhost";
    public static final int PORT = 5000;

    public static void main(String[] args) {
        System.out.println("Connecting to chat server " + HOST + ":" + PORT + " ...");

        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in))) {

            // Thread to read messages from server
            Thread readerThread = new Thread(() -> {
                try {
                    String line;
                    while ((line = serverIn.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            });
            readerThread.setDaemon(true);
            readerThread.start();

            // Main loop: send user input to server
            String input;
            while ((input = userIn.readLine()) != null) {
                serverOut.println(input);
                if ("exit".equalsIgnoreCase(input.trim())) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Unable to connect to server: " + e.getMessage());
        }
    }
}


