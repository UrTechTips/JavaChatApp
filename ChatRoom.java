
import java.util.LinkedList;

public class ChatRoom {
    public String roomName;
    private LinkedList<User> users;
    private LinkedList<Message> messages;

    public void addUser(User newUser) throws UserAlreadyExistsException {
        if (users.contains(newUser)) {
            throw new UserAlreadyExistsException(newUser, this);
        } 
        users.add(newUser);
        // messages.add(newUser.displayName + "is added to the room.");
    }
}

class UserAlreadyExistsException extends Exception {
    UserAlreadyExistsException(User newUser, ChatRoom room) {
        super("User: " + newUser.displayname + " already exists in ChatRoom " + room.roomName);
    }
}