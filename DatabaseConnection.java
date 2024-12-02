import java.sql.*;

public class DatabaseConnection {
    // Database connection URL
    private static final String DB_URL = "jdbc:sqlite:chat_application.db";
    
    public static Connection connect() {
        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            // Establish the connection to the database
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            return null;
        }
    }
}
