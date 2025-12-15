package server;

import common.Message;
import java.util.concurrent.ConcurrentHashMap;

public class ServerManager {
    // Key: Tên Client, Value: Đối tượng ClientHandler quản lý client đó
    private static final ConcurrentHashMap<String, ClientHandler> clients = new ConcurrentHashMap<>();

    public static void addClient(String name, ClientHandler handler) {
        clients.put(name, handler);
        System.out.println("[MANAGER] Client mới đã thêm vào danh sách: " + name);
        System.out.println("[MANAGER] Tổng số client: " + clients.size());
    }

    public static void removeClient(String name) {
        if (name != null && clients.containsKey(name)) {
            clients.remove(name);
            System.out.println("[MANAGER] Đã xóa client: " + name);
        }
    }

    public static void sendToClient(String clientName, Message msg) {
        ClientHandler handler = clients.get(clientName);
        if (handler != null) {
            handler.sendMessage(msg);
        }
    }
}