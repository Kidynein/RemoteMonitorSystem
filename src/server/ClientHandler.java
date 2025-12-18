package server;

import common.Message;
import common.MessageType;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler extends Thread {
    private final Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String clientName;
    private boolean isRunning = true;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            while (isRunning) {
                try {
                    Message msg = (Message) in.readObject();
                    System.out.println("-> Nhận từ " + socket.getInetAddress() + ": " + msg);

                    processMessage(msg);

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            System.err.println("Client ngắt kết nối đột ngột: " + clientName);
        } finally {
            closeConnection();
        }
    }

    private void processMessage(Message msg) {
        switch (msg.getType()) {
            case LOGIN:
                this.clientName = msg.getSender();
                ServerManager.addClient(this.clientName, this);
                server.view.ServerFrame.updateClientList(this.clientName, true);

                sendMessage(new Message(MessageType.AUTH_SUCCESS, "Server", "OK"));
                break;

            case FILE_EVENT:
                server.view.ServerFrame.addLog(msg.getSender(), msg.getAction(), msg.getContent());
                System.out.println("LOG: " + msg.getContent());
                break;

            case ERROR:
                String sender = msg.getSender();
                String errorContent = msg.getContent();

                server.view.ServerFrame.addLog(sender, "LỖI", errorContent);

                System.err.println("LỖI TỪ " + sender + ": " + errorContent);

                javax.swing.SwingUtilities.invokeLater(() -> {
                    javax.swing.JOptionPane.showMessageDialog(null,
                            "Client " + sender + " gặp lỗi:\n" + errorContent,
                            "Lỗi Giám sát",
                            javax.swing.JOptionPane.ERROR_MESSAGE);
                });
                break;

            default:
                System.out.println("Tin nhắn không xác định: " + msg.getType());
        }
    }

    public void sendMessage(Message msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        isRunning = false;
        ServerManager.removeClient(clientName);
        if (clientName != null) {
            server.view.ServerFrame.updateClientList(clientName, false);
        }
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}