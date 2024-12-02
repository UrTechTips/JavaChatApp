import java.sql.Connection;
import java.sql.Statement;

public class DatabaseSetup {
    public static void setupDatabase() {
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                hashed_password TEXT NOT NULL,
                salt TEXT NOT NULL
            );
        """;

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            System.out.println("Tables created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
