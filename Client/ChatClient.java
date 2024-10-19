import java.io.*;
import java.net.*;

public class ChatClient {
    private static Socket socket;
    private static BufferedReader in;
    private static PrintWriter out;

    public static void main(String[] args) throws IOException {
        socket = new Socket("localhost", 3003);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

        // Get the clients name
        System.out.println(in.readLine());
        out.println(userInput.readLine());

        // Get the chat room name
        System.out.println(in.readLine());
        out.println(userInput.readLine());

        // Hanlde password
        System.out.println(in.readLine());
        out.println(userInput.readLine());

        // Starting a thread to listen for requests from server
        Thread messageListener = new Thread(() -> {
            try {
                String incomingMessage;
                while ((incomingMessage = in.readLine()) != null) {
                    System.out.println(incomingMessage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        messageListener.start();

        // Read and send user messages
        String userMessage;
        while (!(userMessage = userInput.readLine()).equalsIgnoreCase("exit")) {
            if (userMessage.equalsIgnoreCase("/list")) {
                out.println(userMessage);
            } else {
                out.println(userMessage);
            }
        }

        socket.close(); // Close socket
    }
}
