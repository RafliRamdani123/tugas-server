// Server.java
package cobacoba.tugas;
import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Server {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Server is listening on port 5000...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream())
                )
            ) {
                String receivedCommand = reader.readLine();
                String receivedHash = reader.readLine();
                System.out.println("Received command: " + receivedCommand);
                System.out.println("Received hash: " + receivedHash);

                if (verifySHA3(receivedCommand, receivedHash)) {
                    System.out.println("Hash verification successful.");
                    executeCommand(receivedCommand);
                } else {
                    System.out.println("Hash verification failed. Command will not be executed.");
                }
            } catch (IOException e) {
                System.err.println("Error handling client: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                    System.out.println("Client disconnected.");
                } catch (IOException e) {
                    System.err.println("Error closing socket: " + e.getMessage());
                }
            }
        }

        private void executeCommand(String command) {
            try {
                String os = System.getProperty("os.name").toLowerCase();
                if (command.equalsIgnoreCase("shutdown")) {
                    if (os.contains("win")) {
                        Runtime.getRuntime().exec("shutdown -s -t 0");
                    } else if (os.contains("linux") || os.contains("mac")) {
                        Runtime.getRuntime().exec("shutdown -h now");
                    }
                    System.out.println("Shutdown command executed.");
                } else if (command.equalsIgnoreCase("open_notepad")) {
                    if (os.contains("win")) {
                        Runtime.getRuntime().exec("notepad");
                    } else {
                        System.out.println("Notepad not available on this OS.");
                    }
                } else if (command.equalsIgnoreCase("open_browser")) {
                    Runtime.getRuntime().exec("cmd /c start http://www.google.com");
                    System.out.println("Browser opened.");
                } else {
                    Runtime.getRuntime().exec(command);
                    System.out.println("Executed custom command: " + command);
                }
            } catch (IOException e) {
                System.err.println("Error executing command: " + e.getMessage());
            }
        }

        private boolean verifySHA3(String data, String expectedHash) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA3-256");
                byte[] hash = digest.digest(data.getBytes());
                String calculatedHash = Base64.getEncoder().encodeToString(hash);
                return calculatedHash.equals(expectedHash);
            } catch (NoSuchAlgorithmException e) {
                System.err.println("SHA-3 algorithm not found: " + e.getMessage());
                return false;
            }
        }
    }
}
