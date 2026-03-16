import java.sql.*;

/**
 * DatabaseManager for MySQL/XAMPP integration
 */
public class DatabaseManager {
    // MySQL connection details for XAMPP (default settings)
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "chatapp";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";  // Default XAMPP MySQL password is empty
    
    private static final String JDBC_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + "?useSSL=false&allowPublicKeyRetrieval=true";

    static {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[DB] MySQL JDBC Driver not found. Make sure mysql-connector-java is in your classpath.");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Initialize database and create tables if they don't exist
     */
    public static void initialize() {
        try (Connection c = getConnection()) {
            System.out.println("[DB] Successfully connected to MySQL database: " + DB_NAME);
        } catch (SQLException e) {
            System.err.println("[DB] Initialization failed: " + e.getMessage());
            System.err.println("[DB] Make sure MySQL is running in XAMPP and the database '" + DB_NAME + "' exists.");
            e.printStackTrace();
        }
    }
}
