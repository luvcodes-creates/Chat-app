import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * UserDB
 *
 * MySQL-backed user database used by the chat server.
 * Data is stored in the `users` table defined by your schema.sql:
 *
 *   users(user_id, username UNIQUE, password_hash, password_salt, ...)
 *
 * Passwords are never stored in plain text; instead we store:
 *   - a random 16‑byte salt (hex‑encoded)
 *   - SHA‑256(salt + password) as password_hash (hex‑encoded)
 */
public class UserDB {

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Add a new user to the DB.
     * If the user already exists, this is a no‑op (no error is thrown).
     */
    public static synchronized void addUser(String username, String password) {
        if (userExists(username)) {
            // Already present, nothing to do
            return;
        }

        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        byte[] hash = hashPassword(salt, password);

        String saltHex = toHex(salt);
        String hashHex = toHex(hash);

        String sql = "INSERT INTO users (username, password_hash, password_salt) VALUES (?, ?, ?)";

        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ensureUsersTable(c);
            ps.setString(1, username);
            ps.setString(2, hashHex);
            ps.setString(3, saltHex);
            ps.executeUpdate();
        } catch (SQLException e) {
            // Silently ignore "UNIQUE constraint failed" in case of race; log others.
            if (!String.valueOf(e.getMessage()).toLowerCase().contains("unique")) {
                System.err.println("[UserDB] Failed to add user '" + username + "': " + e.getMessage());
            }
        }
    }

    /**
     * Check if the given username already exists.
     */
    public static synchronized boolean userExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ? LIMIT 1";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ensureUsersTable(c);
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("[UserDB] Failed to check user existence: " + e.getMessage());
            return false;
        }
    }

    /**
     * Validate a username/password combination against the DB.
     */
    public static synchronized boolean validateUser(String username, String password) {
        String sql = "SELECT password_hash, password_salt FROM users WHERE username = ?";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ensureUsersTable(c);
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }
                String hashHex = rs.getString("password_hash");
                String saltHex = rs.getString("password_salt");

                byte[] salt = fromHex(saltHex);
                byte[] expectedHash = fromHex(hashHex);
                byte[] actualHash = hashPassword(salt, password);

                return MessageDigest.isEqual(expectedHash, actualHash);
            }
        } catch (SQLException e) {
            System.err.println("[UserDB] Failed to validate user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Ensure the `users` table exists (MySQL-compatible schema).
     */
    private static void ensureUsersTable(Connection c) throws SQLException {
        String ddl = "CREATE TABLE IF NOT EXISTS users (" +
                "user_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) NOT NULL UNIQUE, " +
                "password_hash VARCHAR(255) NOT NULL, " +
                "password_salt VARCHAR(255) NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "last_login TIMESTAMP NULL" +
                ")";
        try (PreparedStatement ps = c.prepareStatement(ddl)) {
            ps.executeUpdate();
        }
    }

    /**
     * Hash password using SHA‑256(salt + password).
     */
    private static byte[] hashPassword(byte[] salt, String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            digest.update(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return digest.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private static String toHex(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static byte[] fromHex(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}
