import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MessageDB: Handles message storage and retrieval from MySQL database
 */
public class MessageDB {
    
    /**
     * Save a message to the database
     */
    public static synchronized void saveMessage(int senderId, String messageText) {
        String sql = "INSERT INTO messages (sender_id, message_text) VALUES (?, ?)";
        
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ensureMessagesTable(c);
            ps.setInt(1, senderId);
            ps.setString(2, messageText);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[MessageDB] Failed to save message: " + e.getMessage());
        }
    }
    
    /**
     * Get user ID by username
     */
    public static synchronized int getUserId(String username) {
        String sql = "SELECT user_id FROM users WHERE username = ?";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            System.err.println("[MessageDB] Failed to get user ID: " + e.getMessage());
        }
        return -1;
    }
    
    /**
     * Get recent messages (last N messages)
     */
    public static synchronized List<String> getRecentMessages(int limit) {
        List<String> messages = new ArrayList<>();
        String sql = "SELECT m.message_text, m.sent_at, u.username " +
                     "FROM messages m " +
                     "JOIN users u ON m.sender_id = u.user_id " +
                     "ORDER BY m.sent_at DESC LIMIT ?";
        
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ensureMessagesTable(c);
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                List<String> temp = new ArrayList<>();
                while (rs.next()) {
                    String timestamp = new java.text.SimpleDateFormat("HH:mm:ss")
                        .format(rs.getTimestamp("sent_at"));
                    String username = rs.getString("username");
                    String messageText = rs.getString("message_text");
                    temp.add("[" + timestamp + "] " + username + ": " + messageText);
                }
                // Reverse to show oldest first
                for (int i = temp.size() - 1; i >= 0; i--) {
                    messages.add(temp.get(i));
                }
            }
        } catch (SQLException e) {
            System.err.println("[MessageDB] Failed to get recent messages: " + e.getMessage());
        }
        return messages;
    }
    
    /**
     * Ensure the messages table exists
     */
    private static void ensureMessagesTable(Connection c) throws SQLException {
        String ddl = "CREATE TABLE IF NOT EXISTS messages (" +
                "message_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "sender_id INT NOT NULL, " +
                "message_text TEXT NOT NULL, " +
                "sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (sender_id) REFERENCES users(user_id) ON DELETE CASCADE" +
                ")";
        try (PreparedStatement ps = c.prepareStatement(ddl)) {
            ps.executeUpdate();
        }
    }
}
