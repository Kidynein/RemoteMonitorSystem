package server;

import server.view.ServerFrame;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    private static final int PORT = 9999;

    public static void main(String[] args) {
        // 1. Bật giao diện
        SwingUtilities.invokeLater(() -> {
            new ServerFrame().setVisible(true);
        });

        // 2. Chạy Server Socket
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("Server đã sẵn sàng...");
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    ClientHandler handler = new ClientHandler(clientSocket);
                    handler.start();
                }
            } catch (IOException e) { e.printStackTrace(); }
        }).start();
    }
}