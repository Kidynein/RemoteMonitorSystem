package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    private static final int PORT = 9999;

    public static void main(String[] args) {
        System.out.println("Server đang khởi động tại cổng " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server đã sẵn sàng! Đang chờ Client kết nối...");

            while (true) {
                Socket clientSocket = serverSocket.accept();

                System.out.println("Có kết nối mới từ: " + clientSocket.getInetAddress());

                ClientHandler handler = new ClientHandler(clientSocket);
                handler.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}