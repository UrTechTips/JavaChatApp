import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

public class Server {
    private static final List<ChatRoom> rooms = Collections.synchronizedList(new LinkedList<>());
    private static final int PORT = 8080;
    private static final CopyOnWriteArraySet<ClientHandler> clientHandlers = new CopyOnWriteArraySet<>();

    public static void main(String[] args) {
        rooms.add(new ChatRoom("Default Room"));
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is waiting for clients to connect...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected!");

                ClientHandler clientHandler = new ClientHandler(clientSocket, rooms);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        final Socket clientSocket;
        final PrintWriter out;
        final BufferedReader in;
        User usr;
        List<ChatRoom> rooms;
        ChatRoom cr;

        public ClientHandler(Socket socket, List<ChatRoom> rooms) throws IOException {
            this.clientSocket = socket;
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.rooms = rooms;
            this.cr = rooms.get(0);
        }

        @Override
        public void run() {
            try {
                int choice = Integer.parseInt(in.readLine());

                if (choice == 1) {
                    handleLogin();
                } else if (choice == 2) {
                    handleAccountCreation();
                } else {
                    out.println("Invalid choice. Disconnecting...");
                    clientSocket.close();
                    return;
                }

                String message;
                while ((message = in.readLine()) != null) {
                    processCommand(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cleanup();
            }
        }

        private void processCommand(String message) {
            try {
                if (message.startsWith("/pm")) {
                    processPrivateMessage(message);
                } else if (message.startsWith("/getHistory")) {
                    processGetHistory();
                } else if (message.startsWith("/join")) {
                    processJoinRoom(message);
                } else if (message.startsWith("/create")) {
                    processCreateRoom(message);
                } else {
                    Message msg = new Message(usr, message);
                    cr.addMessage(msg);
                    broadcast(msg.printString());
                }
            } catch (Exception e) {
                out.println("Error processing command: " + e.getMessage());
            }
        }

        private void processPrivateMessage(String message) {
            String[] arr = message.substring(4).split(" ", 2);
            if (arr.length == 2) {
                String broadcastMessage = "@|white [" + usr.username + " whispers to you]|@: " + arr[1];
                if (!privateMessage(arr[0], broadcastMessage)) {
                    out.println("Invalid Username.");
                }
            } else {
                out.println("Invalid /pm format. Use: /pm <username> <message>");
            }
        }

        private void processGetHistory() {
            if (cr.getHistory()) {
                out.println("History Saved");
            } else {
                out.println("Unable to Save History.");
            }
        }

        private void processJoinRoom(String message) {
            int roomId = Integer.parseInt(message.substring(6).trim());
            if (changeRoom(roomId)) {
                out.println("Joined Room " + roomId);
            } else {
                out.println("Unable to join room " + roomId);
            }
        }

        private void processCreateRoom(String message) {
            String roomName = message.substring(8).trim();
            ChatRoom newRoom = new ChatRoom(roomName);
            rooms.add(newRoom);
            changeRoom(newRoom.id);
            out.println("Created and joined Room ID: " + newRoom.id);
        }

        private boolean changeRoom(int id) {
            synchronized (rooms) {
                for (ChatRoom room : rooms) {
                    if (room.id == id) {
                        room.addUser(usr);
                        cr.removeUser(usr);
                        cr = room;
                        return true;
                    }
                }
            }
            return false;
        }

        private void handleLogin() throws IOException {
            try {
                String username = in.readLine();
                String password = in.readLine();
        
                // Validate user login
                if (Authentication.login(username, password)) {
                    usr = new User(username, getRandomColor());
                    cr.addUser(usr);
                    out.println("SUCCESS");
                    System.out.println(username + " logged in successfully!");
                } else {
                    out.println("FAILURE: Invalid username or password.");
                    clientSocket.close();
                }
            } catch (IOException e) {
                out.println("FAILURE: Unable to process login. " + e.getMessage());
                clientSocket.close();
                throw e; // To ensure cleanup if login fails
            } catch (NoSuchAlgorithmException ee) {
                out.println("FAILURE: Unable to process account creation. " + ee.getMessage());
                clientSocket.close();
            }
        }
        
        private void handleAccountCreation() throws IOException {
            try {
                // Read account creation details
                String username = in.readLine();
                String displayName = in.readLine();
                String password = in.readLine();
        
                // Validate and create user account
                if (Authentication.createUser(username, displayName, password)) {
                    usr = new User(username, displayName, getRandomColor());
                    cr.addUser(usr);
                    out.println("ACCOUNT_CREATED");
                    System.out.println("Account created successfully for " + username);
                } else {
                    out.println("FAILURE: Account creation failed. Username might already exist.");
                    clientSocket.close();
                }
            } catch (IOException e) {
                out.println("FAILURE: Unable to process account creation. " + e.getMessage());
                clientSocket.close();
                throw e; // To ensure cleanup if creation fails
            } catch (NoSuchAlgorithmException ee) {
                out.println("FAILURE: Unable to process account creation. " + ee.getMessage());
                clientSocket.close();

            }
        }

        private String getRandomColor() {
            String[] colors = {"Red","Green","Yellow","Blue","Magenta","Cyan","White"};
            Random random = new Random();
            int randomIndex = random.nextInt(colors.length);
            String randomColor = colors[randomIndex];
            return randomColor;
        }
        
        private void broadcast(String message) {
            for (ClientHandler client : clientHandlers) {
                try {
                    if (cr.inChatRoom(client.usr)) { // Check if the user is in the same chat room
                        client.out.println(message);
                    }
                } catch (Exception e) {
                    System.err.println("Error broadcasting message. Removing client.");
                    clientHandlers.remove(client);
                }
            }
        }
        
        private boolean privateMessage(String username, String message) {
            for (ClientHandler client : clientHandlers) {
                try {
                    if (client.usr.username.equals(username)) {
                        client.out.println(message);
                        return true;
                    }
                } catch (Exception e) {
                    System.err.println("Error sending private message. Removing client.");
                    clientHandlers.remove(client);
                }
            }
            return false; // If no matching user was found
        }
        

        private void cleanup() {
            try {
                clientHandlers.remove(this);
                cr.removeUser(usr);
                clientSocket.close();
                System.out.println("Client disconnected.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
