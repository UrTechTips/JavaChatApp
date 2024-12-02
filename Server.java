import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

public class Server {
    private static final int PORT = 8080;
    private static final CopyOnWriteArraySet<ClientHandler> clientHandlers = new CopyOnWriteArraySet<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is waiting for clients to connect...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected!");

                ClientHandler clientHandler = new ClientHandler(clientSocket);
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
        String username;

        public ClientHandler(Socket socket) throws IOException {
            this.clientSocket = socket;
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }

        @Override
        public void run() {
            try {
                // Receive menu choice
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
                    if (message.startsWith("/pm") && message.length() > 4) {
                        String[] arr = message.substring(4).split(" ", 2);
                
                        if (arr.length == 2) {
                            String broadcastMessage = "[" + username + " whispers to you]: " + arr[1];
                            if (!privateMessage(arr[0], broadcastMessage)) {
                                out.println("Invalid Username.");
                            }
                        } else {
                            out.println("Invalid /pm format. Use: /pm <username> <message>");
                        }
                    } else if (message.startsWith("/pm")) {
                        out.println("Invalid /pm format. Use: /pm <username> <message>");
                    } else {
                        String broadcastMessage = "[] @|red " + username + "|@: " + message;
                        System.out.println("Broadcasting: " + broadcastMessage);
                        broadcast(broadcastMessage);
                    }
                }
                
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                cleanup();
            }
        }

        private void handleLogin() throws IOException {
            username = in.readLine();
            String password = in.readLine();

            try {
                if (Users.login(username, password)) {
                    out.println("SUCCESS");
                    System.out.println(username + " logged in successfully!");
                } else {
                    out.println("FAILURE: Invalid Username or Password.");
                    clientSocket.close();
                }
            } catch (Exception e) {
                out.println("FAILURE: " + e.getMessage());
                clientSocket.close();
            }
        }

        private void handleAccountCreation() throws IOException {
            username = in.readLine();
            String displayName = in.readLine();
            String password = in.readLine();

            try {
                if (Users.createUser(username, displayName, password)) {
                    out.println("ACCOUNT_CREATED");
                    System.out.println("Account created for " + username);
                } else {
                    out.println("FAILURE: Account creation failed. Username might already exist.");
                    clientSocket.close();
                }
            } catch (Exception e) {
                out.println("FAILURE: " + e.getMessage());
                clientSocket.close();
            }
        }

        private void broadcast(String message) {
            for (ClientHandler client : clientHandlers) {
                try {
                    if (!client.username.equals(username)) {
                        client.out.println(message);
                    }
                } catch (Exception e) {
                    System.err.println("Failed to send message to a client. Removing client from list.");
                    clientHandlers.remove(client);
                }
            }
        }

        private boolean privateMessage(String user, String message) {
            System.out.println("Private Message to Username: " + user);
            for (ClientHandler client : clientHandlers) {
                try {
                    String usern = client.username;
                    System.out.println("User: " + usern);
                    if (usern.equals(user)) {
                        client.out.println(message);
                        return true;
                    } 
                } catch (Exception e) {
                    System.err.println("Failed to send message to a client. Removing client from list.");
                    clientHandlers.remove(client);
                }
            }
            return false;
        }

        private void cleanup() {
            try {
                clientHandlers.remove(this);
                clientSocket.close();
                System.out.println("Client disconnected.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}