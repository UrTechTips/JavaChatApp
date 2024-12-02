
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Authentication {
    public static boolean createUser(String userName, String displayName, String password)  throws NoSuchAlgorithmException{
        String salt = PasswordUtils.getSalt();
        String encryptedPassword = PasswordUtils.hashPassword(password, salt);
        
        String sql = "INSERT INTO users(username, hashed_password, salt) VALUES(?, ?, ?)";
        System.out.println(encryptedPassword);
        System.out.println(salt);

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName);
            pstmt.setString(2, encryptedPassword);
            pstmt.setString(3, salt);
            pstmt.executeUpdate();
            System.out.println("User registered successfully.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public static boolean login(String username, String password) throws NoSuchAlgorithmException {
        String sql = "SELECT hashed_password, salt FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("hashed_password");
                String salt = rs.getString("salt");
                
                return PasswordUtils.verifyPassword(password, storedHash, salt);
            } else {
                System.out.println("User not found.");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // public void logout() throws AuthenticationError {
    //     if (!isOnline) {
    //         throw new AuthenticationError("Invalid Logout Call");
    //     } 
    //     isOnline = false;
    // }
}

class AuthenticationError extends Exception {
    AuthenticationError( String message) {
        super(message);
    }
}