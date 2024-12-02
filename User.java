import java.util.Random;

public class User {
    String username;
    String displayname;
    String color;

    User(String username, String displayname, String color) {
        this.username = username;
        this.displayname = displayname;

        this.color = color;
    }

    User(String username, String color) {
        this.username = username;
        this.displayname = username;
        this.color = color;
    }
}
