# Java Chat App

The Java Chat App is a real-time, multi-user chatting platform developed in Java. Designed for seamless communication, it offers secure user authentication, private and broadcast messaging, and a vibrant, interactive interface enhanced by external libraries.

## File Structure

```
project-folder/
│ ├── lib/
│ ├── jansi-2.4.0.jar
| └── sqlite-jdbc-3.47.1.0.jar
├── Authentication.java
├── ChatRoom.java
├── Client.java
├── DatabaseConnection.java
├── Message.java
├── PasswordUtils.java
├── Server.java
├── User.java
├── chat_application.db
└── README.md
```

## How to Run

1. **Clone the Repository**:
    ```bash
    git clone https://github.com/UrTechTips/javaJavaChatApp.git
    cd JavaChatApp
    ```
2. **Compile the Java Files:** Ensure that the JAR files are in the Lib/ directory. Use the following command to compile the source code:

    ```bash
    javac Server.java
    javac -cp ".;lib\jansi-2.4.0.jar" Client.java
    ```

3. **Run the Project:** Use the following command to execute the project:
    - _Run the Server_:
    ```bash
    java -cp ".;lib/*" Server
    ```
    - _Run the clients_:
    ```bash
    java -cp ".;lib/*" Client
    ```

You can run as many clients as you want. But you need to run server only once.

## Dependencies

This project uses the following external library:

-   _Jansi 2.4.0:_ For terminal color output.
-   _SQLite JDBC 3.47.1.0:_: For connection with SQL Database.

## Features

-   Authentication: Allows users to create accounts and login to their accounts from any client.
-   Realtime Chatting: Allows all the users currently loggedin to communicate with each other in real time in a chatRoom.
-   Private Messaging: Allows users to send private messages to other users using `\pm` command.

## License

This project is licensed under the MIT License.
