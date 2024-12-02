import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtils {
    public static String getSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt); 
    }

    // Hash the password with the salt
    public static String hashPassword(String password, String salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        String saltedPassword = salt + password; 
        byte[] hashedBytes = md.digest(saltedPassword.getBytes()); 
        return Base64.getEncoder().encodeToString(hashedBytes); 
    }

    // Verify the password
    public static boolean verifyPassword(String inputPassword, String storedHash, String salt) throws NoSuchAlgorithmException {
        String hashedInput = hashPassword(inputPassword, salt); 
        return hashedInput.equals(storedHash); 
    }
}
