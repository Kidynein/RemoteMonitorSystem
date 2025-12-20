package server.view;

import common.Message;
import common.MessageType;
import server.ServerManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ServerFrame extends JFrame {
    // Singleton pattern
    private static ServerFrame instance;

    private JList<String> clientList;
    private DefaultListModel<String> listModel;
    private LogPanel logPanel;

    public ServerFrame() {
        instance = this;
        setupUI();
    }

    public static void updateClientList(String clientName, boolean isAdding) {
        if (instance != null) instance.doUpdateClientList(clientName, isAdding);
    }

    public static void addLog(String client, String action, String path) {
        if (instance != null) instance.logPanel.addLog(client, action, path);
    }

    private void setupUI() {
        setTitle("Hệ thống Giám sát Tập tin Từ xa (Server)");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel trái: Danh sách Client
        listModel = new DefaultListModel<>();
        clientList = new JList<>(listModel);
        JScrollPane listScroll = new JScrollPane(clientList);
        listScroll.setPreferredSize(new Dimension(200, 0));
        listScroll.setBorder(BorderFactory.createTitledBorder("Danh sách Client"));

        // MENU CHUỘT PHẢI
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem monitorItem = new JMenuItem("Giám sát thư mục...");
        popupMenu.add(monitorItem);
        JMenuItem stopItem = new JMenuItem("Ngừng giám sát");
        popupMenu.add(stopItem);

        // Xử lý sự kiện click chuột phải
        clientList.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = clientList.locationToIndex(e.getPoint());
                    clientList.setSelectedIndex(row); // Chọn dòng được click
                    if (row != -1) {
                        popupMenu.show(clientList, e.getX(), e.getY());
                    }
                }
            }
        });
        stopItem.addActionListener(e -> {
            String selectedClient = clientList.getSelectedValue();
            if (selectedClient != null) {
                Message msg = new Message(MessageType.STOP_WATCH, "Server", "STOP");
                ServerManager.sendToClient(selectedClient, msg);

                // Ghi log báo cáo
                logPanel.addLog("Server", "CMD_SEND", "Yêu cầu DỪNG giám sát -> " + selectedClient);
            }
        });

        // Xử lý sự kiện khi chọn menu "Giám sát..."
        monitorItem.addActionListener(e -> {
            String selectedClient = clientList.getSelectedValue();
            if (selectedClient != null) {
                showMonitorDialog(selectedClient);
            }
        });

        // Panel phải: Log
        logPanel = new LogPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScroll, logPanel);
        add(splitPane, BorderLayout.CENTER);
    }

    // Logic hiển thị hộp thoại nhập đường dẫn
    private void showMonitorDialog(String clientName) {
        String path = JOptionPane.showInputDialog(this,
                "Nhập đường dẫn thư mục muốn giám sát trên máy " + clientName + ":\n(Ví dụ: D:/Data hoặc C:\\Users\\Name\\Desktop)",
                "Cấu hình giám sát",
                JOptionPane.QUESTION_MESSAGE);

        if (path != null && !path.trim().isEmpty()) {
            Message msg = new Message(MessageType.START_WATCH, "Server", path.trim());
            ServerManager.sendToClient(clientName, msg);

            logPanel.addLog("Server", "CMD_SEND", "Yêu cầu giám sát: " + path + " -> " + clientName);
        }
    }

    // Cập nhật danh sách Client
    private void doUpdateClientList(String clientName, boolean isAdding) {
        SwingUtilities.invokeLater(() -> {
            if (isAdding) {
                if (!listModel.contains(clientName)) listModel.addElement(clientName);
            } else {
                listModel.removeElement(clientName);
            }
        });
    }
}