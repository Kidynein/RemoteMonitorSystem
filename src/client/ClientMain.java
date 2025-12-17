package client;

import common.Message;
import common.MessageType;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;

public class ClientMain {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 9999;
    private DirectoryWatcher currentWatcher;
    private String clientName;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean isRunning = true;

    public String getClientName() {
        return clientName;
    }

    public void connect() {
        try {
            System.out.println("Đang kết nối tới Server " + SERVER_IP + ":" + SERVER_PORT + "...");
            socket = new Socket(SERVER_IP, SERVER_PORT);

            // Tạo luồng gửi/nhận
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            System.out.println("-> Đã kết nối thành công!");

            // Gửi tin nhắn đăng nhập ngay khi kết nối
            // Lấy tên máy tính tự động
            String hostName = InetAddress.getLocalHost().getHostName();
            int randomId = new Random().nextInt(1000);
            this.clientName = hostName + "-" + randomId;
            Message loginMsg = new Message(MessageType.LOGIN, clientName, "Xin chào Server");
            sendMessage(loginMsg);

            // Bắt đầu lắng nghe lệnh từ Server
            startListening();

        } catch (IOException e) {
            System.err.println("Không thể kết nối tới Server. Hãy chắc chắn Server đã bật!");
        }
    }

    private void startListening() {
        // Tạo một Thread mới chỉ để ngồi nghe Server nói gì
        Thread listenerThread = new Thread(() -> {
            try {
                while (isRunning) {
                    Message msg = (Message) in.readObject();
                    System.out.println("<- Server nói: " + msg.getType() + " - " + msg.getContent());

                    switch (msg.getType()) {
                        case START_WATCH:
                            // msg.getContent() sẽ chứa đường dẫn thư mục cần giám sát
                            String path = msg.getContent();

                            // Nếu đang giám sát cái khác thì dừng lại trước
                            if (currentWatcher != null) {
                                currentWatcher.stopWatching();
                            }

                            try {
                                // Tạo và chạy watcher mới
                                currentWatcher = new DirectoryWatcher(this, path);
                                currentWatcher.start();
                                System.out.println("-> Đã kích hoạt giám sát thư mục: " + path);
                            } catch (IOException e) {
                                sendMessage(new Message(MessageType.ERROR, clientName, "Lỗi không thể giám sát: " + e.getMessage()));
                            }
                            break;

                        case STOP_WATCH:
                            if (currentWatcher != null) {
                                currentWatcher.stopWatching();
                                currentWatcher = null;
                                System.out.println("-> Đã dừng giám sát.");
                            }
                            break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Mất kết nối với Server.");
            } finally {
                close();
            }
        });
        listenerThread.start();
    }

    public void sendMessage(Message msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close() {
        isRunning = false;
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ClientMain().connect();
    }
}