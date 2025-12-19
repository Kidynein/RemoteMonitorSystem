package client;

import common.Message;
import common.MessageType;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;
import client.view.ClientFrame;

public class ClientMain {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 9999;
    private DirectoryWatcher currentWatcher;
    private String clientName;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean isRunning = true;
    private ClientFrame view;

    public ClientMain(ClientFrame view) {
        this.view = view;
    }

    public String getClientName() {
        return clientName;
    }

    public void connect(String ip, int port) {
        new Thread(() -> {
            try {
                view.updateStatus("Đang kết nối tới " + ip + "...");
                socket = new Socket(ip, port);
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                // Logic tạo tên
                String hostName = InetAddress.getLocalHost().getHostName();
                int randomId = new Random().nextInt(1000);
                this.clientName = hostName + "-" + randomId;

                // Gửi Login
                sendMessage(new Message(MessageType.LOGIN, clientName, "Xin chào"));

                // Cập nhật giao diện: Đã kết nối
                view.updateStatus("Đã kết nối! ID: " + clientName);
                view.setConnectedState(true); // Khóa nút Connect, mở nút Disconnect

                startListening();

            } catch (IOException e) {
                view.updateStatus("Lỗi kết nối: " + e.getMessage());
                view.setConnectedState(false);
            }
        }).start();
    }
    public void disconnect() {
        isRunning = false;
        try {
            if (currentWatcher != null) currentWatcher.stopWatching();
            if (socket != null) socket.close();
            view.updateStatus("Đã ngắt kết nối.");
            view.setConnectedState(false);
        } catch (IOException e) { e.printStackTrace(); }
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
                                view.updateMonitoringPath(path);
                                sendMessage(new Message(MessageType.FILE_EVENT, clientName, path, "MONITOR_STARTED"));
                            } catch (IOException e) {
                                System.err.println("Lỗi: Thư mục không tồn tại " + path);
                                String errorMsg = "Thư mục không tồn tại hoặc không thể truy cập: " + path;
                                sendMessage(new Message(MessageType.ERROR, clientName, errorMsg));
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
}