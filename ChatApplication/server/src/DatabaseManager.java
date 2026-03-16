package server.src;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

/**
 * Simple JDBC helper for storing chat messages.
 *
 * Uses SQLite by default with URL like: jdbc:sqlite:chat.db
 */
public class DatabaseManager {

    private final String jdbcUrl;

    public DatabaseManager(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public void init() {
        try (Connection conn = DriverManager.getConnection(jdbcUrl);
             Statement stmt = conn.createStatement()) {

            String createUsers = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE NOT NULL" +
                    ")";

            String createMessages = "CREATE TABLE IF NOT EXISTS messages (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER NOT NULL," +
                    "content TEXT NOT NULL," +
                    "timestamp TEXT NOT NULL," +
                    "FOREIGN KEY(user_id) REFERENCES users(id)" +
                    ")";

            stmt.execute(createUsers);
            stmt.execute(createMessages);
        } catch (SQLException e) {
            System.err.println("DB init error: " + e.getMessage());
        }
    }

    public void saveMessage(String username, String content, LocalDateTime time) {
        String upsertUser = "INSERT INTO users (username) VALUES (?) " +
                "ON CONFLICT(username) DO NOTHING";

        String insertMessage = "INSERT INTO messages (user_id, content, timestamp) " +
                "VALUES ((SELECT id FROM users WHERE username = ?), ?, ?)";

        try (Connection conn = DriverManager.getConnection(jdbcUrl)) {
            conn.setAutoCommit(false);

            try (PreparedStatement userStmt = conn.prepareStatement(upsertUser);
                 PreparedStatement msgStmt = conn.prepareStatement(insertMessage)) {

                userStmt.setString(1, username);
                userStmt.executeUpdate();

                msgStmt.setString(1, username);
                msgStmt.setString(2, content);
                msgStmt.setString(3, time.toString());
                msgStmt.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("DB save error: " + e.getMessage());
        }
    }

    public void close() {
        // For SQLite with per-call connections there is nothing global to close.
    }
}


