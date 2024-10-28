package cobacoba.tugas;

// Client.java
import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class client {
    public static void main(String[] args) {
        String serverAddress = "localhost";
        int port = 5000;

        try (Socket socket = new Socket(serverAddress, port);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            String command = "open_notepad"; // Perintah yang ingin dikirim ke server
            String commandHash = generateSHA3(command);

            writer.println(command);
            writer.println(commandHash);

            System.out.println("Command and hash sent to server.");

        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        }
    }

    private static String generateSHA3(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA3-256");
            byte[] hash = digest.digest(data.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("SHA-3 algorithm not found: " + e.getMessage());
            return null;
        }
    }
}
