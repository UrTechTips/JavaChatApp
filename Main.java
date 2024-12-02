public class Main {
    public static void main(String[] args) throws Exception {
        // User user = new User("STron", "STron", "stron123", false);
        if (User.login("STron", "stron123")) {
            System.out.println("LoggedIn");
        }
    }
}
