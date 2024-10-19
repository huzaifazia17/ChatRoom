import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChatServer {
    private static Map<String, List<ClientHandler>> chatRooms = new HashMap<>();
    private static Map<String, String> chatRoomPasswords = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(3003);
        System.out.println("The Chat Server has started on port 3003:");

        while (true) {
            // Accept new clients
            Socket socket = serverSocket.accept();
            // Handle each client on a separate thread
            new ClientHandler(socket).start();
        }
    }

    // A class for handling multiple different clients using multi threading
    static class ClientHandler extends Thread {
        private Socket socket;
        private String chatRoom;
        private String clientName;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                out.println("Please enter your name:");
                clientName = in.readLine();

                out.println("Enter chat room name:");
                chatRoom = in.readLine();

                // Check if the room exists
                if (chatRoomPasswords.containsKey(chatRoom)) {
                    out.println("Please enter the password for the chat room: " + chatRoom);
                    String inputPassword = in.readLine();
                    if (!chatRoomPasswords.get(chatRoom).equals(inputPassword)) {
                        out.println("Incorrect password. Connection closed.");
                        // close connection if the wrong password is entered
                        socket.close();
                        return;
                    }
                } else {
                    // Set new pass for new room
                    out.println("Please create a password for the chat room:");
                    String newPassword = in.readLine();
                    chatRoomPasswords.put(chatRoom, newPassword);
                    out.println("The chat room has been created with password.");
                }

                synchronized (chatRooms) {
                    chatRooms.putIfAbsent(chatRoom, new ArrayList<>());
                    chatRooms.get(chatRoom).add(this);
                }

                out.println("\nWelcome to " + chatRoom + ", " + clientName
                        + "! Please take note of the following instructions:\n 1. Type 'exit' to leave. \n 2. To see the current list of users in you party type /list and hit enter!");
                broadcastMessage(chatRoom, clientName + " has joined the chat.");

                String message;
                while (!(message = in.readLine()).equalsIgnoreCase("exit")) {
                    if (message.equalsIgnoreCase("/list")) {
                        sendActiveUsersList(); // Send the list of active users
                    } else {
                        broadcastMessage(chatRoom, formatMessageWithTimestamp(clientName, message));
                    }
                }
                leaveChatRoom();
                broadcastMessage(chatRoom, clientName + " has left the chat.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Broadcast message function
        private void broadcastMessage(String room, String message) {
            synchronized (chatRooms) {
                for (ClientHandler client : chatRooms.get(room)) {
                    client.out.println(message);
                }
            }
        }

        // Function to get list of active users in the chat room and print it
        private void sendActiveUsersList() {
            synchronized (chatRooms) {
                List<ClientHandler> clients = chatRooms.get(chatRoom);
                StringBuilder userList = new StringBuilder("Active users: ");
                for (ClientHandler client : clients) {
                    userList.append(client.clientName).append(", ");
                }
                if (userList.length() > 13) {
                    userList.setLength(userList.length() - 2);
                }
                out.println(userList.toString());
            }
        }

        // Remove the client after exit
        private void leaveChatRoom() {
            synchronized (chatRooms) {
                chatRooms.get(chatRoom).remove(this);
                if (chatRooms.get(chatRoom).isEmpty()) {
                    chatRooms.remove(chatRoom);
                    chatRoomPasswords.remove(chatRoom);
                }
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // function to format the message to include timestamp and client name
        private String formatMessageWithTimestamp(String clientName, String message) {
            String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
            return clientName + " [" + timestamp + "]: " + message;
        }
    }
}
