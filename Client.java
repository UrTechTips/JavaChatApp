import java.io.*;
import java.net.*;
import java.util.Scanner;
import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;


public class Client {
    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        try (Socket socket = new Socket("localhost", 8080)) {
            Scanner sc = new Scanner(System.in);
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("Welcome to the Client Application!");
            System.out.println("1. Login");
            System.out.println("2. Create Account");
            System.out.print("Choose an option (1 or 2): ");
            int choice = sc.nextInt();
            sc.nextLine();

            output.println(choice);

            if (choice == 1) {
                login(sc, output, serverInput);
            } else if (choice == 2) {
                createAccount(sc, output, serverInput);
            } else {
                System.out.println("Invalid choice. Exiting...");
                AnsiConsole.systemUninstall();
                sc.close();
                return;
            }

            System.out.println(ansi().eraseScreen() + "You are now logged in!");
            System.out.println("Enter a message to send to the server (type 'exit' to quit):");

            Thread listener = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = serverInput.readLine()) != null) {
                        System.out.println(ansi().render(serverMessage));
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                    System.exit(0);
                }
            });
            listener.start();

            while (true) {
                String message = sc.nextLine();
                if (message.equalsIgnoreCase("exit")) {
                    System.out.println("Goodbye!");
                    break;
                }
                output.println(message);
            }
            AnsiConsole.systemUninstall();
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void login(Scanner sc, PrintWriter output, BufferedReader serverInput) throws IOException {
        System.out.println("\n--- Login ---");
        System.out.print("Enter Username: ");
        String username = sc.nextLine();
        output.println(username);

        System.out.print("Enter Password: ");
        String password = sc.nextLine();
        output.println(password);

        String loginResponse = serverInput.readLine();
        System.out.println(loginResponse);
        if (!loginResponse.equals("SUCCESS")) {
            System.out.println("Login failed: " + loginResponse + "\n");
            System.exit(1); // Exit on failed login
        }
    }

    private static void createAccount(Scanner sc, PrintWriter output, BufferedReader serverInput) throws IOException {
        System.out.println("\n--- Create Account ---");
        System.out.print("Enter Username: ");
        String username = sc.nextLine();
        output.println(username);

        System.out.print("Enter Display Name: ");
        String displayName = sc.nextLine();
        output.println(displayName);

        System.out.print("Enter Password: ");
        String password = sc.nextLine();
        output.println(password);

        String accountResponse = serverInput.readLine();
        if (accountResponse.equals("ACCOUNT_CREATED")) {
            System.out.println("Account created successfully! You can now log in.\n");
        } else {
            System.out.println("Account creation failed: " + accountResponse + "\n");
            System.exit(1);
        }
    }
}
