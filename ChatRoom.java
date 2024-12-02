import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatRoom {
    private static final AtomicInteger idCounter = new AtomicInteger(1);
    public final int id;
    public String roomName;
    private final LinkedList<User> users;
    private final LinkedList<Message> messages;

    ChatRoom() {
        this.id = idCounter.getAndIncrement();
        this.users = new LinkedList<>();
        this.messages = new LinkedList<>();
    }

    ChatRoom(String name) {
        this.roomName = name;
        this.id = idCounter.getAndIncrement();
        this.users = new LinkedList<>();
        this.messages = new LinkedList<>();
    }

    public void addUser(User newUser) {
        users.add(newUser);
    }

    public void removeUser(User newUser) {
        users.remove(newUser);
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public boolean getHistory() {
        String filePath = "history/" + id + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Message message : messages) {
                writer.write(message.toString());
                writer.newLine();
            }
            System.out.println("Messages saved successfully to " + filePath);
            return true;
        } catch (IOException e) {
            System.err.println("Error writing messages to file: " + e.getMessage());
        }
        return false;
    }

    public boolean inChatRoom(User user) {
        return users.contains(user);
    }
}
